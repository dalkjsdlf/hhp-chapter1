package io.hhplus.tdd.point.service;

import io.hhplus.tdd.exception.UserPointErrorResult;
import io.hhplus.tdd.exception.UserPointException;
import io.hhplus.tdd.point.data.PointHistory;
import io.hhplus.tdd.point.data.UserPoint;
import io.hhplus.tdd.point.dto.PointHistoryResponseDto;
import io.hhplus.tdd.point.dto.UserPointResponseDto;

import java.util.List;

public interface IUserPointService {
    public UserPointResponseDto getUserPoint(Long id);
    public UserPointResponseDto chargeUserPoint(Long id, Long amount);


    public UserPointResponseDto useUserPoint(Long id, Long amount);

    public List<PointHistoryResponseDto> getPointHistory(Long userId);
}
