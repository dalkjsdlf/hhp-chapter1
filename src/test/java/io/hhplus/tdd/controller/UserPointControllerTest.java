package io.hhplus.tdd.controller;

import io.hhplus.tdd.point.controller.PointController;
import io.hhplus.tdd.point.service.UserPointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("[Point Controller Test]")
@ExtendWith(MockitoExtension.class)
public class UserPointControllerTest {

    private MockMvc mockMvc;

    @InjectMocks private PointController pointController;
    @Mock
    private UserPointService userPointService;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(pointController)
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

        // when
        ResultActions resultActions = mockMvc
                .perform(get("/point/1")
                .contentType(MediaType.APPLICATION_JSON));


        // then
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("[성공] 사용자의 포인트 내역을 조회")
    @Test()
    public void givenUserId_whenGetPointHistories_thenPointHistories() throws Exception {
        // given
        Long userId = 1L;

        // when
        ResultActions resultActions = mockMvc
                .perform(get("/point/1/histories")
                        .contentType(MediaType.APPLICATION_JSON));


        // then
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("[성공] 사용자의 포인트를 충전")
    @Test()
    public void givenUserIdAndAmount_whenChargePoint_thenSuccessfullyCharge() throws Exception {
        // given
        Long userId = 1L;

        // when
        ResultActions resultActions = mockMvc
                .perform(patch("/point/1")
                        .contentType(MediaType.APPLICATION_JSON).content());


        // then
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("[성공] 사용자의 포인트를 사용")
    @Test()
    public void givenUserIdAndAmount_whenUsePoint_thenSuccessfullyUse(){
        // given

        // when

        // then
    }
}
