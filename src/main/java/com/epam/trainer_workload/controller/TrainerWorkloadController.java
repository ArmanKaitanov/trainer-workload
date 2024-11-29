package com.epam.trainer_workload.controller;

import com.epam.trainer_workload.dto.request.TrainerWorkloadUpdateRequestDto;
import com.epam.trainer_workload.service.TrainerWorkloadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trainers/workload")
@RequiredArgsConstructor
@Validated
public class TrainerWorkloadController {

    private final TrainerWorkloadService trainerWorkloadService;

    @PostMapping
    public ResponseEntity<Void> updateWorkload(@Valid @RequestBody TrainerWorkloadUpdateRequestDto dto) {
        trainerWorkloadService.updateWorkload(dto);
        return ResponseEntity.ok().build();
    }
}
