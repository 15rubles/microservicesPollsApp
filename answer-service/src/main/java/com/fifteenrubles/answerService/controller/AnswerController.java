package com.fifteenrubles.answerService.controller;

import com.fifteenrubles.answerService.entity.Answer;
import com.fifteenrubles.answerService.exception.ApiRequestException;
import com.fifteenrubles.answerService.service.AnswerService;
import com.fifteenrubles.answerService.service.UserIdExtractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/answer")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    private static final String PollUrl = "http://poll-service";
    private static final String UserUrl = "http://user-service";
    private final RestTemplate restTemplate;
    private final UserIdExtractorService userIdExtractorService;

    @GetMapping("/poll/{pollId}")
    public List<Answer> getAllAnswersByPollId(@Valid @PathVariable("pollId") Long pollId) {
        return answerService.findAllAnswerByPollId(pollId);
    }

    @GetMapping("/{id}")
    public Answer findAnswerById(@Valid @PathVariable("id") Long id) {
        return answerService.findAnswerById(id);
    }

    @GetMapping
    public List<Answer> findAnswersByPollIdAndUserId(
            @Valid
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "pollId") Long pollId) {
        return answerService.findAllAnswersByPollIdAndUserId(pollId, userId);
    }

    @GetMapping("/all")
    public List<Answer> findAllAnswers() {
        return answerService.findAllAnswers();
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Answer addAnswer(@Valid @RequestBody Answer answer) {
        Optional<Answer> answerOptional =
                answerService.findAnswerByPollIdAndUserIdAndQuestionIdAndWithOptions(
                        answer.getPollId(),
                        answer.getUserId(),
                        answer.getQuestionId(),
                        answer.getWithOptions());
        if(answerOptional.isPresent())
        {
            Answer newAnswer = answerOptional.get();
            newAnswer.setText(answer.getText());
            return answerService.updateAnswer(newAnswer);
        }
        return answerService.addAnswer(answer);
    }

    @PutMapping("/update")
    public Answer updateAnswer(@Valid @RequestBody Answer answer) {
        return answerService.updateAnswer(answer);
    }

    @DeleteMapping("/delete/{answerId}")
    public void deleteAnswer(@Valid @PathVariable("answerId") Long answerId) {
        answerService.deleteAnswerById(answerId);
    }


    @GetMapping("/self/{pollId}")
    public List<Answer> getAllSelfAnswersByPollId(@Valid @PathVariable("pollId") Long pollId) {
        Long userId = userIdExtractorService.getUserId();
        return answerService.findAllAnswersByPollIdAndUserId(pollId, userId);
    }

    @GetMapping("/self") //lead
    public List<Answer> findAnswersBySelfPollId(
            @Valid
            @RequestParam(name = "pollId") Long pollId) {
        Long userIdFromRequest =  userIdExtractorService.getUserId();

        Long userIdFromDB = restTemplate.getForObject(
                PollUrl + "/poll/userId/{pollId}", //todo for all
                Long.class ,pollId);

        if (!userIdFromDB.equals(userIdFromRequest)) {
            throw new ApiRequestException("Poll is not belong to you", HttpStatus.BAD_REQUEST);
        }
        return answerService.findAllAnswerByPollId(pollId);
    }


    @PostMapping("/self/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Answer addSelfAnswer(@Valid @RequestBody Answer answer) {
        Long userId = userIdExtractorService.getUserId();
        List<Long> allowedPollsId = restTemplate.getForObject(
                UserUrl + "/user/userAllowedPolls/{userId}",
                List.class ,userId);
        if (!Objects.requireNonNull(allowedPollsId).contains(answer.getPollId())) { //todo try
            throw new ApiRequestException("User don't allowed to this poll", HttpStatus.BAD_REQUEST);
        }
        answer.setUserId(userId);
        Optional<Answer> answerOptional =
                answerService.findAnswerByPollIdAndUserIdAndQuestionIdAndWithOptions(
                        answer.getPollId(),
                        answer.getUserId(),
                        answer.getQuestionId(),
                        answer.getWithOptions());
        if(answerOptional.isPresent())
        {
            Answer newAnswer = answerOptional.get();
            newAnswer.setText(answer.getText());
            return answerService.updateAnswer(newAnswer);
        }
        return answerService.addAnswer(answer);
    }

    @GetMapping("isPollAnswersEmpty/{pollId}")
    public Boolean isPollAnswersEmpty(@Valid @PathVariable("pollId") Long pollId) {
        return answerService.findAllAnswerByPollId(pollId).isEmpty();
    }
}
