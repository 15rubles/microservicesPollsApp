package com.fifteenrubles.pollsApp.repository;

import com.fifteenrubles.pollsApp.entity.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PollRepository extends JpaRepository<Poll, Long> {

    Optional<Poll> findPollById(Long id);

    List<Poll> findAllByOwnerUserId(Long ownerUserId);
}
