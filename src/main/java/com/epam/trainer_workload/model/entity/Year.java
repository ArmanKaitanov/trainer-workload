package com.epam.trainer_workload.model.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Year {

    private int year;

    private List<Month> months;
}