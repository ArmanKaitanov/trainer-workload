package com.epam.trainer_workload.service;

import com.epam.trainer_workload.dto.request.TrainerWorkloadUpdateRequestDto;

public interface TrainerWorkloadService {

    void updateWorkload(TrainerWorkloadUpdateRequestDto dto);

    int getWorkload(String trainerUsername, int year, int month);
}
