package com.epam.trainer_workload.dto.request;

import com.epam.trainer_workload.deserializer.CustomActionTypeDeserializer;
import com.epam.trainer_workload.deserializer.CustomBooleanDeserializer;
import com.epam.trainer_workload.model.enumeration.ActionType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TrainerWorkloadUpdateRequestDto {

    @NotBlank(message = "Trainer username is required")
    private String trainerUsername;

    @NotBlank(message = "Trainer first name is required")
    private String trainerFirstName;

    @NotBlank(message = "Trainer last name is required")
    private String trainerLastName;

    @NotNull(message = "Trainer activity status is required")
    @JsonDeserialize(using = CustomBooleanDeserializer.class)
    private Boolean isActive;

    @NotNull(message = "Training date is required")
//    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate trainingDate;

    @NotNull(message = "Training duration is required")
    private Integer trainingDuration;

    @NotNull(message = "Action type is required")
    @JsonDeserialize(using = CustomActionTypeDeserializer.class)
    private ActionType actionType;
}
