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
@Sql(value = {"/sql/user-content-before.sql", "/sql/poll-content-before.sql"}, executionPhase = BEFORE_TEST_METHOD)
@Sql(value = {"/sql/user-content-after.sql", "/sql/poll-content-after.sql"}, executionPhase = AFTER_TEST_METHOD)
public class UserControllerTest {

    private static final String USERNAME_USER = "user";
    private static final String USERNAME_LEAD = "lead";
    private static final String USERNAME_ADMIN = "admin";
    private static final int userQuantity = 3;

    @Autowired
    private MockMvc mockMvc;
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
    @AfterEach
    @Sql(value = {"/sql/user-content-before.sql", "/sql/poll-content-before.sql"}, executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/user-content-after.sql", "/sql/poll-content-after.sql"}, executionPhase = AFTER_TEST_METHOD)
    public void cleanDB(){}

    //positive tests
    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void allUsersTest() throws Exception{
        this.mockMvc.perform(get("/user/all"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(jsonPath("$.*", hasSize(userQuantity)));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void findUserTest() throws Exception{
        this.mockMvc.perform(get("/user/find/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(jsonPath("username", is(USERNAME_USER)));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void addUserTest() throws Exception{
        MockHttpServletRequestBuilder requestBuilder = post("/user/add")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"username\": \"test\"," +
                        "\"password\": \"123\"," +
                        "\"role\": \"USER\"" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("username", is("test")));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void updateUserTest() throws Exception{
        MockHttpServletRequestBuilder requestBuilder = put("/user/update")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"id\": 1," +
                        "\"username\": \"test\"," +
                        "\"password\": \"1111\"," +
                        "\"role\": \"USER\"" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("username", is("test")));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void deleteUserTest() throws Exception{
        this.mockMvc.perform(delete("/user/delete/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }


    @Test
    @WithUserDetails
    public void getSelfUserTest() throws Exception{
        this.mockMvc.perform(get("/user/self"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(jsonPath("username", is(USERNAME_USER)));
    }

    @Test
    @WithUserDetails
    public void updateSelfUserTest() throws Exception{

        MockHttpServletRequestBuilder requestBuilder = put("/user/self/update")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"username\": \"test\"," +
                        "\"password\": \"123\"" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("username", is("test")));
    }

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void  updateUserAllowedPollsTest() throws Exception{
        MockHttpServletRequestBuilder requestBuilder =  put("/user/self/update_user_allowed_polls?pollId=2&isAllowed=true")
                .content("user");
        this.mockMvc.perform( requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("true")));
    }

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void  updateUserAllowedPollsTest2() throws Exception{
        MockHttpServletRequestBuilder requestBuilder =  put("/user/self/update_user_allowed_polls?pollId=1&isAllowed=false")
                .content("user");
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("false")));
    }

    @Test
    public void registrationTest() throws Exception{

        MockHttpServletRequestBuilder requestBuilder = post("/user/registration")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"username\": \"test\"," +
                        "\"password\": \"123\"" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("username", is("test")));
    }

    //negative tests

    @Test
    @WithUserDetails
    public void updateSelfUserExceptionTest() throws Exception{

        MockHttpServletRequestBuilder requestBuilder = put("/user/self/update")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"username\": \"lead\"," +
                        "\"password\": \"123\"" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Username is taken")));
    }

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void  updateUserAllowedPollsDontOwnPollExceptionTest() throws Exception{
        MockHttpServletRequestBuilder requestBuilder =  put("/user/self/update_user_allowed_polls?pollId=3&isAllowed=true")
                .content("user");
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("User dont own the poll")));
    }

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void  updateUserAllowedPollsAlreadyAllowedToPollExceptionTest() throws Exception{
        MockHttpServletRequestBuilder requestBuilder =  put("/user/self/update_user_allowed_polls?pollId=1&isAllowed=true")
                .content("user");
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User already allowed to poll")));
    }

    @Test
    @WithUserDetails(USERNAME_LEAD)
    public void  updateUserAllowedPollsAlreadyDontAllowedToPollExceptionTest() throws Exception{
        MockHttpServletRequestBuilder requestBuilder =  put("/user/self/update_user_allowed_polls?pollId=2&isAllowed=false")
                .content("user");
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User already dont allowed to poll")));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void findUserExceptionTest() throws Exception{
        this.mockMvc.perform(get("/user/find/666"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User with id 666 not found")));
    }

    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void addUserExceptionTest() throws Exception{
        MockHttpServletRequestBuilder requestBuilder = post("/user/add")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{" +
                        "\"username\": \"user\"," +
                        "\"password\": \"123\"," +
                        "\"role\": \"USER\"" +
                        "}");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User with this username exist")));
    }
    @Test
    @WithUserDetails(USERNAME_ADMIN)
    public void deleteUserExceptionTest() throws Exception{
        this.mockMvc.perform(delete("/user/delete/3"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("You dont have permissions to delete this user")));
    }
}