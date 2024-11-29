package com.epam.trainer_workload.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "workload_years")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class WorkloadYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "year_value", nullable = false)
    private int year;

    @ManyToOne
    @JoinColumn(name = "trainer_workload_id")
//    @ToString.Exclude
    private TrainerWorkload trainerWorkload;

    @OneToMany(mappedBy = "workloadYear", cascade = CascadeType.ALL, orphanRemoval = true)
//    @ToString.Exclude
    private List<WorkloadMonth> workloadMonths;
}