package com.sagar.hms.patient;

public final class PatientMapper {
    private PatientMapper() {
    }

    public static Patient toEntity(PatientDto.PatientRequest request) {
        return Patient.builder()
                .name(request.name())
                .age(request.age())
                .gender(request.gender())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .address(request.address())
                .build();
    }

    public static PatientDto.PatientResponse toResponse(Patient patient) {
        return new PatientDto.PatientResponse(
                patient.getId(),
                patient.getName(),
                patient.getAge(),
                patient.getGender(),
                patient.getPhoneNumber(),
                patient.getEmail(),
                patient.getAddress());
    }

    public static void updateEntity(Patient patient, PatientDto.PatientRequest request) {
        patient.setName(request.name());
        patient.setAge(request.age());
        patient.setGender(request.gender());
        patient.setPhoneNumber(request.phoneNumber());
        patient.setEmail(request.email());
        patient.setAddress(request.address());
    }
}
