package io.hhplus.tdd.point.service;

import io.hhplus.tdd.exception.UserPointErrorResult;
import io.hhplus.tdd.exception.UserPointException;
import io.hhplus.tdd.point.data.PointHistory;
import io.hhplus.tdd.point.data.UserPoint;

import java.util.List;

public interface IUserPointService {
    public UserPoint getUserPoint(Long id);
    public UserPoint chargeUserPoint(Long id, Long amount);


    public UserPoint useUserPoint(Long id, Long amount);

    public List<PointHistory> getPointHistory(Long userId);
}
