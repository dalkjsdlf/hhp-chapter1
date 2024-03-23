package io.hhplus.tdd.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.UserPointErrorResult;
import io.hhplus.tdd.exception.UserPointException;
import io.hhplus.tdd.point.data.PointHistory;
import io.hhplus.tdd.point.data.UserPoint;
import io.hhplus.tdd.point.dto.PointHistoryResponseDto;
import io.hhplus.tdd.point.dto.UserPointResponseDto;
import io.hhplus.tdd.point.enumdata.TransactionType;
import io.hhplus.tdd.point.repository.IUserPointRepository;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.service.PointHistoryService;
import io.hhplus.tdd.point.service.UserPointService;
import io.hhplus.tdd.stub.PointHistoryServiceStub;
import io.hhplus.tdd.stub.UserPointRepositoryStub;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

/***
 * -- NULL --
 * [성공] NULL 검사 - 테스트성공
 * -- 포인트조회 --
 * [성공] 사용자ID로 포인트 조회
 * [실패] 사용자ID로 포인트 조회(ID 조회안됨)
 * [실패] 사용자ID로 포인트 조회(ID가 null 입력)
 * [성공] 새로운 사용자 ID로 포인트 충전
 * -- 포인트 충전 --
 * [성공] 기 사용자로 포인트 충전 추가적립 - TODO 점검필요
 * [실패] 충전값이 음수
 * [실패] 충전값이 NULL
 * -- 포인트 사용 --
 * [성공] 기 사용자로 포인트 사용 - TODO 점검필요
 * [실패] 사용값이 음수
 * [실패] 사용값이 NULL
 * [실패] 사용할 사용자의 포인트가 부족한
 * [성공] 특정 사용자로 포인트 히스토리 조회성공
 */
@DisplayName("[User Point Service Test]")
@ExtendWith(MockitoExtension.class)
@Slf4j
public class UserPointServiceTest {


    private UserPointService userPointService;
    private IUserPointRepository userPointRepository;
    private PointHistoryService pointHistoryService;

    public static Long n = 1L;

    private final static ThreadLocal<Long> threadLocal = new ThreadLocal<>();


    private static Logger logger = LoggerFactory.getLogger(UserPointServiceTest.class);

    @BeforeEach
    public void injectObject(){
        userPointRepository = new UserPointRepository(new UserPointTable());
        pointHistoryService = new PointHistoryService(
                new PointHistoryRepository(new PointHistoryTable())
        );

        userPointService = new UserPointService(userPointRepository,pointHistoryService);
    }

    @DisplayName("[성공] NULL 검사 - 테스트성공")
    @Test()
    public void givenNothing_whenNothing_thenUserPointRepository(){
        // given

        // when

        // then
        assertThat(userPointService).isNotNull();
    }

    /**
     * @Desc [포인트 조회] 포인트 조회시도 성공
     * */
    @DisplayName("[성공] 사용자ID로 포인트 조회 (확인 샘플)")
    @Test()
    public void givenUserId_whenGetPoint_thenGetUserPoint(){
        // given
        UserPointRepositoryStub stubRepo = new UserPointRepositoryStub();
        PointHistoryServiceStub stupSvc  = new PointHistoryServiceStub();

        Long id = 1L;
        Long amount = 10000L;

        /*
         * 테스트용 UserPoint객체 생성
         */
        UserPoint resultPoint = new UserPoint(id, amount, System.currentTimeMillis());

        /*
         * Repo의 selectById의 결과값 입력
         */
        stubRepo.setResult(resultPoint);

        /*
         * Stub객체를 주입한 UserPointService 직접 생성
         */
        userPointService = new UserPointService(stubRepo,stupSvc);

        // when
        UserPointResponseDto userPointDto = userPointService.getUserPoint(id);

        // then
        assertThat(userPointDto).isNotNull();
        assertThat(userPointDto.getId()).isEqualTo(id);
    }

    /**
     * @Desc [포인트 조회] 조회시도하는 사용자 ID가 존재하지 않을 때
     * */
    @DisplayName("[실패] 사용자ID로 포인트 조회(ID 조회안됨)")
    @Test()
    public void givenUserId_whenGetPoint_thenThrowPointNotFound(){
        // given
        Long id = 1L;
        doReturn(null).when(userPointRepository).selectById(id);

        // when
        UserPointException userPointException = assertThrows(UserPointException.class,()->userPointService.getUserPoint(id));

        // then
        // UserPoint를 찾지못해 조회실패 Exception.
        assertThat(userPointException.getErrorResult()).isEqualTo(UserPointErrorResult.USER_POINT_NOT_FOUND);
    }

    /**
     * @Desc [포인트 조회] 조회시도하는 사용자 ID가 NULL일때 실패
     * */
    @DisplayName("[실패] 사용자ID로 포인트 조회(ID가 null 입력)")
    @Test()
    public void givenNullOfUserId_whenGetPoint_thenThrowWrongId(){
        // given
        Long id = null;

        // when
        UserPointException userPointException = assertThrows(UserPointException.class,()->userPointService.getUserPoint(id));

        // then
        // UserPoint가 null 조회실패 Exception
        assertThat(userPointException.getErrorResult()).isEqualTo(UserPointErrorResult.WRONG_USER_ID);
    }

