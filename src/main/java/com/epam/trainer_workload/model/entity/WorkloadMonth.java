package com.epam.trainer_workload.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "workload_months")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class WorkloadMonth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "month_value", nullable = false)
    private int month;

    @ManyToOne
    @JoinColumn(name = "workload_year_id")
    @ToString.Exclude
    private WorkloadYear workloadYear;

    @Column(name = "training_duration", nullable = false)
    private int trainingDuration;

    @Column(name = "year_value", nullable = false)
    private int year;

    @Column(name = "trainer_username")
    private String trainerUsername;
}
