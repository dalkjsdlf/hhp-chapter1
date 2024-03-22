package io.hhplus.tdd.point.service;

import io.hhplus.tdd.exception.UserPointErrorResult;
import io.hhplus.tdd.exception.UserPointException;
import io.hhplus.tdd.point.data.PointHistory;
import io.hhplus.tdd.point.data.UserPoint;
import io.hhplus.tdd.point.dto.PointHistoryResponseDto;
import io.hhplus.tdd.point.dto.UserPointResponseDto;
import io.hhplus.tdd.point.enumdata.TransactionType;
import io.hhplus.tdd.point.repository.IUserPointRepository;
import io.hhplus.tdd.threadhandle.SimultaneousEntriesLockByKey;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class UserPointService implements IUserPointService{

    private static final Logger logger = LoggerFactory.getLogger(UserPointService.class);
    private final IUserPointRepository userPointRepository;
    private final IPointHistoryService pointHistoryService;
    private static Long sharedAmount = 0L;

    private final ReentrantLock lock = new ReentrantLock();
    public UserPointService(@Autowired(required = false) IUserPointRepository userPointRepository
                          , @Autowired(required = false) IPointHistoryService pointHistoryService) {
        this.userPointRepository = userPointRepository;
        this.pointHistoryService = pointHistoryService;
    }

    public UserPointResponseDto getUserPoint(Long id){

        if(id == null){
            throw new UserPointException(UserPointErrorResult.WRONG_USER_ID);
        }

        UserPoint userPoint = userPointRepository.selectById(id);
        logger.info("user point [{}]", userPoint);

        if(userPoint == null){
            throw new UserPointException(UserPointErrorResult.USER_POINT_NOT_FOUND);
        }
        return UserPointResponseDto
                .builder()
                .id(userPoint.id())
                .amount(userPoint.point())
                .build();
    }

    public UserPointResponseDto chargeUserPoint(Long id, Long amount){

        if(amount == null || amount < 0){
            throw new UserPointException(UserPointErrorResult.WRONG_POINT_AMOUNT);
        }
        Long newAmount = amount;
        UserPoint newUserPoint = null;
        PointHistory pointHistory = null;

        //String key = "key";
        //SimultaneousEntriesLockByKey lockByKey = new SimultaneousEntriesLockByKey();
        //synchronized (this){
        //lockByKey.lock(key);

        lock.lock();
        UserPoint userPoint = userPointRepository.selectById(id);
        logger.info("userPoint 조회값 [{}] 넘어온 값 [{}]",userPoint,amount);

        if(userPoint != null){
            newAmount += userPoint.point();
        }
        logger.info("userPoint 저장값 [{}]",newAmount);

        newUserPoint = userPointRepository.save(id,newAmount);

        logger.info("userPoint 저장완료");

        pointHistory = pointHistoryService.save(id, TransactionType.CHARGE,amount);

        lock.unlock();


        if(newUserPoint == null || pointHistory == null){
            throw new UserPointException(UserPointErrorResult.FAILED_TO_CHARGE);
        }

        return UserPointResponseDto
                .builder()
                .id(newUserPoint.id())
                .amount(newUserPoint.point())
                .historyId(pointHistory.id())
                .build();
        //}
    }

    public UserPointResponseDto useUserPoint(Long id, Long amount){

        // 검사 함수
        if(amount == null || amount < 0){
            throw new UserPointException(UserPointErrorResult.WRONG_POINT_AMOUNT);
        }

        lock.lock();
        PointHistory pointHistory = null;
        UserPoint newUserPoint = userPointRepository.selectById(id);

        if(newUserPoint == null){
            throw new UserPointException(UserPointErrorResult.USER_POINT_NOT_FOUND);
        }

        long resVal = newUserPoint.point() - amount;

        //음수 검사 함수
        if(resVal < 0){
            throw new UserPointException(UserPointErrorResult.NOT_ENOUGH_POINT);
        }

        UserPoint savedUserPoint = userPointRepository.save(id, amount);

        pointHistory = pointHistoryService.save(id, TransactionType.USE,amount);

        lock.unlock();


        if(pointHistory == null){
            throw new UserPointException(UserPointErrorResult.FAILED_TO_USE);
        }

        return UserPointResponseDto
                .builder()
                .historyId(pointHistory.id())
                .id(savedUserPoint.id())
                .amount(savedUserPoint.point())
                .build();
    }

    public void test(Long val){
        String key = "key";
        SimultaneousEntriesLockByKey lockByKey = new SimultaneousEntriesLockByKey();
        try{
            lockByKey.lock(key);
            logger.info("userPointService shared amount >> " + sharedAmount);
            sharedAmount = sharedAmount + val;
        }finally {
            lockByKey.unlock(key);
        }
    }

    public Long getSharedAmount(){
        return sharedAmount;
    }

    public List<PointHistoryResponseDto> getPointHistory(Long userId){
        if(userId == null){
            throw new UserPointException(UserPointErrorResult.WRONG_USER_ID);
        }

        List<PointHistory> pointHistoreis = pointHistoryService.getPointHistory(userId);
        return pointHistoreis.stream().map(item->PointHistoryResponseDto
                .builder()
                .userId(item.userId())
                .amount(item.amount())
                .type(item.type())
                .build()).collect(Collectors.toList());
    }
}