package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.data.PointHistory;
import io.hhplus.tdd.point.data.UserPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequestMapping("/point")
@RestController
public class PointController {
    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPoint point(@PathVariable Long id) {
        // 특정유저의 포인트를 조회한다고 하는데 id를 부여했어
        // id가 userid라고 이해해도 되겠지?
        // 동일한 id로 입력하려고 할떄 오류
        // id로 insert update
        // 엉뚱한 id로 select해서 실패하게 하는것
        // id로 select 성공

        return new UserPoint(0L, 0L, 0L);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(@PathVariable Long id) {
        return Collections.emptyList();
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(@PathVariable Long id
                          , @RequestBody  Long amount) {
        return new UserPoint(0L, 0L, 0L);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(@PathVariable Long id
                       , @RequestBody  Long amount) {

        return new UserPoint(0L, 0L, 0L);
    }
}
