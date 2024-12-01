package com.epam.trainer_workload.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ErrorResponseDto {

    private String message;

    public static ErrorResponseDto of(String message) {
        return new ErrorResponseDto(message);
    }
}
