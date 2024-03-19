package io.hhplus.tdd.point.dto;

import io.hhplus.tdd.point.enumdata.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class PointHistoryResponseDto {

    @NotNull
    private final Long id;

    @NotNull
    @Min(0)
    private final Long amount;

    @NotNull
    private final TransactionType type;
}
