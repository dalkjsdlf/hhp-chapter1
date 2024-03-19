package io.hhplus.tdd.point.service;

import io.hhplus.tdd.exception.UserPointErrorResult;
import io.hhplus.tdd.exception.UserPointException;
import io.hhplus.tdd.point.data.UserPoint;
import io.hhplus.tdd.point.repository.IUserPointRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserPointService {

    private final IUserPointRepository userPointRepository;

    private static Logger logger = LoggerFactory.getLogger(UserPointService.class);
    public UserPointService(@Autowired(required = false) IUserPointRepository userPointRepository) {
        this.userPointRepository = userPointRepository;
    }

    public UserPoint getUserPoint(Long id){

        if(id == null){
            throw new UserPointException(UserPointErrorResult.WRONG_USER_ID);
        }

        UserPoint userPoint = userPointRepository.selectById(id);
        if(userPoint == null){
            throw new UserPointException(UserPointErrorResult.USER_POINT_NOT_FOUND);
        }

        return userPoint;
    }

    public UserPoint chargeUserPoint(Long id, Long amount){

        if(amount == null || amount < 0){
            throw new UserPointException(UserPointErrorResult.WRONG_POINT_AMOUNT);
        }
        UserPoint userPoint = userPointRepository.selectById(id);

        Long newAmount = amount;
        if(userPoint != null){
            newAmount += userPoint.point();
        }

        UserPoint newUserPoint = userPointRepository.save(id,newAmount);
        logger.debug(">>>>>>>>{}",newUserPoint.point());
        return newUserPoint;

    }

    public UserPoint useUserPoint(Long id, Long amount){

        // 검사 함수
        if(amount == null || amount < 0){
            throw new UserPointException(UserPointErrorResult.WRONG_POINT_AMOUNT);
        }

        UserPoint userPoint = userPointRepository.selectById(id);

        if(userPoint == null){
            throw new UserPointException(UserPointErrorResult.USER_POINT_NOT_FOUND);
        }

        long resVal = userPoint.point() - amount;

        //음수 검사 함수
        if(resVal < 0){
            throw new UserPointException(UserPointErrorResult.NOT_ENOUGH_POINT);
        }

        UserPoint newUserPoint = userPointRepository.save(id,resVal);
        return newUserPoint;
    }
}