package io.hhplus.tdd;

import io.hhplus.tdd.point.data.UserPoint;
import io.hhplus.tdd.point.repository.IUserPointRepository;

import java.util.List;

public class UserPointRepositoryStub implements IUserPointRepository {

    List<UserPoint> selectAllByUserIdReturn;
    UserPoint selectByIdReturn;

    UserPoint saveReturn;


    public void setReturnList(List<UserPoint> returnListVal){
        this.selectAllByUserIdReturn = returnListVal;
    }
    public void setReturn(UserPoint returnVal){
        this.selectByIdReturn = returnVal;
    }
    public void setSavedReturn(UserPoint returnVal){
        this.saveReturn = returnVal;
    }

    @Override
    public List<UserPoint> selectAllByUserId(Long id, Long amount) {
        return selectAllByUserIdReturn;
    }

    @Override
    public UserPoint selectById(Long id) {
        return selectByIdReturn;
    }

    @Override
    public UserPoint save(Long id, Long amount) {
        return saveReturn;
    }
}
