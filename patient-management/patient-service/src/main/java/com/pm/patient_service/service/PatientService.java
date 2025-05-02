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

    private PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getAllPatients() {
        List<PatientResponseDTO> patientResponse = new ArrayList<>();
        patientRepository.findAll().forEach(patient -> patientResponse.add(new PatientResponseDTO(patient.getFirstName(), patient.getLastName(), patient.getEmail(), patient.getRegisteredDate())));
        return patientResponse;
    }
}
