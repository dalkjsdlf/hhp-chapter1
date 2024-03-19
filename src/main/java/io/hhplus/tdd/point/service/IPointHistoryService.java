package io.hhplus.tdd.point.service;

import io.hhplus.tdd.exception.UserPointErrorResult;
import io.hhplus.tdd.exception.UserPointException;
import io.hhplus.tdd.point.data.PointHistory;
import io.hhplus.tdd.point.enumdata.TransactionType;

import java.util.List;

public interface IPointHistoryService {
    public List<PointHistory> getPointHistory(Long userId);

    public PointHistory save(Long userId, TransactionType type, Long amount);
}
