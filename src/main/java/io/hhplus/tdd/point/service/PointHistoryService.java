package io.hhplus.tdd.point.service;

import io.hhplus.tdd.exception.UserPointErrorResult;
import io.hhplus.tdd.exception.UserPointException;
import io.hhplus.tdd.point.data.PointHistory;
import io.hhplus.tdd.point.enumdata.TransactionType;
import io.hhplus.tdd.point.repository.IPointHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointHistoryService implements IPointHistoryService{

    private final IPointHistoryRepository pointHistoryRepository;

    public PointHistoryService(@Autowired IPointHistoryRepository pointHistoryRepository) {
        this.pointHistoryRepository = pointHistoryRepository;
    }

    public List<PointHistory> getPointHistory(Long userId){

        if(userId == null){
            throw new UserPointException(UserPointErrorResult.WRONG_USER_ID);
        }

        List<PointHistory> pointHistories = pointHistoryRepository.selectAllByUserId(userId);

        if(pointHistories.isEmpty()){
            throw new UserPointException(UserPointErrorResult.USER_POINT_NOT_FOUND);
        }

        return pointHistories;
    }

    public PointHistory save(Long userId, TransactionType type, Long amount){

        if(userId == null){
            throw new UserPointException(UserPointErrorResult.WRONG_USER_ID);
        }
        if(type == null){
            throw new UserPointException(UserPointErrorResult.WRONG_TRANSACTION_TYPE);
        }
        if(amount == null || amount < 0){
            throw new UserPointException(UserPointErrorResult.WRONG_POINT_AMOUNT);
        }

        PointHistory pointHistory = pointHistoryRepository.insert(userId, amount, type);
        return pointHistory;
    }

}
