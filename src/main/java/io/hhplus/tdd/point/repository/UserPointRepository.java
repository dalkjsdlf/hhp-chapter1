package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.data.UserPoint;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserPointRepository implements IUserPointRepository{

    private UserPointTable userPointTable;

    public UserPointRepository(@Autowired UserPointTable userPointTable) {
        this.userPointTable = userPointTable;
    }

    @Override
    public List<UserPoint> selectAllByUserId(Long id, Long amount) {
        return null;
    }

    @Override
    public UserPoint selectById(Long id) {

        try {
            return userPointTable.selectById(id);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserPoint save(Long id, Long amount) {
        try {
            return userPointTable.insertOrUpdate(id, amount);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
