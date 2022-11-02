package com.fifteenrubles.pollsApp.service;

import com.fifteenrubles.pollsApp.entity.Answer;
import com.fifteenrubles.pollsApp.exception.ApiRequestException;
import com.fifteenrubles.pollsApp.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AnswerService {
    private final AnswerRepository answerRepository;

    public Answer addAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

    public Optional<Answer> findAnswerByPollIdAndUserIdAndQuestionIdAndWithOptions(Long pollId, Long userId, Long questionId, Boolean withOptions){
        return answerRepository.findAnswerByPollIdAndUserIdAndQuestionIdAndWithOptions(pollId,userId,questionId,withOptions);
    }
    public Answer updateAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

    public void deleteAnswerById(Long id) {
        answerRepository.deleteById(id);
    }

    public Answer findAnswerById(Long id) {
        return answerRepository.findAnswerById(id)
                .orElseThrow(() -> new ApiRequestException("Answer with id " + id + " not found", HttpStatus.NOT_FOUND));
    }

    public List<Answer> findAllAnswers() {
        return answerRepository.findAll();
    }

    public List<Answer> findAllAnswerByPollId(Long pollId) {
        return answerRepository.findAllByPollId(pollId);
    }

    public List<Answer> findAllAnswersByPollIdAndUserId(Long pollId, Long userId) {
        return answerRepository.findAllByPollIdAndUserId(pollId, userId);
    }
}
