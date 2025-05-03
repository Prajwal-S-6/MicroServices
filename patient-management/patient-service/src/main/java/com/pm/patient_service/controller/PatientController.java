package com.pm.patient_service.controller;

import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.dto.validators.AddPatientValidatorGroup;
import com.pm.patient_service.service.PatientService;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/patients")               // http::localhost:8080/patients
public class PatientController {

    //constructor-based DI (or can use @Autowired)
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<PatientResponseDTO>> getPatients() {
        List<PatientResponseDTO> patients = patientService.getAllPatients();
        return ResponseEntity.of(Optional.ofNullable(patients));
    }

    @PostMapping(value = "/save", consumes = "application/json")
    public ResponseEntity<PatientResponseDTO> addPatient(@Validated({Default.class, AddPatientValidatorGroup.class}) @RequestBody PatientRequestDTO patient) {
        PatientResponseDTO patientResponseDTO = patientService.addPatient(patient);
        return ResponseEntity.of(Optional.ofNullable(patientResponseDTO));
    }

    @PutMapping(value = "/update/{id}", consumes = "application/json")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable(name = "id") UUID id, @RequestBody @Valid PatientRequestDTO patientRequestDTO) {
        PatientResponseDTO patientResponseDTO = patientService.updatePatient(id, patientRequestDTO);
        return ResponseEntity.ok().body(patientResponseDTO);
    }

    @DeleteMapping(value = "/delete/{id}", consumes = "application/json")
    public ResponseEntity<Void> deletePatient(@PathVariable(name="id") UUID id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
