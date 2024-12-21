package com.epam.trainer_workload.model.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Month {

    private int month;

    private int trainingDuration;
}
