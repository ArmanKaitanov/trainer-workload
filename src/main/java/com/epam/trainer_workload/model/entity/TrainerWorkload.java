package com.epam.trainer_workload.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "trainer_workload")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TrainerWorkload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "trainer_username", nullable = false)
    private String trainerUsername;

    @NonNull
    @Column(name = "trainer_first_name", nullable = false)
    private String trainerFirstname;

    @NonNull
    @Column(name = "trainer_last_name", nullable = false)
    private String trainerLastname;

    @NonNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

//    @NonNull
//    @Column(name = "training_date", nullable = false)
//    private LocalDate trainingDate;
//
//    @NonNull
//    @Column(name = "training_duration", nullable = false)
//    private Integer trainingDuration;

    @OneToMany(mappedBy = "trainerWorkload", cascade = CascadeType.ALL, orphanRemoval = true)
//    @ToString.Exclude
    private List<WorkloadYear> workloadYears;
}
