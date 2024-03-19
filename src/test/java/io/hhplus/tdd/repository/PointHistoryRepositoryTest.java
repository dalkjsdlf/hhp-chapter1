package io.hhplus.tdd.repository;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.data.PointHistory;
import io.hhplus.tdd.point.data.UserPoint;
import io.hhplus.tdd.point.enumdata.TransactionType;
import io.hhplus.tdd.point.repository.IPointHistoryRepository;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/*
* 1. null
* 2. 입력하는 성공
* 3. 사용자 ID로 조회성공
**/

/**
 * @Desc UserPoint Repository Test
 * 1)
 */
@DisplayName("[Repository Test]")

public class PointHistoryRepositoryTest {

    private IPointHistoryRepository pointHistoryRepository;

    @BeforeEach
    public void beforeAction(){
        pointHistoryRepository = new PointHistoryRepository(new PointHistoryTable());
    }

    @DisplayName("[성공 케이스] pointHistoryRepository not null 검사")
    @Test()
    public void givenNothing_whenNothing_thenPointHistoryRepositoryNotnull(){
        // given

        // when

        // then
        assertThat(pointHistoryRepository).isNotNull();
    }



    @DisplayName("[성공 케이스] PointHistory UserId로 입력")
    @Test()
    public void givenUserPoint_whenInsertByUserPoint_thenSuccessfullyInsert(){
        // given
        Long id = 1L;
        Long amount = 10000L;

        // when
        PointHistory savedPointHistory    = pointHistoryRepository.insert(id,amount,TransactionType.CHARGE,System.currentTimeMillis());

        // then
        // 저장이 잘 되었는지
        assertThat(savedPointHistory).isNotNull();
    }

    @DisplayName("[성공 케이스] PointHistory 입력시 동일 ID가 입력되면 UPDATE 된다.")
    @Test()
    public void givenUserPoint_whenSelectALlByUserId_thenGetPointHistory(){
        // given
        Long id = 1L;

        // when
        pointHistoryRepository.insert(id,10000L,TransactionType.CHARGE,System.currentTimeMillis());
        pointHistoryRepository.insert(id,500L,TransactionType.USE,System.currentTimeMillis());
        pointHistoryRepository.insert(id,1000L,TransactionType.CHARGE,System.currentTimeMillis());

        List<PointHistory> selectedPointHistoryByUserId = pointHistoryRepository.selectAllByUserId(id);

        // then
        // 같은 ID로 3건 조회되어야 한다.
        assertThat(selectedPointHistoryByUserId.size()).isEqualTo(3);
    }


}
