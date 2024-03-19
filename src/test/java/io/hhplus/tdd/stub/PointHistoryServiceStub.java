package io.hhplus.tdd.stub;

import io.hhplus.tdd.point.data.PointHistory;
import io.hhplus.tdd.point.data.UserPoint;
import io.hhplus.tdd.point.enumdata.TransactionType;
import io.hhplus.tdd.point.service.IPointHistoryService;

import java.util.List;

public class PointHistoryServiceStub implements IPointHistoryService {
    private PointHistory result;
    private List<PointHistory> resultList;
    public void setResult(PointHistory result){
        this.result = result;
    }

    public void setNull(){

    }

    public void setListResult(List<PointHistory> resultList){
        this.resultList = resultList;
    }

    @Override
    public List<PointHistory> getPointHistory(Long userId) {
        return resultList;
    }

    @Override
    public PointHistory save(Long userId, TransactionType type, Long amount) {
        return result;
    }
}
