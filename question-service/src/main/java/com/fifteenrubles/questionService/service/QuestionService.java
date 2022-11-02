package com.fifteenrubles.questionService.service;

import com.fifteenrubles.questionService.entity.Question;
import com.fifteenrubles.questionService.exception.ApiRequestException;
import com.fifteenrubles.questionService.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {
    private final QuestionRepository questionRepository;

    public Question addQuestion(Question question) {
        return questionRepository.save(question);
    }

    public Question updateQuestion(Question question) {
        return questionRepository.save(question);
    }

    public void deleteQuestionById(Long id) {
        questionRepository.deleteById(id);
    }

    public Question findQuestionById(Long id) {
        return questionRepository.findQuestionById(id)
                .orElseThrow(() -> new ApiRequestException("Question not found", HttpStatus.NOT_FOUND));
    }

    public List<Question> findAllQuestions() {
        return questionRepository.findAll();
    }

    public List<Question> findAllQuestionsByPollId(Long id) {
        return questionRepository.findAllByPollId(id);
    }
}
