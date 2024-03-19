package io.hhplus.tdd.service;

import io.hhplus.tdd.UserPointRepositoryStub;
import io.hhplus.tdd.exception.UserPointErrorResult;
import io.hhplus.tdd.exception.UserPointException;
import io.hhplus.tdd.point.data.PointHistory;
import io.hhplus.tdd.point.data.UserPoint;
import io.hhplus.tdd.point.dto.PointHistoryResponseDto;
import io.hhplus.tdd.point.dto.UserPointRequestDto;
import io.hhplus.tdd.point.dto.UserPointResponseDto;
import io.hhplus.tdd.point.enumdata.TransactionType;
import io.hhplus.tdd.point.repository.IPointHistoryRepository;
import io.hhplus.tdd.point.repository.IUserPointRepository;
import io.hhplus.tdd.point.service.PointHistoryService;
import io.hhplus.tdd.point.service.UserPointService;
import io.hhplus.tdd.stub.PointHistoryServiceStub;
import org.apache.catalina.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
public class UserPointServiceTest {


    @InjectMocks private UserPointService userPointService;
    @Mock private IUserPointRepository userPointRepository;
    @Mock private PointHistoryService pointHistoryService;

    @DisplayName("[성공] NULL 검사 - 테스트성공")
    @Test()
    public void givenNothing_whenNothing_thenUserPointRepository(){
        // given

        // when

        // then
        assertThat(userPointService).isNotNull();
    }
    /**
     * 포인트 조회 테스트케이스
     * */

    @DisplayName("[성공] 사용자ID로 포인트 조회")
    @Test()
    public void givenUserId_whenGetPoint_thenGetUserPoint(){
        // given
        Long id = 1L;
        Long amount = 10000L;
        UserPoint resultPoint = new UserPoint(id, amount, System.currentTimeMillis());
        doReturn(resultPoint).when(userPointRepository).selectById(id);

//        UserPointRepositoryStub stubRepo = new UserPointRepositoryStub();
//        PointHistoryServiceStub stupSvc  = new PointHistoryServiceStub();
//
//        stubRepo.setReturn(resultPoint);
//        stupSvc.setResult(null);
//
//        userPointService = new UserPointService(stubRepo,stupSvc);

        // when
        UserPointResponseDto userPointDto = userPointService.getUserPoint(id);

        // then
        assertThat(userPointDto).isNotNull();
        assertThat(userPointDto.getId()).isEqualTo(id);
    }
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
     * 포인트 충전 테스트케이스
     * */
    @DisplayName("[성공] 새로운 사용자 ID로 포인트 충전")
    @Test()
    public void givenNewUserId_whenChargePoint_thenSuccessfullyCharge(){
        // given
        Long id = 1L;
        Long amount = 10000L;
        // repository에서 user point null 반환
        doReturn(null).when(userPointRepository).selectById(id);
        doReturn(getNewUserPoint(id, amount)).when(userPointRepository).save(id,amount);

        // when
        // DTO로 만들어?
        UserPointResponseDto chargedUserPoint = userPointService.chargeUserPoint(id,amount);


        // then
        assertThat(chargedUserPoint).isNotNull();
        assertThat(chargedUserPoint.getId()).isEqualTo(id);
        assertThat(chargedUserPoint.getAmount()).isEqualTo(amount);
    }

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

        doReturn(initPoint).when(userPointRepository).selectById(id);
        doReturn(resultPoint).when(userPointRepository).save(id,totalAmount);

        // when
        // DTO로 만들어?
        UserPointResponseDto chargedUserPoint = userPointService.chargeUserPoint(id,newAmount);

        // then
        assertThat(chargedUserPoint).isNotNull();
        assertThat(chargedUserPoint.getId()).isEqualTo(id);
        assertThat(chargedUserPoint.getAmount()).isEqualTo(totalAmount);
    }
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
     * 포인트 사용 테스트케이스
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
        doReturn(initPoint).when(userPointRepository).selectById(id);
        doReturn(resultPoint).when(userPointRepository).save(id,resAmount);

        // when
        // DTO로 만들어?
        UserPointResponseDto chargedUserPoint = userPointService.useUserPoint(id,newAmount);

        // then
        assertThat(chargedUserPoint).isNotNull();
        assertThat(chargedUserPoint.getId()).isEqualTo(id);
        assertThat(chargedUserPoint.getAmount()).isEqualTo(resAmount);
    }
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
}
