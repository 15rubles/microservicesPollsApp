package com.fifteenrubles.questionService.controller;

import com.fifteenrubles.questionService.entity.Question;
import com.fifteenrubles.questionService.exception.ApiRequestException;
import com.fifteenrubles.questionService.service.QuestionService;
import com.fifteenrubles.questionService.service.UserIdExtractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/question")
@RequiredArgsConstructor

public class QuestionController {

    private final QuestionService questionService;


    private static final String PollUrl = "http://poll-service";
    private static final String UserUrl = "http://user-service";
    private final RestTemplate restTemplate;
    private final UserIdExtractorService userIdExtractorService;

    @GetMapping("/{pollId}/all")
    public List<Question> getAllQuestionsInPoll(@Valid @PathVariable("pollId") Long pollId) {
        return questionService.findAllQuestionsByPollId(pollId);
    }

    @GetMapping("/all")
    public List<Question> getAllQuestions() {
        return questionService.findAllQuestions();
    }

    @GetMapping("/{id}")
    public Question findQuestionById(@Valid @PathVariable("id") Long id) {
        return questionService.findQuestionById(id);
    }

    @PutMapping("/update")
    public Question updateQuestion(@Valid @RequestBody Question question) {
        return questionService.updateQuestion(question);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Question addQuestion(@Valid @RequestBody Question question) {
        return questionService.addQuestion(question);
    }

    @DeleteMapping("/delete/{questionId}")
    public void deleteQuestion(@Valid @PathVariable("questionId") Long id) {
        questionService.deleteQuestionById(id);
    }


    @GetMapping("/self/{pollId}/all")
    public List<Question> getAllQuestionsInSelfPoll(@Valid @PathVariable("pollId") Long pollId) {
        Long userId = userIdExtractorService.getUserId();
        Long pollOwnerId = restTemplate.getForObject(
                PollUrl + "/poll/userId/{pollId}",
                Long.class ,pollId);
        if (Objects.equals(pollOwnerId, userId)) {
            return questionService.findAllQuestionsByPollId(pollId);
        }
        throw new ApiRequestException("Poll is not allowed", HttpStatus.FORBIDDEN);
    }

    @PutMapping("/self/update")
    public Question updateSelfQuestion(@Valid @RequestBody Question question) {
        Long userId = userIdExtractorService.getUserId();
        Long pollId = questionService.findQuestionById(question.getId()).getPollId();
        Long pollOwnerId = restTemplate.getForObject(
                PollUrl + "/poll/userId/{pollId}",
                Long.class ,pollId);
        if (Objects.equals(pollOwnerId, userId)) {
            question.setPollId(pollId);
            return questionService.updateQuestion(question);
        }
        throw new ApiRequestException("Poll is not allowed", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/self/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Question addQuestionToSelfPoll(@Valid @RequestBody Question question) {
        Long userId = userIdExtractorService.getUserId();
        Long pollOwnerId = restTemplate.getForObject(
                PollUrl + "/poll/userId/{pollId}",
                Long.class ,question.getPollId());
        if (Objects.equals(pollOwnerId, userId)) {
            return questionService.addQuestion(question);
        }
        throw new ApiRequestException("Poll is not allowed", HttpStatus.FORBIDDEN);

    }

    @DeleteMapping("/self/delete/{questionId}")
    public void deleteQuestionInSelfPoll(@Valid @PathVariable("questionId") Long id) {
        Long userId = userIdExtractorService.getUserId();
        Long pollOwnerId =restTemplate.getForObject(
                PollUrl + "/poll/userId/{pollId}",
                Long.class ,questionService.findQuestionById(id).getPollId());
        if (!Objects.equals(pollOwnerId, userId)) {
            throw new ApiRequestException("Poll is not allowed", HttpStatus.FORBIDDEN);
        }
        questionService.deleteQuestionById(id);
    }

    @GetMapping("/self/allowed_polls/{pollId}")
    public List<Question> getAllowedPollQuestions(@Valid @PathVariable("pollId") Long pollId) {
        Long userId = userIdExtractorService.getUserId();
        List<Long> allowedPollsId = restTemplate.getForObject(
                UserUrl + "/user/userAllowedPolls/{userId}",
                List.class ,userId);
        if (!Objects.requireNonNull(allowedPollsId).contains(pollId)) {
            throw new ApiRequestException("User don't have access to poll", HttpStatus.FORBIDDEN);
        }
        List<Question> questions =  questionService.findAllQuestionsByPollId(pollId);
        for(Question question : questions){
            question.setRightAnswer("");
        }
        return questions;
    }
}
