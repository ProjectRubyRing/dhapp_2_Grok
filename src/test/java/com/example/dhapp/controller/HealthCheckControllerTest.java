package com.example.dhapp.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * GET /api/healthcheck は業務処理をせず 200 を返す。
 */
class HealthCheckControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new HealthCheckController()).build();
    }

    @Test
    void getReturns200WithEmptyBody() throws Exception {
        mockMvc.perform(get("/api/healthcheck"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void getReturns200UnderDeploymentContextPath() throws Exception {
        mockMvc.perform(get("/iwinmichl/api/healthcheck").contextPath("/iwinmichl"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
}
