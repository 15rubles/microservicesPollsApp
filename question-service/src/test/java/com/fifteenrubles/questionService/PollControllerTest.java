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
@Sql(value = {"/sql/poll-content-before.sql", "/sql/user-content-before.sql"}, executionPhase = BEFORE_TEST_METHOD)
@Sql(value = {"/sql/poll-content-after.sql", "/sql/user-content-after.sql"}, executionPhase = AFTER_TEST_METHOD)
public class PollControllerTest {
    private static final String USERNAME_LEAD = "lead";
    private static final String USERNAME_ADMIN = "admin";
    private static final int pollQuantity = 3;
    private static final int pollSelfQuantity = 2;
    private static final int userAllowedPollsQuantity = 2;
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
    @Sql(value = {"/sql/poll-content-before.sql", "/sql/user-content-before.sql"}, executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/poll-content-after.sql", "/sql/user-content-after.sql"}, executionPhase = AFTER_TEST_METHOD)
    public void cleanDB(){}


    //positive tests
    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void getAllPollsTest() throws Exception{
        this.mockMvc.perform(get("/poll/all"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.*", hasSize(pollQuantity)));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void findPollByIdTest() throws Exception{
        this.mockMvc.perform(get("/poll/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(jsonPath("name", is("test1")));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void addPollTest() throws Exception{
        MockHttpServletRequestBuilder requestBuilder = post("/poll/add")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"name\": \"test\"," +
                        "\"owner_user_id\": 1" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name", is("test")));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void deletePollTest() throws Exception{
        this.mockMvc.perform(delete("/poll/delete/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void updatePollTest() throws Exception{

        MockHttpServletRequestBuilder requestBuilder = put("/poll/update")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"id\": 1," +
                        "\"is_deleted\": 0," +
                        "\"name\": \"test\"," +
                        "\"owner_user_id\": 2" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("test")));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void findSelfPollByIdTest() throws Exception{
        this.mockMvc.perform(get("/poll/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(jsonPath("name", is("test1")));
    }

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void getAllSelfPollsTest() throws Exception{
        this.mockMvc.perform(get("/poll/self/all"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.*", hasSize(pollSelfQuantity)));
    }

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void getSelfPollTest() throws Exception{
        this.mockMvc.perform(get("/poll/self/2"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(jsonPath("name", is("test2")));
    }

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void addSelfPollTest() throws Exception{

        MockHttpServletRequestBuilder requestBuilder = post("/poll/self/add")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"name\": \"test\"" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name", is("test")));
    }

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void deleteSelfPollTest() throws Exception{
        this.mockMvc.perform(delete("/poll/self/delete/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void updateSelfPollTest() throws Exception{

        MockHttpServletRequestBuilder requestBuilder = put("/poll/self/update")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"id\": 1,"+
                        "\"name\": \"test\"" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("test")));
    }

    @Test
    @WithUserDetails
    public void getAllUserAllowedPollsTest() throws Exception{
        this.mockMvc.perform(get("/poll/self/allowed_polls"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.*", hasSize(userAllowedPollsQuantity)));
    }

    //negative tests

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void getSelfPollExceptionTest() throws Exception{
        this.mockMvc.perform(get("/poll/self/3"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Poll is not belong to you")));
    }

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void deleteSelfPollExceptionTest() throws Exception{
        this.mockMvc.perform(delete("/poll/self/delete/3"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Poll is not belong to you")));
    }

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void updateSelfPollExceptionTest() throws Exception{

        MockHttpServletRequestBuilder requestBuilder = put("/poll/self/update")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"id\": 3,"+
                        "\"name\": \"test\"" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Poll is not belong to you")));
    }

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void getAllUserAllowedPollsExceptionTest() throws Exception{
        this.mockMvc.perform(get("/poll/self/allowed_polls"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User don't have allowed polls")));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void findPollByIdExceptionTest() throws Exception{
        this.mockMvc.perform(get("/poll/10"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Poll with id 10 not found")));
    }
}
