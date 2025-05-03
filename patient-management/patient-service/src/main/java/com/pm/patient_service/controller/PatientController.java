package com.pm.patient_service.controller;

import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.dto.validators.AddPatientValidatorGroup;
import com.pm.patient_service.exceptions.PatientNotFoundException;
import com.pm.patient_service.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
// http::localhost:8080/patients
@RequestMapping("/patients")
@Tag(name= "Patient", description = "API for patients management")
public class PatientController {

    //constructor-based DI (or can use @Autowired)
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // http::localhost:8080/patients
    @GetMapping(produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok")})
    @Operation(summary = "Get list of patients")
    public ResponseEntity<List<PatientResponseDTO>> getPatients() {
        List<PatientResponseDTO> patients = patientService.getAllPatients();
        return ResponseEntity.of(Optional.ofNullable(patients));
    }

    // http::localhost:8080/patients/save
    @PostMapping(value = "/save", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Invalid input")})
    @Operation(summary = "Add new patient")
    public ResponseEntity<PatientResponseDTO> addPatient(@Validated({Default.class, AddPatientValidatorGroup.class}) @RequestBody PatientRequestDTO patient) {
        PatientResponseDTO patientResponseDTO = patientService.addPatient(patient);
        return ResponseEntity.of(Optional.ofNullable(patientResponseDTO));
    }

    // http::localhost:8080/patients/update/a1f8f93a-4b5a-47b8-805e-19e9ef8ad01b
    @PutMapping(value = "/update/{id}", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Invalid patient Id"),
            @ApiResponse(responseCode = "404", description = "Patient not found")})
    @Operation(summary = "Update existing patient")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable(name = "id") UUID id, @RequestBody @Valid PatientRequestDTO patientRequestDTO) {
        PatientResponseDTO patientResponseDTO = patientService.updatePatient(id, patientRequestDTO);
        return ResponseEntity.ok().body(patientResponseDTO);
    }

    // http::localhost:8080/patients/delete/a1f8f93a-4b5a-47b8-805e-19e9ef8ad01b
    @DeleteMapping(value = "/delete/{id}", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Invalid patient Id"),
            @ApiResponse(responseCode = "404", description = "Patient not found")})
    @Operation(summary = "Delete existing patient")
    public ResponseEntity<Void> deletePatient(@PathVariable(name="id") UUID id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();

    }
}
