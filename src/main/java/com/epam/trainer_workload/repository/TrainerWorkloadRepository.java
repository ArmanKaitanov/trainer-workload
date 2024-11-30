package com.epam.trainer_workload.repository;

import com.epam.trainer_workload.model.entity.TrainerWorkload;
import com.epam.trainer_workload.model.entity.WorkloadYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TrainerWorkloadRepository extends JpaRepository<TrainerWorkload, Long> {

    Optional<TrainerWorkload> findByTrainerUsername(String trainerUsername);
}