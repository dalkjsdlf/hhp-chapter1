package io.hhplus.tdd.controller;

import com.google.gson.Gson;
import io.hhplus.tdd.exception.ApiControllerAdvice;
import io.hhplus.tdd.point.controller.UserPointController;
import io.hhplus.tdd.point.dto.PointHistoryResponseDto;
import io.hhplus.tdd.point.dto.UserPointRequestDto;
import io.hhplus.tdd.point.dto.UserPointResponseDto;
import io.hhplus.tdd.point.enumdata.TransactionType;
import io.hhplus.tdd.point.service.UserPointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Point Controller Test]")
@ExtendWith(MockitoExtension.class)
public class UserPointControllerTest {

    private MockMvc mockMvc;

    @InjectMocks private UserPointController pointController;
    @Mock
    private UserPointService userPointService;

    private Gson gson;

    @BeforeEach
    public void init(){
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(pointController)
                .setControllerAdvice(new ApiControllerAdvice())
                .build();
    }

    @DisplayName("[성공] NULL 체크")
    @Test()
    public void givenNothing_whenNothing_thenNotNull(){
        // given

        // when

        // then
        assertThat(pointController).isNotNull();
    }
    @DisplayName("[성공] 사용자의 포인트를 조회")
    @Test()
    public void givenUserId_whenGetPoint_thenPoint() throws Exception {
        // given
        Long userId = 1L;


        Mockito.doReturn(getUserPointResponseDto(userId,2000L))
                .when(userPointService)
                .getUserPoint(1L);
        // when
        ResultActions resultActions = mockMvc
                .perform(get("/point/1")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk());
//        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L));
//        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[0].amount").value(2000L));
    }

    @DisplayName("[성공] 사용자의 포인트 내역을 조회")
    @Test()
    public void givenUserId_whenGetPointHistories_thenPointHistories() throws Exception {
        // given
        Long userId = 1L;

        Mockito.doReturn(
                List.of(getPointHistoryResponseDto(userId, 10000L, TransactionType.CHARGE),
                        getPointHistoryResponseDto(userId, 2000L , TransactionType.USE),
                        getPointHistoryResponseDto(userId, 300L  , TransactionType.CHARGE))
                )
                .when(userPointService).getPointHistory(userId);

        // controller -> service -> reposi
        // when
        ResultActions resultActions = mockMvc
                .perform(get("/point/1/histories")
                        .contentType(MediaType.APPLICATION_JSON));


        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[0].userId").value(1L));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[1].userId").value(1L));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[2].userId").value(1L));
    }


    @DisplayName("[실패] 사용자의 포인트 내역을 조회 (사용자 없음)")
    @Test()
    public void givenUserId_whenGetPointHistories_thenNoPointHistories() throws Exception {
        // given

        //UserPointController userPointController = new UserPointController(new UserPointService(new UserPointRepository(new UserPointTable()),new PointHistoryService(new PointHistoryRepository(new PointHistoryTable()))));

        // when
        ResultActions resultActions = mockMvc
                .perform(get("/point//histories")
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @DisplayName("[실패] 페이지 잘못 접근")
    @Test()
    public void givenNothing_whenRequestByWrongUrl_thenNotFound() throws Exception {
        // given

        //UserPointController userPointController = new UserPointController(new UserPointService(new UserPointRepository(new UserPointTable()),new PointHistoryService(new PointHistoryRepository(new PointHistoryTable()))));

        // when
        ResultActions resultActions = mockMvc
                .perform(get("/poin")
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isNotFound());
    }

    @DisplayName("[성공] 사용자의 포인트를 충전")
    @Test()
    public void givenUserIdAndAmount_whenChargePoint_thenSuccessfullyCharge() throws Exception {
        // given
        Long userId = 1L;

        // when
        ResultActions resultActions = mockMvc
                .perform(patch("/point/1/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(getUserPointRequestDto(10000L))));
        // then
        resultActions.andExpect(status().isCreated());
    }

    @DisplayName("[성공] 사용자의 포인트를 사용")
    @Test()
    public void givenUserIdAndAmount_whenUsePoint_thenSuccessfullyUse() throws Exception {
        Long userId = 1L;

        // when
        ResultActions resultActions = mockMvc
                .perform(patch("/point/1/use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(getUserPointRequestDto(10000L))));

        // then
        resultActions.andExpect(status().isCreated());
    }

    @DisplayName("[실패] 사용자의 포인트를 충전 (음수 입력)")
    @Test()
    public void givenNegativeAmount_whenChargePoint_thenSuccessfullyCharge() throws Exception {
        // given
        Long userId = 1L;

        // when
        ResultActions resultActions = mockMvc
                .perform(patch("/point/1/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(getUserPointRequestDto(-1L))));

        // then
        resultActions.andExpect(status().isBadRequest());
    }


    private UserPointRequestDto getUserPointRequestDto(final Long amount){
        return UserPointRequestDto
                .builder()
                .amount(amount)
                .build();
    }


    private PointHistoryResponseDto getPointHistoryResponseDto
            (
                    final Long userId,
                    final Long amount,
                    final TransactionType type
                    ) {
        return PointHistoryResponseDto
                .builder()
                .userId(userId)
                .amount(amount)
                .type(type)
                .build();
    }

    private UserPointResponseDto getUserPointResponseDto
            (
                    final Long id,
                    final Long amount
            ) {
        return UserPointResponseDto
                .builder()
                .id(id)
                .amount(amount)
                .build();
    }
}
