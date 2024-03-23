package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.data.PointHistory;
import io.hhplus.tdd.point.enumdata.TransactionType;

import java.util.List;
public class PointHistoryRepository implements IPointHistoryRepository{

    private PointHistoryTable pointHistoryTable;

    public PointHistoryRepository(PointHistoryTable pointHistoryTable) {
        this.pointHistoryTable = pointHistoryTable;
    }

    @Override
    public PointHistory insert(Long id, Long amount, TransactionType transactionType) {
        try {
            return pointHistoryTable.insert(id,amount,transactionType,System.currentTimeMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PointHistory> selectAllByUserId(Long userId) {
       return pointHistoryTable.selectAllByUserId(userId);
    }
}
