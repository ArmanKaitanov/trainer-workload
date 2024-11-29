package com.epam.trainer_workload.service;

import com.epam.trainer_workload.dto.request.TrainerWorkloadUpdateRequestDto;
import com.epam.trainer_workload.model.entity.TrainerWorkload;
import com.epam.trainer_workload.model.entity.WorkloadMonth;
import com.epam.trainer_workload.model.entity.WorkloadYear;
import com.epam.trainer_workload.model.enumeration.ActionType;
import com.epam.trainer_workload.repository.TrainerWorkloadRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
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
                        .workloadYears(new ArrayList<>())
                        .build()
                );


//        List<WorkloadYear> workloadByAllYears = trainerWorkloadRepository.findWorkloadYearsByTrainerUsername(dto.getTrainerUsername());
//        if(workloadByAllYears == null || workloadByAllYears.isEmpty()) {
//            workloadByAllYears = new ArrayList<>();
//        }
//
//        TrainerWorkload workload = TrainerWorkload.builder()
//                .trainerUsername(dto.getTrainerUsername())
//                .trainerFirstname(dto.getTrainerFirstName())
//                        .trainerLastname(dto.getTrainerLastName())
//                        .isActive(dto.getIsActive())
//                        .trainingDate(dto.getTrainingDate())
//                        .trainingDuration(dto.getTrainingDuration())
//                        .workloadYears(workloadByAllYears)
//                        .build();


//        trainerWorkloadRepository.save(trainerWorkload);

        Year year = Year.of(dto.getTrainingDate().getYear());
        int month = dto.getTrainingDate().getMonthValue();
        int duration = dto.getTrainingDuration();

        if (dto.getActionType() == ActionType.ADD) {
            addOrUpdateWorkloadHours(trainerWorkload, year, month, duration);
            trainerWorkloadRepository.save(trainerWorkload);
        } else if (dto.getActionType() == ActionType.DELETE) {
            removeWorkloadHours(trainerWorkload, year, month, duration);
        }

        logger.info("Trainer's {} workload has been successfully updated", dto.getTrainerUsername());
    }

    private void addOrUpdateWorkloadHours(TrainerWorkload trainerWorkload, Year year, int month, int duration) {
        logger.info("Trying to increment trainer's {} workload, value: {}, year: {}, month: {}",
                trainerWorkload.getTrainerUsername(), duration, year, month);

        List<WorkloadYear> workloadByAllYears = trainerWorkload.getWorkloadYears();

        WorkloadYear workloadYear = workloadByAllYears.stream()
                .filter(y -> y.getYear() == year.getValue())
                .findFirst()
                .orElseGet(() -> {
                            WorkloadYear newYear = WorkloadYear.builder()
                                    .year(year.getValue())
                                    .workloadMonths(new ArrayList<>())
                                    .trainerWorkload(trainerWorkload)
//                                    .trainingDuration(0)
                                    .build();
                            workloadByAllYears.add(newYear);
                            logger.info("Added new workload year: {}, trainer: {}", year, trainerWorkload.getTrainerUsername());

                            return newYear;
                        }
                );

        WorkloadMonth workloadMonth = workloadYear.getWorkloadMonths().stream()
                .filter(m -> m.getMonth() == month)
                .findFirst()
                .orElseGet(() -> {
                            WorkloadMonth newMonth = WorkloadMonth.builder()
                                    .month(month)
                                    .trainingDuration(0)
                                    .workloadYear(workloadYear)
                                    .trainerUsername(trainerWorkload.getTrainerUsername())
                                    .year(workloadYear.getYear())
                                    .build();
                            workloadYear.getWorkloadMonths().add(newMonth);
                            logger.info("Added new workload month: {}, year: {}, trainer: {}",
                                    month, year, trainerWorkload.getTrainerUsername());

                            return newMonth;
                        }
                );

        workloadMonth.setTrainingDuration(workloadMonth.getTrainingDuration() + duration);

        logger.info("Updated workload month: {}, year: {}, trainer: {}, new duration: {}",
                month, year, trainerWorkload.getTrainerUsername(), workloadMonth.getTrainingDuration());    }

    private void removeWorkloadHours(TrainerWorkload trainerWorkload, Year year, int month, int duration) {
        logger.info("Trying to decrement trainer's {} workload, value: {}, year: {}, month: {}",
                trainerWorkload.getTrainerUsername(), duration, year, month);

        List<WorkloadYear> workloadByAllYears = trainerWorkload.getWorkloadYears();

        WorkloadYear workloadYear = workloadByAllYears.stream()
                .filter(y -> y.getYear() == year.getValue())
                .findFirst()
                .orElse(null);

        if (workloadYear != null) {
            WorkloadMonth workloadMonth = workloadYear.getWorkloadMonths().stream()
                    .filter(m -> m.getMonth() == month)
                    .findFirst()
                    .orElse(null);

            if (workloadMonth != null) {
                workloadMonth.setTrainingDuration(workloadMonth.getTrainingDuration() - duration);
                logger.info("Removed training hours: {}, month: {}, year: {}, trainer: {}, new duration: {}",
                        duration, month, year, trainerWorkload.getTrainerUsername(), workloadMonth.getTrainingDuration());
            }
        }
    }
}
