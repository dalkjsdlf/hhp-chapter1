package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.data.PointHistory;
import io.hhplus.tdd.point.enumdata.TransactionType;

import java.util.List;

public class PointHistoryRepository implements IPointHistoryRepository{
    @Override
    public PointHistory insert(Long id, Long amount, TransactionType transactionType, Long updateMillis) {
        return null;
    }

    @Override
    public List<PointHistory> selectAllByUserId(Long userId) {
        return null;
    }
}
