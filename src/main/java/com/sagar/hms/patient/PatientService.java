package com.sagar.hms.patient;

import com.sagar.hms.exception.ResourceNotFoundException;
import com.sagar.hms.exception.ServiceUnavailableException;
import com.sagar.hms.messaging.PatientEventPublisher;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientEventPublisher patientEventPublisher;

    @Transactional
    @CacheEvict(value = {"patients", "patientsSearch"}, allEntries = true)
    public PatientDto.PatientResponse create(PatientDto.PatientRequest request) {
        Patient saved = patientRepository.save(PatientMapper.toEntity(request));
        patientEventPublisher.publishPatientCreated(saved.getPhoneNumber());
        log.info("Created patient id={} phone={}", saved.getId(), saved.getPhoneNumber());
        return PatientMapper.toResponse(saved);
    }

    @Cacheable(value = "patients", key = "'all'")
    public List<PatientDto.PatientResponse> getAllPatients() {
        return patientRepository.findAll().stream().map(PatientMapper::toResponse).toList();
    }

    @Cacheable(value = "patients", key = "#id")
    @CircuitBreaker(name = "patientService", fallbackMethod = "getPatientByIdFallback")
    @Retry(name = "patientService")
    @RateLimiter(name = "patientService")
    public PatientDto.PatientResponse getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        return PatientMapper.toResponse(patient);
    }

    public PatientDto.PatientResponse getPatientByIdFallback(Long id, Throwable throwable) {
        log.error("Fallback triggered for getPatientById id={}", id, throwable);
        throw new ServiceUnavailableException("Patient service is temporarily unavailable for id: " + id);
    }

    @Cacheable(value = "patientsSearch", key = "#name + ':' + #phoneNumber + ':' + #email")
    @CircuitBreaker(name = "patientService", fallbackMethod = "searchFallback")
    @Retry(name = "patientService")
    public List<PatientDto.PatientResponse> search(String name, String phoneNumber, String email) {
        return patientRepository.findAll(PatientSpecifications.withFilters(name, phoneNumber, email))
                .stream()
                .map(PatientMapper::toResponse)
                .toList();
    }

    public List<PatientDto.PatientResponse> searchFallback(String name, String phoneNumber, String email, Throwable throwable) {
        log.error("Fallback triggered for search name={} phoneNumber={} email={}", name, phoneNumber, email, throwable);
        throw new ServiceUnavailableException("Patient search unavailable");
    }

    @Transactional
    @CacheEvict(value = {"patients", "patientsSearch"}, allEntries = true)
    public PatientDto.PatientResponse update(Long id, PatientDto.PatientRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        PatientMapper.updateEntity(patient, request);
        Patient updated = patientRepository.save(patient);
        log.info("Updated patient id={}", id);
        return PatientMapper.toResponse(updated);
    }

    @Transactional
    @CacheEvict(value = {"patients", "patientsSearch"}, allEntries = true)
    public void delete(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        patientRepository.delete(patient);
        patientEventPublisher.publishPatientDeleted(patient.getPhoneNumber());
        log.info("Deleted patient id={} phone={}", id, patient.getPhoneNumber());
    }
}
