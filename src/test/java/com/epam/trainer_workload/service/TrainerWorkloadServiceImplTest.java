package com.epam.trainer_workload.service;

import com.epam.trainer_workload.dto.request.TrainerWorkloadUpdateRequestDto;
import com.epam.trainer_workload.model.entity.Month;
import com.epam.trainer_workload.model.entity.TrainerWorkload;
import com.epam.trainer_workload.model.entity.Year;
import com.epam.trainer_workload.model.enumeration.ActionType;
import com.epam.trainer_workload.repository.TrainerWorkloadRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerWorkloadServiceImplTest {

    @Mock
    private TrainerWorkloadRepository trainerWorkloadRepository;

    @InjectMocks
    private TrainerWorkloadServiceImpl trainerWorkloadService;

    private TrainerWorkload trainerWorkload;

    @BeforeEach
    void setUp() {
        Month workloadMonth = Month.builder()
                .month(11)
                .trainingDuration(60)
                .build();

        Year workloadYear = Year.builder()
                .year(2024)
                .months(new ArrayList<>(List.of(workloadMonth)))
                .build();

        trainerWorkload = TrainerWorkload.builder()
                .id("1L")
                .trainerUsername("username")
                .trainerFirstname("John")
                .trainerLastname("Doe")
                .isActive(true)
                .years(new ArrayList<>(List.of(workloadYear)))
                .build();
    }

    @Test
    public void updateWorkload_shouldAddNewWorkloadToExistedYearAndMonth_whenValidRequestProvided() {
        // given
        TrainerWorkloadUpdateRequestDto requestDto =
                getTrainerWorkloadUpdateRequestDto(ActionType.ADD, LocalDate.of(2024, 11, 10));
        Year workloadYear = trainerWorkload.getYears().get(0);
        Month workloadMonth = workloadYear.getMonths().get(0);
        trainerWorkload.getYears().get(0).getMonths().get(0).setTrainingDuration(0);

        when(trainerWorkloadRepository.findByTrainerUsername(requestDto.getTrainerUsername())).thenReturn(Optional.of(trainerWorkload));

        // when
        trainerWorkloadService.updateWorkload(requestDto);

        // then
        verify(trainerWorkloadRepository, times(1)).findByTrainerUsername(requestDto.getTrainerUsername());
        verify(trainerWorkloadRepository, times(1)).save(trainerWorkload);
        assertThat(workloadMonth.getTrainingDuration()).isEqualTo(requestDto.getTrainingDuration());
        assertThat(workloadMonth.getMonth()).isEqualTo(requestDto.getTrainingDate().getMonthValue());
    }

    @Test
    public void updateWorkload_shouldAddNewWorkloadNewYearAndMonth_whenValidRequestProvided() {
        // given
        TrainerWorkloadUpdateRequestDto requestDto =
                getTrainerWorkloadUpdateRequestDto(ActionType.ADD, LocalDate.of(2025, 1, 10));
        int newWorkloadYear = 2025;
        int newWorkloadMonth = 1;

        when(trainerWorkloadRepository.findByTrainerUsername(requestDto.getTrainerUsername())).thenReturn(Optional.of(trainerWorkload));

        // when
        trainerWorkloadService.updateWorkload(requestDto);

        // then
        int expectedWorkloadDuration = trainerWorkload.getYears().stream()
                .filter(wy -> wy.getYear() == newWorkloadYear)
                .flatMap(workloadYear -> workloadYear.getMonths().stream())
                .filter(wm -> wm.getMonth() == newWorkloadMonth)
                .map(Month::getTrainingDuration)
                .findAny()
                .orElse(0);

        verify(trainerWorkloadRepository, times(1)).findByTrainerUsername(requestDto.getTrainerUsername());
        verify(trainerWorkloadRepository, times(1)).save(trainerWorkload);
        assertThat(expectedWorkloadDuration).isNotEqualTo(0);
        assertThat(expectedWorkloadDuration).isEqualTo(requestDto.getTrainingDuration());
    }

    @Test
    public void updateWorkload_shouldRemoveWorkload_whenValidRequestProvided() {
        // given
        TrainerWorkloadUpdateRequestDto requestDto =
                getTrainerWorkloadUpdateRequestDto(ActionType.DELETE, LocalDate.of(2024, 11, 10));
        trainerWorkload.getYears().get(0).getMonths().get(0).setTrainingDuration(180);
        Year workloadYear = trainerWorkload.getYears().get(0);
        Month workloadMonth = workloadYear.getMonths().get(0);

        when(trainerWorkloadRepository.findByTrainerUsername(requestDto.getTrainerUsername())).thenReturn(Optional.of(trainerWorkload));

        // when
        trainerWorkloadService.updateWorkload(requestDto);

        // then
        verify(trainerWorkloadRepository, times(1)).findByTrainerUsername(requestDto.getTrainerUsername());
        assertThat(workloadMonth.getTrainingDuration()).isEqualTo(120);
        assertThat(workloadMonth.getMonth()).isEqualTo(requestDto.getTrainingDate().getMonthValue());
    }

    @Test
    public void updateWorkload_shouldDecrementWorkloadToZeroAndRemoveMonthAndYear_whenValidRequestProvided() {
        // given
        TrainerWorkloadUpdateRequestDto requestDto =
                getTrainerWorkloadUpdateRequestDto(ActionType.DELETE, LocalDate.of(2024, 11, 10));

        when(trainerWorkloadRepository.findByTrainerUsername(requestDto.getTrainerUsername())).thenReturn(Optional.of(trainerWorkload));

        // when
        trainerWorkloadService.updateWorkload(requestDto);

        // then
        verify(trainerWorkloadRepository, times(1)).findByTrainerUsername(requestDto.getTrainerUsername());
        boolean isMonthRemoved = trainerWorkload.getYears().stream()
                .noneMatch(workloadYear -> workloadYear.getMonths().stream()
                        .anyMatch(workloadMonth -> workloadMonth.getMonth() == 11));
        assertTrue(isMonthRemoved);
        assertTrue(trainerWorkload.getYears().isEmpty());
    }

    @Test
    public void getWorkload_shouldReturnWorkload_whenValidRequestProvided() {
        // given
        String username = "username";
        int year = 2024;
        int month = 11;
        int expectedDuration = trainerWorkload.getYears().get(0).getMonths().get(0).getTrainingDuration();

        when(trainerWorkloadRepository.findByTrainerUsername(username)).thenReturn(Optional.of(trainerWorkload));

        // when
        int workloadDuration = trainerWorkloadService.getWorkload(username, year, month);

        // then
        assertThat(workloadDuration).isEqualTo(expectedDuration);
        verify(trainerWorkloadRepository, times(1)).findByTrainerUsername(username);
    }

    @Test
    public void getWorkload_shouldThrowEntityNotFoundException_whenNonExistingTrainerUsernameProvided() {
        // given
        String nonExistingUsername = "nonExistingUsername";
        int year = 2024;
        int month = 11;

        when(trainerWorkloadRepository.findByTrainerUsername(nonExistingUsername)).thenReturn(Optional.empty());

        // when / then
        assertThrows(EntityNotFoundException.class, () -> trainerWorkloadService.getWorkload(nonExistingUsername, year, month));

        verify(trainerWorkloadRepository, times(1)).findByTrainerUsername(nonExistingUsername);
    }

    private static TrainerWorkloadUpdateRequestDto getTrainerWorkloadUpdateRequestDto(ActionType actionType, LocalDate date) {
        return TrainerWorkloadUpdateRequestDto.builder()
                .trainerUsername("username")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .actionType(actionType)
                .isActive(true)
                .trainingDate(date)
                .trainingDuration(60)
                .build();
    }
}
