package io.hhplus.tdd.repository;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.data.UserPoint;
import io.hhplus.tdd.point.repository.IUserPointRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
/**
 * @Desc UserPoint Repository Test
 * 1) null test
 * 2) selectById
 * 3) insertAndUpdate
 */
@DisplayName("[Repository Test]")


public class UserPointRepositoryTest {

    private IUserPointRepository userPointRepository;


    @BeforeEach
    public void beforeAction(){
        userPointRepository = new UserPointRepository(new UserPointTable());
    }

    @DisplayName("[성공 케이스] userPointRepository not null 검사")
    @Test()
    public void givenNothing_whenNothing_thenUserPointRepositoryNotnull(){
        // given

        // when

        // then
        assertThat(userPointRepository).isNotNull();
    }

    @DisplayName("[성공 케이스] User Point 입력시 존재하지 않는 ID로 입력하면 정상적으로 새로 입력")
    @Test()
    public void givenUserPoint_whenInsertById_thenSuccessfullyInsert(){
        // given
        Long id = 1L;
        Long amount = 10000L;

        // when
        UserPoint savedPoint    = userPointRepository.save(id, amount);
        UserPoint selectedPoint = userPointRepository.selectById(1L);

        // then
        // 저장이 잘 되었는지
        // 저장된 데이터 조회가 잘 되었는지
        // 두 데이터가 동일한지
        assertThat(savedPoint).isNotNull();
        assertThat(selectedPoint).isNotNull();
        assertThat(savedPoint).isEqualTo(selectedPoint);
    }

    @DisplayName("[성공 케이스] User Point 입력시 동일 ID가 입력되면 UPDATE 된다.")
    @Test()
    public void givenUserPoint_whenInsertByDupId_thenThrowDupException(){
        // given
        Long id = 1L;
        Long amount = 10000L;

        // when
        userPointRepository.save(id, amount);
        UserPoint savedPoint2     = userPointRepository.save(id, amount * 2);

        UserPoint savedPointInMap = userPointRepository.selectById(1L);

        // then
        // 두 번째 데이터가 저장될 때 동일한 ID로 저장하면 UPDATE 된다.
        assertThat(savedPointInMap.point()).isEqualTo(savedPoint2.point());
    }


}
