package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.exception.UserPointErrorResult;
import io.hhplus.tdd.exception.UserPointException;
import io.hhplus.tdd.point.data.PointHistory;
import io.hhplus.tdd.point.data.UserPoint;
import io.hhplus.tdd.point.dto.PointHistoryResponseDto;
import io.hhplus.tdd.point.dto.UserPointRequestDto;
import io.hhplus.tdd.point.dto.UserPointResponseDto;
import io.hhplus.tdd.point.service.UserPointService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/point")
@RestController
public class UserPointController {

    private final UserPointService userPointService;

    public UserPointController(@Autowired UserPointService userPointService) {
        this.userPointService = userPointService;
    }

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public ResponseEntity<UserPointResponseDto> point(@PathVariable(name ="id") Long id) {

        return ResponseEntity.ok(userPointService.getUserPoint(id));
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public ResponseEntity<List<PointHistoryResponseDto>> history(@PathVariable(name ="id") Long id) {
        if(id == null){
            throw new UserPointException(UserPointErrorResult.WRONG_USER_ID);
        }
        return ResponseEntity.ok(userPointService.getPointHistory(id));
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public ResponseEntity<UserPointResponseDto> charge(@PathVariable(name ="id") Long id
                          , @Valid @RequestBody UserPointRequestDto userPointRequestDto) {
        UserPointResponseDto userPointResponseDto = userPointService.chargeUserPoint(id, userPointRequestDto.getAmount());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userPointResponseDto);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public ResponseEntity<UserPointResponseDto> use(@PathVariable(name ="id") Long id
                       , @Valid @RequestBody UserPointRequestDto userPointRequestDto) {
        UserPointResponseDto userPointResponseDto = userPointService.useUserPoint(id, userPointRequestDto.getAmount());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userPointResponseDto);
    }
}
