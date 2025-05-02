package com.pm.patient_service.controller;

import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/patients")               // http::localhost:8080/patients
public class PatientController {

    //constructor-based DI (or can use @Autowired)
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<List<PatientResponseDTO>> getPatients() {
        List<PatientResponseDTO> patients = patientService.getAllPatients();
        return ResponseEntity.of(Optional.ofNullable(patients));
    }
}
