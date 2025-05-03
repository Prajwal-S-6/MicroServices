package com.pm.patient_service.dto;

import com.pm.patient_service.dto.validators.AddPatientValidatorGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PatientRequestDTO(
        @NotBlank(message = "First name is required") @Size(max = 100, message = "First name cannot be more than 100") String firstName,
        @NotBlank(message = "Last name is required") @Size(max = 100, message = "Last name cannot be more than 100") String lastName,
        @NotBlank(message = "Email is required") @Email(message = "Invalid email address")  String email,
        @NotBlank(message = "Address is required") String address,
        @NotBlank(message = "DOB is required") String dateOfBirth,
        @NotBlank(groups = AddPatientValidatorGroup.class, message = "Registered date is required") String registeredDate) {

}