    /**
     * @Desc [포인트 충전] 처음 충전하는 사용자 충전 시도 성공
     * */
    @DisplayName("[성공] 새로운 사용자 ID로 포인트 충전")
    @Test()
    public void givenNewUserId_whenChargePoint_thenSuccessfullyCharge(){
        // given
        Long id = 1L;
        Long amount = 10000L;
        // repository에서 user point null 반환

        // when
        // DTO로 만들어?
        UserPointResponseDto chargedUserPoint = userPointService.chargeUserPoint(id,amount);


        // then
        assertThat(chargedUserPoint).isNotNull();
        assertThat(chargedUserPoint.getId()).isEqualTo(id);
        assertThat(chargedUserPoint.getAmount()).isEqualTo(amount);
    }

    /**
     * @Desc [포인트 충전] 사용자가 이미 존재할 때 추가 충전시도 성공
     * */
    @DisplayName("[성공] 기 사용자로 포인트 충전 추가적립 - TODO 점검필요")
    @Test()
    public void givenExistedUserId_whenChargePoint_thenSuccessfullyCharge(){
        // given
        Long id = 1L;
        Long newAmount = 10000L; //추가 충전 포인트
        Long initAmount = 2000L; //충전 전 가용포인트

        Long totalAmount = initAmount + newAmount; //최종 충전 포인트

        UserPoint initPoint = getNewUserPoint(id,initAmount);
        UserPoint resultPoint = getNewUserPoint(id,totalAmount);

        // when
        // DTO로 만들어?
        UserPointResponseDto chargedUserPoint = userPointService.chargeUserPoint(id,newAmount);

        // then
        assertThat(chargedUserPoint).isNotNull();
        assertThat(chargedUserPoint.getId()).isEqualTo(id);
        assertThat(chargedUserPoint.getAmount()).isEqualTo(totalAmount);
    }

    /**
     * @Desc [포인트 충전] 충전하려는 포인트 데이터가 음수 일때 실패
     * */
    @DisplayName("[실패] 충전값이 음수")
    @Test()
    public void givenUserIdAndNegativeAmount_whenChargePoint_thenThrowWrongAmount(){
        // given
        Long id = 1L;
        Long amount = -1L;

        // when
        // DTO로 만들어?
        UserPointException userPointException = assertThrows(UserPointException.class,()-> userPointService.chargeUserPoint(id,amount));

        // then
        assertThat(userPointException.getErrorResult()).isEqualTo(UserPointErrorResult.WRONG_POINT_AMOUNT);
    }

    /**
     * @Desc [포인트 충전] 충전하려는 포인트 데이터가 NULL일때 실패
     * */
    @DisplayName("[실패] 충전값이 NULL")
    @Test()
    public void givenUserIdAndNullAmount_whenChargePoint_thenThrowWrongAmount(){
        // given
        Long id = 1L;
        Long amount = null;

        // when
        // DTO로 만들어?
        UserPointException userPointException = assertThrows(UserPointException.class,()-> userPointService.chargeUserPoint(id,amount));

        // then
        assertThat(userPointException.getErrorResult()).isEqualTo(UserPointErrorResult.WRONG_POINT_AMOUNT);
    }

    /**
     * @Desc [포인트 충전] 사용자가 이미 존재할 때 추가 충전시도 하였을 때 히스토리 저장 확인
     * */
    @DisplayName("[성공] 기 사용자로 포인트 충전 추가적립 - TODO 점검필요")
    @Test()
    public void givenExistedUserId_whenChargePoint_thenSuccessfullySaveHistory(){
        // given
        Long id = 1L;
        Long newAmount = 10000L; //추가 충전 포인트
        Long initAmount = 2000L; //충전 전 가용포인트

        Long totalAmount = initAmount + newAmount; //최종 충전 포인트

        UserPoint initPoint = getNewUserPoint(id,initAmount);
        UserPoint resultPoint = getNewUserPoint(id,totalAmount);

        // when
        // DTO로 만들어?
        UserPointResponseDto chargedUserPoint = userPointService.chargeUserPoint(id,newAmount);

        // then
        assertThat(chargedUserPoint).isNotNull();
        assertThat(chargedUserPoint.getId()).isEqualTo(id);
        assertThat(chargedUserPoint.getAmount()).isEqualTo(totalAmount);
    }

