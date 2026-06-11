package com.sagar.hms.patient;

import jakarta.validation.constraints.*;

public final class PatientDto {
    private PatientDto() {
    }

    public record PatientRequest(
            @NotBlank String name,
            @NotNull @Positive Integer age,
            @NotBlank String gender,
            @NotBlank String phoneNumber,
            @NotBlank @Email String email,
            @NotBlank String address
    ) {
    }

    public record PatientResponse(
            Long id,
            String name,
            Integer age,
            String gender,
            String phoneNumber,
            String email,
            String address
    ) {
    }
}
