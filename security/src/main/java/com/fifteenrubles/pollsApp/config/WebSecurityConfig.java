package com.fifteenrubles.pollsApp.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fifteenrubles.pollsApp.dto.UserDto;
import com.fifteenrubles.pollsApp.entity.User;
import com.google.common.collect.ImmutableList;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static com.fifteenrubles.pollsApp.entity.Permission.*;


@EnableWebSecurity
public class WebSecurityConfig {

    private final static String USER_URL = "/user";
    private final static String POLL_URL = "/poll";
    private final static String QUESTION_URL = "/question";
    private final static String QUESTION_WITH_OPTIONS_URL = "/questionWithOptions";
    private final static String ANSWER_URL = "/answer";
    private final static String SELF_URL = "/self";


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    private AuthenticationFailureHandler failureHandler() {
        return (httpServletRequest, httpServletResponse, e) -> {
            httpServletResponse.sendError(401, "Authentication failure");
            httpServletResponse.getWriter().append("Authentication failure");
            httpServletResponse.setStatus(401);
        };
    }

    private AuthenticationSuccessHandler successHandler() {
        return (httpServletRequest, httpServletResponse, authentication) -> {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setUsername(user.getUsername());
            userDto.setPassword("");
            userDto.setRole(user.getAuth().name());
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(userDto);

            httpServletResponse.setStatus(200);
            httpServletResponse.getWriter().append(json);
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedMethods(ImmutableList.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(ImmutableList.of("*"));
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader("*");
        configuration.addAllowedOrigin("http://localhost:4200");
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @Order()
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf().disable()
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers(USER_URL + "/registration").not().fullyAuthenticated()
                //user_self
                .antMatchers(HttpMethod.GET, USER_URL + SELF_URL + "/**").hasAuthority(USER_SELF_READ.getPermission())
                .antMatchers(HttpMethod.PUT, USER_URL + SELF_URL + "/**").hasAuthority(USER_SELF_WRITE.getPermission())
                //user
                .antMatchers(HttpMethod.GET, USER_URL + "/**").hasAuthority(USER_READ.getPermission())
                .antMatchers(HttpMethod.PUT, USER_URL + "/**").hasAuthority(USER_WRITE.getPermission())
                .antMatchers(HttpMethod.POST, USER_URL + "/**").hasAuthority(USER_WRITE.getPermission())
                .antMatchers(HttpMethod.DELETE, USER_URL + "/**").hasAuthority(USER_WRITE.getPermission())
                //poll_self
                .antMatchers(HttpMethod.GET, POLL_URL + SELF_URL + "/allowed_polls").hasAuthority(ALLOWED_POLLS_READ.getPermission())
                .antMatchers(HttpMethod.GET, POLL_URL + SELF_URL + "/**").hasAuthority(POLL_SELF_READ.getPermission())
                .antMatchers(HttpMethod.PUT, POLL_URL + SELF_URL + "/**").hasAuthority(POLL_SELF_WRITE.getPermission())
                .antMatchers(HttpMethod.POST, POLL_URL + SELF_URL + "/**").hasAuthority(POLL_SELF_WRITE.getPermission())
                .antMatchers(HttpMethod.DELETE, POLL_URL + SELF_URL + "/**").hasAuthority(POLL_SELF_WRITE.getPermission())
                //poll
                .antMatchers(HttpMethod.GET, POLL_URL + "/**").hasAuthority(POLL_READ.getPermission())
                .antMatchers(HttpMethod.PUT, POLL_URL + "/**").hasAuthority(POLL_WRITE.getPermission())
                .antMatchers(HttpMethod.POST, POLL_URL + "/**").hasAuthority(POLL_WRITE.getPermission())
                .antMatchers(HttpMethod.DELETE, POLL_URL + "/**").hasAuthority(POLL_WRITE.getPermission())
                //question_self
                .antMatchers(HttpMethod.GET, QUESTION_URL + SELF_URL + "/allowed_polls/**").hasAuthority(ALLOWED_POLLS_READ.getPermission())
                .antMatchers(HttpMethod.GET, QUESTION_URL + SELF_URL + "/**").hasAuthority(QUESTION_SELF_READ.getPermission())
                .antMatchers(HttpMethod.PUT, QUESTION_URL + SELF_URL + "/**").hasAuthority(QUESTION_SELF_WRITE.getPermission())
                .antMatchers(HttpMethod.POST, QUESTION_URL + SELF_URL + "/**").hasAuthority(QUESTION_SELF_WRITE.getPermission())
                .antMatchers(HttpMethod.DELETE, QUESTION_URL + SELF_URL + "/**").hasAuthority(QUESTION_SELF_WRITE.getPermission())
                //question
                .antMatchers(HttpMethod.GET, QUESTION_URL + "/**").hasAuthority(QUESTION_READ.getPermission())
                .antMatchers(HttpMethod.PUT, QUESTION_URL + "/**").hasAuthority(QUESTION_WRITE.getPermission())
                .antMatchers(HttpMethod.POST, QUESTION_URL + "/**").hasAuthority(QUESTION_WRITE.getPermission())
                .antMatchers(HttpMethod.DELETE, QUESTION_URL + "/**").hasAuthority(QUESTION_WRITE.getPermission())
                //questionWithOptions_self
                .antMatchers(HttpMethod.GET, QUESTION_WITH_OPTIONS_URL + SELF_URL + "/allowed_polls/**").hasAuthority(ALLOWED_POLLS_READ.getPermission())
                .antMatchers(HttpMethod.GET, QUESTION_WITH_OPTIONS_URL + SELF_URL + "/**").hasAuthority(QUESTION_SELF_READ.getPermission())
                .antMatchers(HttpMethod.PUT, QUESTION_WITH_OPTIONS_URL + SELF_URL + "/**").hasAuthority(QUESTION_SELF_WRITE.getPermission())
                .antMatchers(HttpMethod.POST, QUESTION_WITH_OPTIONS_URL + SELF_URL + "/**").hasAuthority(QUESTION_SELF_WRITE.getPermission())
                .antMatchers(HttpMethod.DELETE, QUESTION_WITH_OPTIONS_URL + SELF_URL + "/**").hasAuthority(QUESTION_SELF_WRITE.getPermission())
                //questionWithOptions
                .antMatchers(HttpMethod.GET, QUESTION_WITH_OPTIONS_URL + "/**").hasAuthority(QUESTION_READ.getPermission())
                .antMatchers(HttpMethod.PUT, QUESTION_WITH_OPTIONS_URL + "/**").hasAuthority(QUESTION_WRITE.getPermission())
                .antMatchers(HttpMethod.POST, QUESTION_WITH_OPTIONS_URL + "/**").hasAuthority(QUESTION_WRITE.getPermission())
                .antMatchers(HttpMethod.DELETE, QUESTION_WITH_OPTIONS_URL + "/**").hasAuthority(QUESTION_WRITE.getPermission())
                //answer_self
                .antMatchers(HttpMethod.GET, ANSWER_URL + SELF_URL + "/**").hasAuthority(ANSWER_SELF_READ.getPermission())
                .antMatchers(HttpMethod.PUT, ANSWER_URL + SELF_URL + "/**").hasAuthority(ANSWER_SELF_WRITE.getPermission())
                .antMatchers(HttpMethod.POST, ANSWER_URL + SELF_URL + "/**").hasAuthority(ANSWER_SELF_WRITE.getPermission())
                .antMatchers(HttpMethod.DELETE, ANSWER_URL + SELF_URL + "/**").hasAuthority(ANSWER_SELF_WRITE.getPermission())
                //answer
                .antMatchers(HttpMethod.GET, ANSWER_URL + "/**").hasAuthority(ANSWER_READ.getPermission())
                .antMatchers(HttpMethod.PUT, ANSWER_URL + "/**").hasAuthority(ANSWER_WRITE.getPermission())
                .antMatchers(HttpMethod.POST, ANSWER_URL + "/**").hasAuthority(ANSWER_WRITE.getPermission())
                .antMatchers(HttpMethod.DELETE, ANSWER_URL + "/**").hasAuthority(ANSWER_WRITE.getPermission())
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .successHandler(successHandler())
                .failureHandler(failureHandler())
                .permitAll().and()
                .logout()
                .and()
                .build();

    }

}