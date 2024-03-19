package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.data.PointHistory;
import io.hhplus.tdd.point.enumdata.TransactionType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPointHistoryRepository {
    public PointHistory insert(
            Long id,
            Long amount,
            TransactionType transactionType
    );
    public List<PointHistory> selectAllByUserId(Long userId);
}
