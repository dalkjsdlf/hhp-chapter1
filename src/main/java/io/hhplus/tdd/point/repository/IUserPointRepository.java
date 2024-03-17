package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.data.UserPoint;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserPointRepository {

    List<UserPoint> selectAllByUserId(Long id, Long amount);
    public UserPoint selectById(Long id);
    public UserPoint save(Long id, Long amount);
}
