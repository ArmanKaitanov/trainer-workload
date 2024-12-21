package com.epam.trainer_workload.service;

import com.epam.trainer_workload.dto.request.TrainerWorkloadUpdateRequestDto;
import com.epam.trainer_workload.model.entity.Month;
import com.epam.trainer_workload.model.entity.TrainerWorkload;
import com.epam.trainer_workload.model.entity.Year;
import com.epam.trainer_workload.model.enumeration.ActionType;
import com.epam.trainer_workload.repository.TrainerWorkloadRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerWorkloadServiceImpl.class);

    private final TrainerWorkloadRepository trainerWorkloadRepository;

    @Override
    @Transactional
    public void updateWorkload(TrainerWorkloadUpdateRequestDto dto) {
        logger.info("Trying to update trainer's workload for trainer with username {}", dto.getTrainerUsername());

        TrainerWorkload trainerWorkload = trainerWorkloadRepository.findByTrainerUsername(dto.getTrainerUsername())
                .orElse(TrainerWorkload.builder()
                        .trainerUsername(dto.getTrainerUsername())
                        .trainerFirstname(dto.getTrainerFirstName())
                        .trainerLastname(dto.getTrainerLastName())
                        .isActive(dto.getIsActive())
                        .years(new ArrayList<>())
                        .build()
                );

        int year = dto.getTrainingDate().getYear();
        int month = dto.getTrainingDate().getMonthValue();
        int duration = dto.getTrainingDuration();

        if (dto.getActionType() == ActionType.ADD) {
            addOrUpdateWorkloadDuration(trainerWorkload, year, month, duration);
        } else if (dto.getActionType() == ActionType.DELETE) {
            removeWorkloadDuration(trainerWorkload, year, month, duration);
        }

        trainerWorkloadRepository.save(trainerWorkload);

        logger.info("Trainer's {} workload has been successfully updated", dto.getTrainerUsername());
    }

    @Override
    public int getWorkload(String trainerUsername, int year, int month) {
        logger.info("Trying to get workload for trainer with username {}", trainerUsername);

        TrainerWorkload trainerWorkload = trainerWorkloadRepository.findByTrainerUsername(trainerUsername)
                .orElseThrow(() -> {
                    logger.info("Trainer with username {} not found", trainerUsername);
                    return new EntityNotFoundException(String.format("Trainer with username %s not found", trainerUsername));
                });

        int duration = trainerWorkload.getYears().stream()
                .filter(workloadYear -> workloadYear.getYear() == year)
                .flatMap(workloadYear -> workloadYear.getMonths().stream())
                .filter(workloadMonth -> workloadMonth.getMonth() == month)
                .map(Month::getTrainingDuration)
                .findAny().orElse(0);

        logger.info("Workload for trainer with username {} has been successfully gotten", trainerUsername);

        return duration;
    }

    private void addOrUpdateWorkloadDuration(TrainerWorkload trainerWorkload, int year, int month, int duration) {
        logger.info("Trying to increment trainer's {} workload, value: {}, year: {}, month: {}",
                trainerWorkload.getTrainerUsername(), duration, year, month);

        List<Year> workloadByAllYears = trainerWorkload.getYears();

        Year workloadYear = workloadByAllYears.stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseGet(() -> {
                            Year newYear = Year.builder()
                                    .year(year)
                                    .months(new ArrayList<>())
                                    .build();
                            workloadByAllYears.add(newYear);
                            logger.info("Added new workload year: {}, trainer: {}", year, trainerWorkload.getTrainerUsername());

                            return newYear;
                        }
                );

        Month workloadMonth = workloadYear.getMonths().stream()
                .filter(m -> m.getMonth() == month)
                .findFirst()
                .orElseGet(() -> {
                            Month newMonth = Month.builder()
                                    .month(month)
                                    .trainingDuration(0)
                                    .build();
                            workloadYear.getMonths().add(newMonth);
                            logger.info("Added new workload month: {}, year: {}, trainer: {}",
                                    month, year, trainerWorkload.getTrainerUsername());

                            return newMonth;
                        }
                );

        workloadMonth.setTrainingDuration(workloadMonth.getTrainingDuration() + duration);

        logger.info("Updated workload month: {}, year: {}, trainer: {}, new duration: {}",
                month, year, trainerWorkload.getTrainerUsername(), workloadMonth.getTrainingDuration());
    }

    private void removeWorkloadDuration(TrainerWorkload trainerWorkload, int year, int month, int duration) {
        logger.info("Trying to decrement trainer's {} workload, value: {}, year: {}, month: {}",
                trainerWorkload.getTrainerUsername(), duration, year, month);

        List<Year> workloadByAllYears = trainerWorkload.getYears();

        Year workloadYear = workloadByAllYears.stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(String.format("Year %s is not found while removing workload process", year)));

        Month workloadMonth = workloadYear.getMonths().stream()
                .filter(m -> m.getMonth() == month)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(String.format("Month %s is not found while removing workload process", month)));

        int newDuration = workloadMonth.getTrainingDuration() - duration;
        workloadMonth.setTrainingDuration(newDuration);
        logger.info("Removed training hours: {}, month: {}, year: {}, trainer: {}, new duration: {}",
                duration, month, year, trainerWorkload.getTrainerUsername(), newDuration);

        if (newDuration == 0) {
            workloadYear.getMonths().remove(workloadMonth);
            logger.info("Removed workload month: {}, year: {}, trainer: {}, because training duration in this month is 0",
                    month, year, trainerWorkload.getTrainerUsername());

            if (workloadYear.getMonths().isEmpty()) {
                workloadByAllYears.remove(workloadYear);
                logger.info("Removed workload year: {}, trainer: {}, because training duration in this year is 0",
                        year, trainerWorkload.getTrainerUsername());
            }
        }
    }
}
