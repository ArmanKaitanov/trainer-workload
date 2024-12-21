package com.epam.trainer_workload.model.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "trainers")
@Data
@Builder
public class TrainerWorkload {

    @Id
    private String id;

    @Indexed
    private String trainerUsername;

    private String trainerFirstname;

    private String trainerLastname;

    private Boolean isActive;

    private List<Year> years;
}
