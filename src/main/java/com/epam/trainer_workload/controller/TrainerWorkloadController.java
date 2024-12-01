package com.epam.trainer_workload.controller;

import com.epam.trainer_workload.dto.request.TrainerWorkloadUpdateRequestDto;
import com.epam.trainer_workload.dto.response.TrainerWorkloadResponseDto;
import com.epam.trainer_workload.service.TrainerWorkloadService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping
    public ResponseEntity<TrainerWorkloadResponseDto> getTrainerWorkload(
            @RequestParam(value = "trainerUsername") String trainerUsername,
            @RequestParam(value = "year") int year,
            @RequestParam(value = "month")
            @Min(value = 1, message = "Month value should be int number from 1 to 12")
            @Max(value = 12, message = "Month value should be int number from 1 to 12")
            int month
            ) {
        int duration = trainerWorkloadService.getWorkload(trainerUsername, year, month);

        return ResponseEntity.ok(TrainerWorkloadResponseDto.builder()
                .trainerUsername(trainerUsername)
                .year(year)
                .month(month)
                .duration(duration)
                .build()
        );
    }
}
