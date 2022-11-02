package com.fifteenrubles.pollsApp.controller;

import com.fifteenrubles.pollsApp.entity.Question;
import com.fifteenrubles.pollsApp.entity.User;
import com.fifteenrubles.pollsApp.exception.ApiRequestException;
import com.fifteenrubles.pollsApp.service.PollService;
import com.fifteenrubles.pollsApp.service.QuestionService;
import com.fifteenrubles.pollsApp.service.UserIdExtractorService;
import com.fifteenrubles.pollsApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/question")
@RequiredArgsConstructor

public class QuestionController {

    private final QuestionService questionService;

    private final UserService userService;
    private final PollService pollService;
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
        Long pollOwnerId = pollService.findPollById(pollId).getOwnerUserId();
        if (pollOwnerId.equals(userId)) {
            return questionService.findAllQuestionsByPollId(pollId);
        }
        throw new ApiRequestException("Poll is not allowed", HttpStatus.FORBIDDEN);
    }

    @PutMapping("/self/update")
    public Question updateSelfQuestion(@Valid @RequestBody Question question) {
        Long userId = userIdExtractorService.getUserId();
        Long pollId = questionService.findQuestionById(question.getId()).getPollId();
        Long pollOwnerId = pollService.findPollById(pollId).getOwnerUserId();
        if (pollOwnerId.equals(userId)) {
            question.setPollId(pollId);
            return questionService.updateQuestion(question);
        }
        throw new ApiRequestException("Poll is not allowed", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/self/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Question addQuestionToSelfPoll(@Valid @RequestBody Question question) {
        Long userId = userIdExtractorService.getUserId();
        Long pollOwnerId = pollService.findPollById(question.getPollId()).getOwnerUserId();
        if (pollOwnerId.equals(userId)) {
            return questionService.addQuestion(question);
        }
        throw new ApiRequestException("Poll is not allowed", HttpStatus.FORBIDDEN);

    }

    @DeleteMapping("/self/delete/{questionId}")
    public void deleteQuestionInSelfPoll(@Valid @PathVariable("questionId") Long id) {
        Long userId = userIdExtractorService.getUserId();
        Long pollOwnerId = pollService.findPollById(questionService.findQuestionById(id).getPollId()).getOwnerUserId();
        if (!pollOwnerId.equals(userId)) {
            throw new ApiRequestException("Poll is not allowed", HttpStatus.FORBIDDEN);
        }
        questionService.deleteQuestionById(id);
    }

    @GetMapping("/self/allowed_polls/{pollId}")
    public List<Question> getAllowedPollQuestions(@Valid @PathVariable("pollId") Long pollId) {
        Long userId = userIdExtractorService.getUserId();
        List<Long> allowedPollsId = userService.findUserById(userId).getAllowedPolls();
        if (!allowedPollsId.contains(pollId)) {
            throw new ApiRequestException("User don't have access to poll", HttpStatus.FORBIDDEN);
        }
        List<Question> questions =  questionService.findAllQuestionsByPollId(pollId);
        for(Question question : questions){
            question.setRightAnswer("");
        }
        return questions;
    }
}
