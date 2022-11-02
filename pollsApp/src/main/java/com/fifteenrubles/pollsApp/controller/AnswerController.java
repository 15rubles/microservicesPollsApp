package com.fifteenrubles.pollsApp.controller;

import com.fifteenrubles.pollsApp.entity.Answer;
import com.fifteenrubles.pollsApp.exception.ApiRequestException;
import com.fifteenrubles.pollsApp.service.AnswerService;
import com.fifteenrubles.pollsApp.service.PollService;
import com.fifteenrubles.pollsApp.service.UserIdExtractorService;
import com.fifteenrubles.pollsApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/answer")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;
    private final PollService pollService;
    private final UserIdExtractorService userIdExtractorService;

    private final UserService userService;

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
        Long userIdFromRequest = userIdExtractorService.getUserId();
        Long userIdFromDB = pollService.findPollById(pollId).getOwnerUserId();
        if (!userIdFromDB.equals(userIdFromRequest)) {
            throw new ApiRequestException("Poll is not belong to you", HttpStatus.BAD_REQUEST);
        }
        return answerService.findAllAnswerByPollId(pollId);
    }


    @PostMapping("/self/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Answer addSelfAnswer(@Valid @RequestBody Answer answer) {
        Long userId = userIdExtractorService.getUserId();
        List<Long> allowedPollsId = userService.findUserById(userId).getAllowedPolls();
        if (!allowedPollsId.contains(answer.getPollId())) {
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
}
