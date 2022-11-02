package com.fifteenrubles.pollsApp;

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
        "/sql/question-content-before.sql"}, executionPhase = BEFORE_TEST_METHOD)
@Sql(value = {"/sql/poll-content-after.sql", "/sql/user-content-after.sql",
        "/sql/question-content-after.sql"}, executionPhase = AFTER_TEST_METHOD)
public class QuestionControllerTest {
    private static final String USERNAME_LEAD = "lead";
    private static final String USERNAME_ADMIN = "admin";
    private static final int questionQuantity = 3;
    private static final int questionSelfQuantity = 2;
    private static final int allowedPollQuestionsSelfQuantity = 2;
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
            "/sql/question-content-before.sql"}, executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/poll-content-after.sql", "/sql/user-content-after.sql",
            "/sql/question-content-after.sql"}, executionPhase = AFTER_TEST_METHOD)
    public void cleanDB(){}

    //positive tests
    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void getAllQuestionsTest() throws Exception{
        this.mockMvc.perform(get("/question/all"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.*", hasSize(questionQuantity)));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void getAllQuestionsInPollTest() throws Exception{
        this.mockMvc.perform(get("/question/1/all"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.*", hasSize(questionSelfQuantity)));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void getQuestionsById() throws Exception{
        this.mockMvc.perform(get("/question/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(jsonPath("text", is("text1")));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void updateQuestionTest() throws Exception{

        MockHttpServletRequestBuilder requestBuilder = put("/question/update")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"id\": 1," +
                        "\"pollId\": 1," +
                        "\"text\": \"test\"," +
                        "\"rightAnswer\": \"ans\"" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("text", is("test")));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void addQuestionTest() throws Exception{
        MockHttpServletRequestBuilder requestBuilder = post("/question/add")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"text\": \"test\"," +
                        "\"pollId\": 1," +
                        "\"rightAnswer\": \"ans\"" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("text", is("test")));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void deleteQuestionTest() throws Exception{
        this.mockMvc.perform(delete("/question/delete/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void getAllQuestionsInSelfPollTest() throws Exception{
        this.mockMvc.perform(get("/question/self/1/all"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.*", hasSize(questionSelfQuantity)));
    }

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void updateSelfQuestionTest() throws Exception{

        MockHttpServletRequestBuilder requestBuilder = put("/question/self/update")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"id\": 1,"+
                        "\"text\": \"test\"," +
                        "\"pollId\": 1," +
                        "\"rightAnswer\": \"ans\"" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("text", is("test")));
    }

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void addSelfQuestionTest() throws Exception{

        MockHttpServletRequestBuilder requestBuilder = post("/question/self/add")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"text\": \"test\"," +
                        "\"pollId\": 1," +
                        "\"rightAnswer\": \"ans\"" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("text", is("test")));
    }

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void deleteSelfQuestionTest() throws Exception{
        this.mockMvc.perform(delete("/question/self/delete/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails
    public void getAllowedPollQuestionsTest() throws Exception{
        this.mockMvc.perform(get("/question/self/allowed_polls/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.*", hasSize(allowedPollQuestionsSelfQuantity)));
    }

    //negative tests

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void getAllQuestionsInSelfPollExceptionTest() throws Exception{
        this.mockMvc.perform(get("/question/self/3/all"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Poll is not allowed")));
    }

    @Test
    @WithUserDetails
    public void updateSelfQuestionExceptionTest() throws Exception{

        MockHttpServletRequestBuilder requestBuilder = put("/question/self/update")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"id\": 1," +
                        "\"pollId\": 3," +
                        "\"text\": \"test\"," +
                        "\"rightAnswer\": \"ans\"" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void addSelfQuestionExceptionTest() throws Exception{

        MockHttpServletRequestBuilder requestBuilder = post("/question/self/add")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"text\": \"test\"," +
                        "\"pollId\": 3," +
                        "\"rightAnswer\": \"ans\"" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Poll is not allowed")));
    }

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void deleteSelfQuestionExceptionTest() throws Exception{
        this.mockMvc.perform(delete("/question/self/delete/3"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Poll is not allowed")));
    }

    @Test
    @WithUserDetails
    public void getAllowedPollQuestionsExceptionTest() throws Exception{
        this.mockMvc.perform(get("/question/self/allowed_polls/2"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("User don't have access to poll")));
    }


}
