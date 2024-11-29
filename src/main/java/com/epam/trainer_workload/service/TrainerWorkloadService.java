package com.epam.trainer_workload.service;

import com.epam.trainer_workload.dto.request.TrainerWorkloadGetRequestDto;
import com.epam.trainer_workload.dto.request.TrainerWorkloadUpdateRequestDto;
import com.epam.trainer_workload.dto.response.TrainerWorkloadResponseDto;

public interface TrainerWorkloadService {

    void updateWorkload(TrainerWorkloadUpdateRequestDto dto);

    int getWorkload(String trainerUsername, int year, int month);
}
