package com.pm.patient_service.controller;

import com.pm.patient_service.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PatientController {

    private PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/patients")
    public void fetchAllPatients() {
        patientService.getAllPatients().forEach(patient -> System.out.println(patient));
    }
}
