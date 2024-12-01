package com.epam.trainer_workload.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrainerWorkloadResponseDto {

    private String trainerUsername;

    private int year;

    private int month;

    private int duration;
}