    /**
     * @Desc [포인트 사용]초기포인트가 10000일때 2000포인트 사용 시도 성공
     * */
    @DisplayName("[성공] 기 사용자로 포인트 사용 - TODO 점검필요")
    @Test()
    public void givenExistedUserId_whenUsePoint_thenSuccessfullyUse(){
        // given
        Long id = 1L;
        Long newAmount  = 2000L; //추가 충전 포인트
        Long initAmount = 10000L; //충전 전 가용포인트

        Long resAmount = initAmount - newAmount; //최종 충전 포인트

        UserPoint initPoint = getNewUserPoint(id,initAmount);
        UserPoint resultPoint = getNewUserPoint(id,resAmount);

        // when
        // DTO로 만들어?
        UserPointResponseDto chargedUserPoint = userPointService.useUserPoint(id,newAmount);

        // then
        assertThat(chargedUserPoint).isNotNull();
        assertThat(chargedUserPoint.getId()).isEqualTo(id);
        assertThat(chargedUserPoint.getAmount()).isEqualTo(resAmount);
    }
    /**
     * @Desc [포인트 사용]사용하려는 포인트 데이터가 음수 일때 실패
     * */
    @DisplayName("[실패] 사용값이 음수")
    @Test()
    public void givenUserIdAndNegativeAmount_whenUsePoint_thenThrowWrongAmount(){
        // given
        Long id = 1L;
        Long amount = -1L;

        // when
        // DTO로 만들어?
        UserPointException userPointException = assertThrows(UserPointException.class,()-> userPointService.useUserPoint(id,amount));

        // then
        assertThat(userPointException.getErrorResult()).isEqualTo(UserPointErrorResult.WRONG_POINT_AMOUNT);
    }

    /**
     * @Desc [포인트 사용]사용하려는 포인트 데이터가 NULL일때 실패
     * */
    @DisplayName("[실패] 사용값이 NULL")
    @Test()
    public void givenUserIdAndNullAmount_whenUsePoint_thenThrowWrongAmount(){
        // given
        Long id = 1L;
        Long amount = null;

        // when
        // DTO로 만들어?
        UserPointException userPointException = assertThrows(UserPointException.class,()-> userPointService.useUserPoint(id,amount));

        // then
        assertThat(userPointException.getErrorResult()).isEqualTo(UserPointErrorResult.WRONG_POINT_AMOUNT);
    }

    /**
     * @Desc [포인트 사용] 2000포인트가 저장되어 있는 상태에서 10000포인트를 사용하려는 시도시 실패
     * */
    @Disabled
    @DisplayName("[실패] 사용할 사용자의 포인트가 부족한")
    @Test()
    public void givenUserIdAndAmount_whenUsePoint_thenThrowNotEnough(){
        // given
        Long id = 1L;
        Long amount = 10000L;

        doReturn(getNewUserPoint(id,2000L)).when(userPointRepository).selectById(id);

        // when
        // DTO로 만들어?
        UserPointException userPointException = assertThrows(UserPointException.class,()-> userPointService.useUserPoint(id,amount));

        // then
        assertThat(userPointException.getErrorResult()).isEqualTo(UserPointErrorResult.NOT_ENOUGH_POINT);
    }

    /**
     * @Desc [포인트 사용] 특정 사용자로 히스토리 조회 성공
     * */
    @DisplayName("[성공] 특정 사용자로 포인트 히스토리 조회성공")
    @Test()
    public void givenUserId_whenGetPointHistory_thenGetUserPointHistory(){
        // given
        Long id = 1L;
        Long amount = 10000L;


        List<PointHistory> pointHistoryList = List.of(
                new PointHistory(1L, id, TransactionType.CHARGE, amount, System.currentTimeMillis()),
                new PointHistory(2L, id, TransactionType.USE, 2000L, System.currentTimeMillis()),
                new PointHistory(3L, id, TransactionType.CHARGE, 3000L, System.currentTimeMillis()));

        doReturn(pointHistoryList).when(pointHistoryService).getPointHistory(id);

        // when
        List<PointHistoryResponseDto> result = userPointService.getPointHistory(id);

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(3);
    }

    public UserPoint getNewUserPoint(Long id, Long amount){
        return new UserPoint(id,amount,System.currentTimeMillis());
    }

    @DisplayName("[성공] 여러 사용자가 동시에 충전하여도 순차처리됨")
    @Test()
    public void givenChargeValue_whenChargeSimultaneously_thenSyncResult() throws InterruptedException {
        // given
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(26);

        CountDownLatch latch = new CountDownLatch(threadCount);
        Long userId = 1L;

        userPointService.chargeUserPoint(userId,10000L);
        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {

                    userPointService.chargeUserPoint(userId,n);
                    n = n + 1;
                    logger.info("threadLocal [{}]",n);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        UserPointResponseDto result = userPointService.getUserPoint(userId);
        logger.info("결과 >>> {}", result.getAmount());

        //then
        AssertionsForClassTypes.assertThat( result.getAmount()).isEqualTo(10010L);
    }


    @Disabled
    @DisplayName("[성공] 여러 사용자가 동시에 사용하여도 순차처리됨")
    @Test()
    public void givenUseValue_whenUseSimultaneously_thenSyncResult() throws InterruptedException {
        // given
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(26);

        CountDownLatch latch = new CountDownLatch(threadCount);
        Long userId = 1L;

        userPointService.chargeUserPoint(userId,10000L);

        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {

                    logger.debug("threadLocal [{}]",n);
                    userPointService.useUserPoint(userId,n);

                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        UserPointResponseDto result = userPointService.getUserPoint(userId);
        logger.debug("결과 >>> {}", result.getAmount());

        //then
        AssertionsForClassTypes.assertThat( result.getAmount()).isEqualTo(9990);
    }

}
