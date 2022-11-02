package com.fifteenrubles.pollsApp.repository;

import com.fifteenrubles.pollsApp.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    Optional<Answer> findAnswerById(Long id);

    List<Answer> findAllByPollId(Long pollId);

    List<Answer> findAllByPollIdAndUserId(Long pollId, Long userId);

    Optional<Answer> findAnswerByPollIdAndUserIdAndQuestionIdAndWithOptions (Long pollId, Long userId, Long questionId, Boolean withOptions);
}
