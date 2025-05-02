package com.pm.patient_service.dto;

import java.time.LocalDate;

public record PatientResponseDTO(String firstName, String lastName, String email, LocalDate registeredDate) {
}
