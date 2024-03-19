package io.hhplus.tdd.service;

import io.hhplus.tdd.exception.UserPointErrorResult;
import io.hhplus.tdd.exception.UserPointException;
import io.hhplus.tdd.point.data.PointHistory;
import io.hhplus.tdd.point.data.UserPoint;
import io.hhplus.tdd.point.enumdata.TransactionType;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.service.PointHistoryService;
import io.hhplus.tdd.point.service.UserPointService;
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
 * 1)  null검사
 * 2)  특정사용자Id로 포인트 조회시 조회안됨 - 실패 (TC작성)
 * 3)  특정사용자Id가 null일경우 - 실패 (TC작성)
 * 7)  ID의 타입이 맞지 않는경우 - 실패
 * 4)  특정사용자ID로 포인트 조회시 성공 (TC작성)
 * 5)  새로운 ID로 충전시 성공(TC작성)
 * 8)  기존에 존재하는 ID로 충전시 추가 적립됨 (TC작성)
 * 9)  충전시 Amount가 음수일 경우 실패
 * 10) 충전시 Amount가 null일 경우 실패
 * 12) 사용시
 */
@DisplayName("[User Point Service Test]")
@ExtendWith(MockitoExtension.class)
public class UserPointServiceTest {


    @InjectMocks private UserPointService userPointService;
    @Mock private UserPointRepository userPointRepository;

    @InjectMocks private PointHistoryService pointHistoryService;
    @Mock private PointHistoryRepository pointHistoryRepository;

    @DisplayName("[성공케이스]")
    @Test()
    public void givenNothing_whenNothing_thenUserPointRepository(){
        // given

        // when

        // then
        assertThat(userPointService).isNotNull();
    }


    @DisplayName("[실패 케이스] 특정 사용자로 포인트 조회시 찾지 못해 실패!")
    @Test()
    public void givenUserId_whenGetPoint_thenThrowPointNotFound(){
        // given
        Long id = 1L;
        doReturn(null).when(userPointRepository).selectById(id);

        //UserPointRepositoryStub userPointRepositoryStub = new UserPointRepositoryStub().setReturn(null).selectById(id);

        //userPointService = new UserPointService(userPointRepositoryStub);


        // when
        UserPointException userPointException = assertThrows(UserPointException.class,()->userPointService.getUserPoint(id));


        // then
        // UserPoint를 찾지못해 조회실패 Exception.
        assertThat(userPointException.getErrorResult()).isEqualTo(UserPointErrorResult.USER_POINT_NOT_FOUND);
    }

    @DisplayName("[실패 케이스] 특정 사용자가 null일 경우 포인트 조회시 실패")
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

    @DisplayName("[성공 케이스] 특정 사용자가 포인트 조회성공")
    @Test()
    public void givenUserId_whenGetPoint_thenGetUserPoint(){
        // given
        Long id = 1L;
        Long amount = 10000L;
        UserPoint resultPoint = new UserPoint(id, amount, System.currentTimeMillis());
        doReturn(resultPoint).when(userPointRepository).selectById(id);

        // when
        UserPoint userPoint = userPointService.getUserPoint(id);

        // then
        assertThat(userPoint).isNotNull();
        assertThat(userPoint.id()).isEqualTo(id);
    }

    @DisplayName("[성공 케이스] 새로운 특정 사용자로 포인트 충전시 성공")
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
        UserPoint chargedUserPoint = userPointService.chargeUserPoint(id,amount);

