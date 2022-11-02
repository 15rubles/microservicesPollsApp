package com.fifteenrubles.questionWithOptionsService.service;

import com.fifteenrubles.questionWithOptionsService.entity.QuestionWithOptions;
import com.fifteenrubles.questionWithOptionsService.exception.ApiRequestException;
import com.fifteenrubles.questionWithOptionsService.repository.QuestionWithOptionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionWithOptionsService {
    private final QuestionWithOptionsRepository questionWithOptionsRepository;

    public QuestionWithOptions addQuestion(QuestionWithOptions question) {
        return questionWithOptionsRepository.save(question);
    }

    public QuestionWithOptions updateQuestion(QuestionWithOptions question) {
        return questionWithOptionsRepository.save(question);
    }

    public void deleteQuestionById(Long id) {
        questionWithOptionsRepository.deleteById(id);
    }

    public QuestionWithOptions findQuestionById(Long id) {
        return questionWithOptionsRepository.findQuestionById(id)
                .orElseThrow(() -> new ApiRequestException("Question not found", HttpStatus.NOT_FOUND));
    }

    public List<QuestionWithOptions> findAllQuestions() {
        return questionWithOptionsRepository.findAll();
    }

    public List<QuestionWithOptions> findAllQuestionsByPollId(Long id) {
        return questionWithOptionsRepository.findAllByPollId(id);
    }
}

