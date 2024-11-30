package com.epam.trainer_workload.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrainerWorkloadGetRequestDto {

    @NotBlank(message = "Trainer username is required")
    private String trainerUsername;

    @NotNull(message = "Year is required")
    private int year;

    @NotNull(message = "Month is required")
    @Min(value = 1)
    @Max(value = 12)
    private int month;
}
