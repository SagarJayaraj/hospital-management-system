package com.sagar.hms.patient;

import com.sagar.hms.messaging.PatientEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientEventPublisher patientEventPublisher;

    @InjectMocks
    private PatientService patientService;

    @Test
    void createShouldPersistAndPublishMessage() {
        PatientDto.PatientRequest request = new PatientDto.PatientRequest(
                "John", 35, "Male", "1234567890", "john@example.com", "Address");

        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient patient = invocation.getArgument(0);
            patient.setId(1L);
            return patient;
        });

        PatientDto.PatientResponse response = patientService.create(request);

        assertEquals(1L, response.id());
        verify(patientRepository, times(1)).save(any(Patient.class));
        verify(patientEventPublisher, times(1)).publishPatientCreated("1234567890");
    }
}
