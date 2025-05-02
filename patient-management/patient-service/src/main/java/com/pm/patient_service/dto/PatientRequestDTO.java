package com.pm.patient_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PatientRequestDTO(@NotBlank @Size(max=100, message = "First name cannot be more than 100") String firstName, @NotBlank @Size(max=100, message = "Last name cannot be more than 100") String lastName, @NotBlank @Email String email, @NotBlank String dateOfBirth) {

}
