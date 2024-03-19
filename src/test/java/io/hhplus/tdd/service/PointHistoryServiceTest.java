package io.hhplus.tdd.service;

import io.hhplus.tdd.exception.UserPointErrorResult;
import io.hhplus.tdd.exception.UserPointException;
import io.hhplus.tdd.point.data.PointHistory;
import io.hhplus.tdd.point.enumdata.TransactionType;
import io.hhplus.tdd.point.repository.IPointHistoryRepository;
import io.hhplus.tdd.point.service.PointHistoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

/**
 * [성공] NULL 검사
 * [성공] 사용자의 point history 조회
 * [실패] 사용자의 point history 조회 (사용자가 없음)
 * [실패] 사용자의 point history 조회 (사용자가 null로 입력)
 * [성공] 사용자의 충전 point history 저장
 * [성공] 사용자의 사용 point history 저장
 * [실패] 사용자의 point history 저장(사용자 ID가 null) - 충전 트랜젝션으로 갈음
 * */
@DisplayName("[Point History Service Test]")
@ExtendWith(MockitoExtension.class)
public class PointHistoryServiceTest {

    @InjectMocks
    private PointHistoryService pointHistoryService;
    @Mock
    private IPointHistoryRepository pointHistoryRepository;

    @DisplayName("[성공] NULL 검사")
    @Test()
    public void givenNothing_whenNothing_thenServiceAndRepositoryNotNull() {
        // given

        // when

        // then
        assertThat(pointHistoryService).isNotNull();
        assertThat(pointHistoryRepository).isNotNull();

    }

    @DisplayName("[성공] 사용자의 point history 조회")
    @Test()
    public void givenUserId_whenGetPointHistoryByUserId_thenPointHistory() {
        // given
        Long userId = 1L;

        doReturn(
                List.of(
                        getNewPointHistory(1L, userId, TransactionType.CHARGE, 10000L),
                        getNewPointHistory(2L, userId, TransactionType.CHARGE, 20000L),
                        getNewPointHistory(3L, userId, TransactionType.USE, 10000L)
                )
        ).when(pointHistoryRepository).selectAllByUserId(userId);

        // when
        List<PointHistory> pointHistories = pointHistoryService.getPointHistory(userId);

        // then
        assertThat(pointHistories.size()).isEqualTo(3);
    }

    @DisplayName("[실패] 사용자의 point history 조회 (사용자가 없음)")
    @Test()
    public void givenUserId_whenGetPointHistoryByUserId_thenThrowNotFoundUser() {
        // given
        Long userId = 1L;

        doReturn(
                List.of(
                        getNewPointHistory(1L, userId, TransactionType.CHARGE, 10000L),
                        getNewPointHistory(2L, userId, TransactionType.CHARGE, 20000L),
                        getNewPointHistory(3L, userId, TransactionType.USE, 10000L)
                )
        ).when(pointHistoryRepository).selectAllByUserId(userId);

        // when
        List<PointHistory> pointHistories = pointHistoryService.getPointHistory(userId);

        // then
        assertThat(pointHistories.size()).isEqualTo(3);
    }

    @DisplayName("[실패] 사용자의 point history 조회 (사용자가 null로 입력)")
    @Test()
    public void givenNullUserId_whenGetPointHistoryByUserId_thenThrowWrongUserId() {
        // given
        Long userId = 1L;
        doReturn(new ArrayList<PointHistory>()).when(pointHistoryRepository).selectAllByUserId(userId);

        // when
        UserPointException userPointException = assertThrows(UserPointException.class, () -> pointHistoryService.getPointHistory(userId));

        // then
        assertThat(userPointException.getErrorResult()).isEqualTo(UserPointErrorResult.USER_POINT_NOT_FOUND);
    }

    @DisplayName("[성공] 사용자의 충전 point history 저장")
    @Test()
    public void givenPointHistory_whenSavePointHistoryOfCharge_thenSuccessfullySave() {
        // given
        Long userId = 1L;
        Long amount = 10000L;
        TransactionType type = TransactionType.CHARGE;

        PointHistory pointHistory = getNewPointHistory(1L, userId, type, amount);

        doReturn(pointHistory).when(pointHistoryRepository).insert(
                userId,
                amount,
                type);

        // when
        PointHistory result = pointHistoryService.save(userId, type, amount);

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.amount()).isEqualTo(amount);
    }

    @DisplayName("[성공] 사용자의 사용 point history 저장")
    @Test()
    public void givenPointHistory_whenSavePointHistoryOfUse_thenSuccessfullySave() {
        // given
        Long userId = 1L;
        Long amount = 10000L;
        TransactionType type = TransactionType.USE;

        PointHistory pointHistory = getNewPointHistory(1L, userId, type, amount);

        doReturn(pointHistory).when(pointHistoryRepository).insert(
                userId,
                amount,
                type);

        // when
        PointHistory result = pointHistoryService.save(userId, type, amount);

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.amount()).isEqualTo(amount);
    }

    @DisplayName("[실패] 사용자의 point history 저장(사용자 ID가 null) - 충전 트랜젝션으로 갈음")
    @Test()
    public void givenPointHistory_whenSavePointHistory_thenThrowWrongUserId() {
        // given
        Long userId = null;

        // when
        UserPointException userPointException = assertThrows(UserPointException.class, () -> pointHistoryService.save(userId, TransactionType.CHARGE, 1000L));

        // then
        assertThat(userPointException.getErrorResult()).isEqualTo(UserPointErrorResult.WRONG_USER_ID);
    }

    @DisplayName("[실패] 사용자의 point history 저장(Amount가 음수) - 충전 트랜젝션으로 갈음")
    @Test()
    public void givenPointHistory_whenSavePointHistory_thenThrowWrongPoint() {
        // given
        Long userId = 1L;
        Long amount = -1L;

        // when
        UserPointException userPointException = assertThrows(UserPointException.class, () -> pointHistoryService.save(userId, TransactionType.CHARGE, amount));

        // then
        assertThat(userPointException.getErrorResult()).isEqualTo(UserPointErrorResult.WRONG_POINT_AMOUNT);
    }

    private PointHistory getNewPointHistory(Long id, Long userId, TransactionType transactionType, Long amount) {
        return new PointHistory(id, userId, transactionType, amount, System.currentTimeMillis());
    }
}
