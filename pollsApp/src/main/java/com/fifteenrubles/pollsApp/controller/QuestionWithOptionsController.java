package com.fifteenrubles.pollsApp.controller;

import com.fifteenrubles.pollsApp.entity.QuestionWithOptions;
import com.fifteenrubles.pollsApp.exception.ApiRequestException;
import com.fifteenrubles.pollsApp.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/questionWithOptions")
@RequiredArgsConstructor

public class QuestionWithOptionsController {

    private final QuestionWithOptionsService questionWithOptionsService;

    private final UserService userService;
    private final PollService pollService;
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
        Long pollOwnerId = pollService.findPollById(pollId).getOwnerUserId();
        if (pollOwnerId.equals(userId)) {
            return questionWithOptionsService.findAllQuestionsByPollId(pollId);
        }
        throw new ApiRequestException("Poll is not allowed", HttpStatus.FORBIDDEN);
    }

    @PutMapping("/self/update")
    public QuestionWithOptions updateSelfQuestion(@Valid @RequestBody QuestionWithOptions question) {
        Long userId = userIdExtractorService.getUserId();
        Long pollId = questionWithOptionsService.findQuestionById(question.getId()).getPollId();
        Long pollOwnerId = pollService.findPollById(pollId).getOwnerUserId();
        if (pollOwnerId.equals(userId)) {
            question.setPollId(pollId);
            return questionWithOptionsService.updateQuestion(question);
        }
        throw new ApiRequestException("Poll is not allowed", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/self/add")
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionWithOptions addQuestionToSelfPoll(@Valid @RequestBody QuestionWithOptions question) {
        Long userId = userIdExtractorService.getUserId();
        Long pollOwnerId = pollService.findPollById(question.getPollId()).getOwnerUserId();
        if (pollOwnerId.equals(userId)) {
            return questionWithOptionsService.addQuestion(question);
        }
        throw new ApiRequestException("Poll is not allowed", HttpStatus.FORBIDDEN);

    }

    @DeleteMapping("/self/delete/{questionId}")
    public void deleteQuestionInSelfPoll(@Valid @PathVariable("questionId") Long id) {
        Long userId = userIdExtractorService.getUserId();
        Long pollOwnerId = pollService.findPollById(questionWithOptionsService.findQuestionById(id).getPollId()).getOwnerUserId();
        if (!pollOwnerId.equals(userId)) {
            throw new ApiRequestException("Poll is not allowed", HttpStatus.FORBIDDEN);
        }
        questionWithOptionsService.deleteQuestionById(id);
    }

    @GetMapping("/self/allowed_polls/{pollId}")
    public List<QuestionWithOptions> getAllowedPollQuestions(@Valid @PathVariable("pollId") Long pollId) {
        Long userId = userIdExtractorService.getUserId();
        List<Long> allowedPollsId = userService.findUserById(userId).getAllowedPolls();
        if (!allowedPollsId.contains(pollId)) {
            throw new ApiRequestException("User don't have access to poll", HttpStatus.FORBIDDEN);
        }
        List<QuestionWithOptions> questions =  questionWithOptionsService.findAllQuestionsByPollId(pollId);
        for(QuestionWithOptions question : questions){
            question.setRightAnswer("");
        }
        return questions;
    }
}
