package com.fifteenrubles.pollsApp.repository;

import com.fifteenrubles.pollsApp.entity.QuestionWithOptions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionWithOptionsRepository extends JpaRepository<QuestionWithOptions, Long> {

    Optional<QuestionWithOptions> findQuestionById(Long id);

    List<QuestionWithOptions> findAllByPollId(Long pollId);
}
