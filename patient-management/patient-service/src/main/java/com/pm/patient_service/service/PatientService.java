package com.pm.patient_service.service;

import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.model.Patient;
import com.pm.patient_service.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PatientService {

    //constructor-based DI (or can use @Autowired)
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getAllPatients() {
        return patientRepository.findAll().stream().map(PatientService::toDTO).toList();
    }

    public static PatientResponseDTO toDTO(Patient patient) {
        return new PatientResponseDTO(patient.getFirstName(), patient.getLastName(), patient.getEmail(), patient.getRegisteredDate().toString());
    }
}