        // then
        assertThat(chargedUserPoint).isNotNull();
        assertThat(chargedUserPoint.id()).isEqualTo(id);
        assertThat(chargedUserPoint.point()).isEqualTo(amount);
    }

    @DisplayName("[성공 케이스] 기존에 특정 사용자로 포인트 충전시 추가충전 - TODO 점검필요")
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
        UserPoint chargedUserPoint = userPointService.chargeUserPoint(id,newAmount);

        // then
        assertThat(chargedUserPoint).isNotNull();
        assertThat(chargedUserPoint.id()).isEqualTo(id);
        assertThat(chargedUserPoint.point()).isEqualTo(totalAmount);
    }

    @DisplayName("[실패 케이스] 충전값이 음수일 경우")
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

    @DisplayName("[실패 케이스] 충전값이 null일 경우")
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

    @Disabled
    @DisplayName("[실패 케이스] Transaction Type이 null일 경우")
    @Test()
    public void givenUserIdAndAmount_whenChargePoint_thenThrowWrongAmount(){
        // given
        Long id = 1L;
        Long amount = 10000L;

        // when
        // DTO로 만들어?
        UserPointException userPointException = assertThrows(UserPointException.class,()-> userPointService.chargeUserPoint(id,amount));

        // then
        assertThat(userPointException.getErrorResult()).isEqualTo(UserPointErrorResult.WRONG_POINT_AMOUNT);
    }

    @DisplayName("[성공 케이스] 기존에 특정 사용자로 포인트 사용 - TODO 점검필요")
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
        UserPoint chargedUserPoint = userPointService.useUserPoint(id,newAmount);

        // then
        assertThat(chargedUserPoint).isNotNull();
        assertThat(chargedUserPoint.id()).isEqualTo(id);
        assertThat(chargedUserPoint.point()).isEqualTo(resAmount);
    }

    @DisplayName("[실패 케이스] 충전값이 음수일 경우")
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

    @DisplayName("[실패 케이스] 충전값이 null일 경우")
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

    @DisplayName("[실패 케이스] 사용할 사용자가 없을경우")
    @Test()
    public void givenUserIdAndNullAmount_whenUsePoint_thenThrowNotFoundUserPoint(){
        // given
        Long id = 1L;
        Long amount = 10000L;
        doReturn(null).when(userPointRepository).selectById(id);
        // when
        // DTO로 만들어?
        UserPointException userPointException = assertThrows(UserPointException.class,()-> userPointService.useUserPoint(id,amount));

        // then
        assertThat(userPointException.getErrorResult()).isEqualTo(UserPointErrorResult.USER_POINT_NOT_FOUND);
    }

    @DisplayName("[실패 케이스] 사용할 사용자의 포인트가 부족한경우")
    @Test()
    public void givenUserIdAndNullAmount_whenUsePoint_thenThrowNotEnoughPoint(){
        // given
        Long id = 1L;
        Long initAmount = 1000L;
        Long useAmount = 2000L;

        UserPoint initPoint = getNewUserPoint(id,initAmount);

        doReturn(initPoint).when(userPointRepository).selectById(id);

        // when
        // DTO로 만들어?
        UserPointException userPointException = assertThrows(UserPointException.class,()-> userPointService.useUserPoint(id,useAmount));

        // then
        assertThat(userPointException.getErrorResult()).isEqualTo(UserPointErrorResult.NOT_ENOUGH_POINT);
    }


    @DisplayName("[성공 케이스] 특정 사용자가 포인트 히스토리 조회성공")
    @Test()
    public void givenUserId_whenGetPointHistory_thenGetUserPointHistory(){
        // given
        Long id = 1L;
        Long amount = 10000L;


        List<PointHistory> pointHistoryList = List.of(
                new PointHistory(1L, id, TransactionType.CHARGE, amount, System.currentTimeMillis()),
                new PointHistory(2L, id, TransactionType.USE, 2000L, System.currentTimeMillis()),
                new PointHistory(3L, id, TransactionType.CHARGE, 3000L, System.currentTimeMillis()));

        doReturn(pointHistoryList).when(pointHistoryRepository).selectAllByUserId(id);

        // when
        List<PointHistory> result = pointHistoryService.getPointHistory(id);

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(3);
    }

    public UserPoint getNewUserPoint(Long id, Long amount){
        return new UserPoint(id,amount,System.currentTimeMillis());
    }
}
