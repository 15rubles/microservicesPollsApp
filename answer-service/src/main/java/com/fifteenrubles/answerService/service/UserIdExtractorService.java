package com.fifteenrubles.answerService.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserIdExtractorService {
    private static final String SecurityUrl = "http://security";
    private final RestTemplate restTemplate;
    public Long getUserId() {

        return restTemplate.getForObject(
                SecurityUrl + "/userIdFromContext",
                Long.class);
    }
}
