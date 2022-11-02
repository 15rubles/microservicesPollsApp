package com.fifteenrubles.pollsApp.service;

import com.fifteenrubles.pollsApp.entity.Poll;
import com.fifteenrubles.pollsApp.exception.ApiRequestException;
import com.fifteenrubles.pollsApp.repository.AnswerRepository;
import com.fifteenrubles.pollsApp.repository.PollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PollService {

    private final PollRepository pollRepository;
    private final AnswerRepository answerRepository;

    public Poll addPoll(Poll poll) {
        poll.setIsDeleted(false);
        return pollRepository.save(poll);
    }

    public Poll updatePoll(Poll poll) {
        return pollRepository.save(poll);
    }

    public void deletePollById(Long id) {
        boolean isPollAnswersEmpty = answerRepository.findAllByPollId(id).isEmpty();
        if (isPollAnswersEmpty) {
            pollRepository.deleteById(id);
        } else {
            Poll poll = pollRepository.findPollById(id).orElseThrow();
            poll.setIsDeleted(true);
            updatePoll(poll);
        }
    }

    public Poll findPollById(Long id) {
        return pollRepository.findPollById(id)
                .orElseThrow(() -> new ApiRequestException("Poll with id " + id + " not found", HttpStatus.NOT_FOUND));
    }

    public Optional<Poll> findPollByIdOptional(Long id) {
        return pollRepository.findPollById(id);
    }

    public List<Poll> findAllPolls() {
        return pollRepository.findAll();
    }

    public List<Poll> findAllPollsByOwnerUserId(Long id) {
        return pollRepository.findAllByOwnerUserId(id);
    }

}
