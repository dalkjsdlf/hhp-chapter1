package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.data.PointHistory;
import io.hhplus.tdd.point.data.UserPoint;
import io.hhplus.tdd.point.service.UserPointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/point")
@RestController
public class PointController {

    private final UserPointService userPointService;

    public PointController(@Autowired UserPointService userPointService) {
        this.userPointService = userPointService;
    }

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPoint point(@PathVariable(name ="id") Long id) {
        return userPointService.getUserPoint(id);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(@PathVariable(name ="id") Long id) {
        return userPointService.getPointHistory(id);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(@PathVariable(name ="id") Long id
                          , @RequestBody  Long amount) {
       return userPointService.chargeUserPoint(id, amount);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(@PathVariable(name ="id") Long id
                       , @RequestBody  Long amount) {

        return userPointService.useUserPoint(id, amount);
    }
}
