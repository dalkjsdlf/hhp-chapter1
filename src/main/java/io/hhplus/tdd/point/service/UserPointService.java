package io.hhplus.tdd.point.service;

import io.hhplus.tdd.exception.UserPointErrorResult;
import io.hhplus.tdd.exception.UserPointException;
import io.hhplus.tdd.point.data.PointHistory;
import io.hhplus.tdd.point.data.UserPoint;
import io.hhplus.tdd.point.dto.PointHistoryResponseDto;
import io.hhplus.tdd.point.dto.UserPointResponseDto;
import io.hhplus.tdd.point.enumdata.TransactionType;
import io.hhplus.tdd.point.repository.IUserPointRepository;
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

    private final ReentrantLock lock = new ReentrantLock();
    public UserPointService(@Autowired(required = false) IUserPointRepository userPointRepository
                          , @Autowired(required = false) IPointHistoryService pointHistoryService) {
        this.userPointRepository = userPointRepository;
        this.pointHistoryService = pointHistoryService;
    }


    /**
     * @Desc 사용자 ID의 포인트 단건 조회
     * @param id
     * @return
     */
    public UserPointResponseDto getUserPoint(Long id){

        //사용자 ID가 NULL로 입력되면 예외처리
        if(id == null){
            throw new UserPointException(UserPointErrorResult.WRONG_USER_ID);
        }

        // 사용자 ID로 포인트 조회
        UserPoint userPoint = userPointRepository.selectById(id);
        logger.info("user point [{}]", userPoint);

        // 사용자가 존재하지 않았을 때 예외처리
        if(userPoint == null){
            throw new UserPointException(UserPointErrorResult.USER_POINT_NOT_FOUND);
        }

        // Dto로 변환
        return convertUserPointToResDto(userPoint,null);
    }

    /**
     * @Desc UserId의 포인트를 amount만큼 충전
     * @param id  --> 사용자ID
     * @param amount
     * @return
     */
    public UserPointResponseDto chargeUserPoint(Long id, Long amount){

        //포인트값이 null이거나 음수로 입력되었을때 예외처리
        if(amount == null || amount < 0){
            throw new UserPointException(UserPointErrorResult.WRONG_POINT_AMOUNT);
        }
        //String key = "key";
        //SimultaneousEntriesLockByKey lockByKey = new SimultaneousEntriesLockByKey();
        //lockByKey.lock(key);

        Long newAmount = amount;

        lock.lock();

        // 해당 사용자가 존재하는지 검사하기 위해 조회
        UserPoint existanceUserPoint = userPointRepository.selectById(id);
        logger.info("userPoint 조회값 [{}] 넘어온 값 [{}]",existanceUserPoint,amount);

        // 사용할 포인트가 등록되지 않는지 검사
        // 사용할 포인트의 사용자가 등록되지 않았을 경우 예외처리한다.
        if(existanceUserPoint != null){
            newAmount += existanceUserPoint.point();
        }

        logger.info("userPoint 저장값 [{}]",newAmount);

        // 사용자 포인트 저장
        UserPoint savedUserPoint = userPointRepository.save(id,newAmount);
        logger.info("userPoint 저장완료");

        // 포인트 내역 저장
        PointHistory savedPointHistory = pointHistoryService.save(id, TransactionType.CHARGE,amount);
        logger.info(">>>>>> pointhistory[{}]",savedPointHistory);
        lock.unlock();

        //포인트나 내역저장에 실패하였을때 예외처리
        if(savedUserPoint == null || savedPointHistory == null){
            throw new UserPointException(UserPointErrorResult.FAILED_TO_CHARGE);
        }

        //Dto로 변환하여 반환
        return convertUserPointToResDto(savedUserPoint, savedUserPoint.id());
    }

    /**
     * @Desc UserId의 포인트를 amount만큼 차감하여 사용
     * @param id
     * @param amount
     * @return
     */
    public UserPointResponseDto useUserPoint(Long id, Long amount){

        // 검사 함수
        if(amount == null || amount < 0){
            throw new UserPointException(UserPointErrorResult.WRONG_POINT_AMOUNT);
        }

        // 동시성 문제 해결을 위한 lock
        lock.lock();

        // 해당 사용자가 존재하는지 검사하기 위해 조회
        UserPoint existanceUserPoint = userPointRepository.selectById(id);
        logger.info("userPoint 조회값 [{}] 넘어온 값 [{}]",existanceUserPoint,amount);

        // 사용할 포인트가 등록되지 않는지 검사
        // 사용할 포인트의 사용자가 등록되지 않았을 경우 예외처리한다.
        if(existanceUserPoint == null){
            throw new UserPointException(UserPointErrorResult.USER_POINT_NOT_FOUND);
        }

        long newAmount = existanceUserPoint.point() - amount;
        logger.info("userPoint 저장값 [{}]",newAmount);
        // 사용 후 남은 포인트가 음수인지 검사
        // 음수이면 포인트가 충분하지 않아 예외처리
        if(newAmount < 0){
            throw new UserPointException(UserPointErrorResult.NOT_ENOUGH_POINT);
        }

        // 사용자 포인트 저장
        UserPoint savedUserPoint = userPointRepository.save(id, newAmount);
        logger.info("userPoint 저장완료");

        // 포인트 내역 저장
        PointHistory pointHistory = pointHistoryService.save(id, TransactionType.USE, amount);
        // 동시성 문제 해결을 위한 unlock
        lock.unlock();

        // 포인트 내역 저장 실패시 예외처리한다.
        if(pointHistory == null){
            throw new UserPointException(UserPointErrorResult.FAILED_TO_USE);
        }

        //DTO로 전환하여 반환
        return convertUserPointToResDto(savedUserPoint, pointHistory.id());
    }

    /**
     * @Desc UserId의 포인트내역 다건 조회
     * @param userId
     * @return
     */
    public List<PointHistoryResponseDto> getPointHistory(Long userId){
        if(userId == null){
            throw new UserPointException(UserPointErrorResult.WRONG_USER_ID);
        }

        List<PointHistory> pointHistoreis = pointHistoryService.getPointHistory(userId);
        return convertPointHistoryToResDto(pointHistoreis);
    }

    /**
     * @Desc UserPoint를 UserPointResponseDto로 변환하는 메서드
     * @param userPoint
     * @param pointHistoryId
     * @return
     */
    private UserPointResponseDto convertUserPointToResDto(UserPoint userPoint, Long pointHistoryId){
        return UserPointResponseDto
                .builder()
                .historyId(pointHistoryId)
                .id(userPoint.id())
                .amount(userPoint.point())
                .build();
    }

    /**
     * @Desc PointHistory를 PointHistoryResponseDto로 변환하는 메서드
     * @param pointHistories
     * @return
     */
    private List<PointHistoryResponseDto> convertPointHistoryToResDto(List<PointHistory> pointHistories){
        return pointHistories.stream().map(item->PointHistoryResponseDto
                .builder()
                .userId(item.userId())
                .amount(item.amount())
                .type(item.type())
                .build()).collect(Collectors.toList());
    }
}