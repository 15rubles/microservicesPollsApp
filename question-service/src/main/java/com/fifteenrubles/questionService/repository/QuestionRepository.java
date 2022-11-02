package com.fifteenrubles.questionService.repository;

import com.fifteenrubles.questionService.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findQuestionById(Long id);

    List<Question> findAllByPollId(Long pollId);

}
