package com.pm.patient_service.dto;

import java.time.LocalDate;

public record PatientResponseDTO(String id, String firstName, String lastName, String email, String address, String dateOfBirth) {
}
