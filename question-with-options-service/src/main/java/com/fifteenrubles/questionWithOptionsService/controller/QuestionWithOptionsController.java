package com.fifteenrubles.questionWithOptionsService.controller;

import com.fifteenrubles.questionWithOptionsService.entity.QuestionWithOptions;
import com.fifteenrubles.questionWithOptionsService.exception.ApiRequestException;
import com.fifteenrubles.questionWithOptionsService.service.QuestionWithOptionsService;
import com.fifteenrubles.questionWithOptionsService.service.UserIdExtractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/questionWithOptions")
@RequiredArgsConstructor

public class QuestionWithOptionsController {

    private final QuestionWithOptionsService questionWithOptionsService;

    private static final String PollUrl = "http://poll-service";
    private static final String UserUrl = "http://user-service";
    private final RestTemplate restTemplate;
    private final UserIdExtractorService userIdExtractorService;

    @GetMapping("/{pollId}/all")
    public List<QuestionWithOptions> getAllQuestionsInPoll(@Valid @PathVariable("pollId") Long pollId) {
        return questionWithOptionsService.findAllQuestionsByPollId(pollId);
    }

    @GetMapping("/all")
    public List<QuestionWithOptions> getAllQuestions() {
        return questionWithOptionsService.findAllQuestions();
    }

    @GetMapping("/{id}")
    public QuestionWithOptions findQuestionById(@Valid @PathVariable("id") Long id) {
        return questionWithOptionsService.findQuestionById(id);
    }

    @PutMapping("/update")
    public QuestionWithOptions updateQuestion(@Valid @RequestBody QuestionWithOptions question) {
        return questionWithOptionsService.updateQuestion(question);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionWithOptions addQuestion(@Valid @RequestBody QuestionWithOptions question) {
        return questionWithOptionsService.addQuestion(question);
    }

    @DeleteMapping("/delete/{questionId}")
    public void deleteQuestion(@Valid @PathVariable("questionId") Long id) {
        questionWithOptionsService.deleteQuestionById(id);
    }


    @GetMapping("/self/{pollId}/all")
    public List<QuestionWithOptions> getAllQuestionsInSelfPoll(@Valid @PathVariable("pollId") Long pollId) {
        Long userId = userIdExtractorService.getUserId();
        Long pollOwnerId = restTemplate.getForObject(
                PollUrl + "/poll/userId/{pollId}",
                Long.class ,pollId);
        if (Objects.equals(pollOwnerId, userId)) {
            return questionWithOptionsService.findAllQuestionsByPollId(pollId);
        }
        throw new ApiRequestException("Poll is not allowed", HttpStatus.FORBIDDEN);
    }

    @PutMapping("/self/update")
    public QuestionWithOptions updateSelfQuestion(@Valid @RequestBody QuestionWithOptions question) {
        Long userId = userIdExtractorService.getUserId();
        Long pollId = questionWithOptionsService.findQuestionById(question.getId()).getPollId();
        Long pollOwnerId = restTemplate.getForObject(
                PollUrl + "/poll/userId/{pollId}",
                Long.class ,pollId);
        if (Objects.equals(pollOwnerId, userId)) {
            question.setPollId(pollId);
            return questionWithOptionsService.updateQuestion(question);
        }
        throw new ApiRequestException("Poll is not allowed", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/self/add")
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionWithOptions addQuestionToSelfPoll(@Valid @RequestBody QuestionWithOptions question) {
        Long userId = userIdExtractorService.getUserId();
        Long pollOwnerId = restTemplate.getForObject(
                PollUrl + "/poll/userId/{pollId}",
                Long.class ,question.getPollId());
        if (Objects.equals(pollOwnerId, userId)) {
            return questionWithOptionsService.addQuestion(question);
        }
        throw new ApiRequestException("Poll is not allowed", HttpStatus.FORBIDDEN);

    }

    @DeleteMapping("/self/delete/{questionId}")
    public void deleteQuestionInSelfPoll(@Valid @PathVariable("questionId") Long id) {
        Long userId = userIdExtractorService.getUserId();
        Long pollOwnerId = restTemplate.getForObject(
                PollUrl + "/poll/userId/{pollId}",
                Long.class ,questionWithOptionsService.findQuestionById(id).getPollId());
        if (!Objects.equals(pollOwnerId, userId)) {
            throw new ApiRequestException("Poll is not allowed", HttpStatus.FORBIDDEN);
        }
        questionWithOptionsService.deleteQuestionById(id);
    }

    @GetMapping("/self/allowed_polls/{pollId}")
    public List<QuestionWithOptions> getAllowedPollQuestions(@Valid @PathVariable("pollId") Long pollId) {
        Long userId = userIdExtractorService.getUserId();
        List<Long> allowedPollsId = restTemplate.getForObject(
                UserUrl + "/user/userAllowedPolls/{userId}",
                List.class ,userId);
        if (!Objects.requireNonNull(allowedPollsId).contains(pollId)) {
            throw new ApiRequestException("User don't have access to poll", HttpStatus.FORBIDDEN);
        }
        List<QuestionWithOptions> questions =  questionWithOptionsService.findAllQuestionsByPollId(pollId);
        for(QuestionWithOptions question : questions){
            question.setRightAnswer("");
        }
        return questions;
    }
}
