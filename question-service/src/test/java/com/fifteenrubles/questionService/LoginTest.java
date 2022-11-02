package com.fifteenrubles.questionService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class LoginTest {
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

    //positive tests //todo
   // @Test
    public void correctLoginTest() throws Exception{
        this.mockMvc.perform(formLogin().user("admin").password("123"))
                .andDo(print())
                .andExpect(jsonPath("username", is("admin")));;
    }

    //negative tests
    @Test
    public void loginTestException() throws Exception {
        this.mockMvc.perform(get("/user/self"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }



}
