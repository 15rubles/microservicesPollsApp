package com.fifteenrubles.questionService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Sql(value = {"/sql/poll-content-before.sql", "/sql/user-content-before.sql",
        "/sql/question-content-before.sql",  "/sql/answer-content-before.sql"}, executionPhase = BEFORE_TEST_METHOD)
@Sql(value = {"/sql/poll-content-after.sql", "/sql/user-content-after.sql",
        "/sql/question-content-after.sql","/sql/answer-content-after.sql"}, executionPhase = AFTER_TEST_METHOD)
public class AnswerControllerTest {

    private static final String USERNAME_LEAD = "lead";
    private static final String USERNAME_ADMIN = "admin";
    private static final int answerQuantity = 3;
    private static final int answerSelfQuantity = 2;
    private static final int answerPollQuantity = 2;

    @Container
    public static MySQLContainer container = new MySQLContainer()
            .withUsername("user")
            .withPassword("1111")
            .withDatabaseName("polls_app_test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.username", container::getUsername);
    }

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    @Sql(value = {"/sql/poll-content-before.sql", "/sql/user-content-before.sql",
            "/sql/question-content-before.sql",  "/sql/answer-content-before.sql"}, executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/poll-content-after.sql", "/sql/user-content-after.sql",
            "/sql/question-content-after.sql","/sql/answer-content-after.sql"}, executionPhase = AFTER_TEST_METHOD)
    public void cleanDB(){}

    //positive tests
    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void getAllAnswersTest() throws Exception{
        this.mockMvc.perform(get("/answer/all"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.*", hasSize(answerQuantity)));
    }
    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void getAllAnswersInPollTest() throws Exception{
        this.mockMvc.perform(get("/answer/poll/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.*", hasSize(answerPollQuantity)));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void getAnswerByIdTest() throws Exception{
        this.mockMvc.perform(get("/answer/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(jsonPath("text", is("test1")));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void getAnswersByPollIdAndUserIdTest() throws Exception{
        this.mockMvc.perform(get("/answer?userId=1&pollId=1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.*", hasSize(answerSelfQuantity)));
    }

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void getSelfAnswersByPollIdAndUserIdTest() throws Exception{
        this.mockMvc.perform(get("/answer/self?pollId=1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.*", hasSize(answerSelfQuantity)));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void updateAnswerTest() throws Exception{

        MockHttpServletRequestBuilder requestBuilder = put("/answer/update")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"id\": 1," +
                        "\"poll_id\": 1," +
                        "\"question_id\": 1," +
                        "\"text\": \"test\"," +
                        "\"user_id\": 1" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("text", is("test")));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void addAnswerTest() throws Exception{
        MockHttpServletRequestBuilder requestBuilder = post("/answer/add")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"text\": \"test\"," +
                        "\"pollId\": 1," +
                        "\"questionId\": 1," +
                        "\"userId\": 1" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("text", is("test")));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void deleteAnswerTest() throws Exception{
        this.mockMvc.perform(delete("/answer/delete/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails
    public void addSelfAnswerTest() throws Exception{

        MockHttpServletRequestBuilder requestBuilder = post("/answer/self/add")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"pollId\": 1," +
                        "\"questionId\": 1," +
                        "\"text\": \"test\"" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("text", is("test")));
    }

    @Test
    @WithUserDetails
    public void getSelfAnswersByPollIdTest() throws Exception{
        this.mockMvc.perform(get("/answer/self/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.*", hasSize(answerSelfQuantity)));
    }

    //negative tests

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void getSelfAnswersByPollIdAndUserIdExceptionTest() throws Exception{
        this.mockMvc.perform(get("/answer/self?userId=1&pollId=3"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Poll is not belong to you")));
    }

    @Test
    @WithUserDetails
    public void addSelfAnswerExceptionTest() throws Exception{

        MockHttpServletRequestBuilder requestBuilder = post("/answer/self/add")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"pollId\": 2," +
                        "\"questionId\": 1," +
                        "\"text\": \"test\"" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User don't allowed to this poll")));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void getAnswerByIdExceptionTest() throws Exception{
        this.mockMvc.perform(get("/answer/666"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Answer with id 666 not found")));
    }
}
