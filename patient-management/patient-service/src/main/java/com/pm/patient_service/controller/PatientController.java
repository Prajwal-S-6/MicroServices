package com.pm.patient_service.controller;

import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/patients")               // http::localhost:8080/patients
public class PatientController {

    //constructor-based DI (or can use @Autowired)
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping(value = "/patients", produces = "application/json")
    public List<PatientResponseDTO> fetchAllPatients() {
        return patientService.getAllPatients();
    }
}
