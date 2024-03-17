package io.hhplus.tdd.point.data;

import io.hhplus.tdd.point.enumdata.TransactionType;

public record PointHistory(
        Long id,
        Long userId,
        TransactionType type,
        Long amount,
        Long timeMillis
) {
}
