package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.data.UserPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
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

        return null;
    }

    @Override
    public UserPoint save(Long id, Long amount) {
        return null;
    }
}
