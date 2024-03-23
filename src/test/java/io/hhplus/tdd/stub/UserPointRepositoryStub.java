package io.hhplus.tdd.stub;

import io.hhplus.tdd.point.data.UserPoint;
import io.hhplus.tdd.point.repository.IUserPointRepository;
import lombok.Setter;

import java.util.List;

public class UserPointRepositoryStub implements IUserPointRepository {

    @Setter
    private UserPoint result;
    private List<UserPoint> resultList;

    public void setNull(){

    }

    public void setListResult(List<UserPoint> resultList){
        this.resultList = resultList;
    }
    @Override
    public List<UserPoint> selectAllByUserId(Long id, Long amount) {
        return resultList;
    }

    @Override
    public UserPoint selectById(Long id) {
        return result;
    }

    @Override
    public UserPoint save(Long id, Long amount) {
        return result;
    }
}
