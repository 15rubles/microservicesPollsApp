package com.fifteenrubles.pollsApp.controller;


import com.fifteenrubles.pollsApp.dto.PollDto;
import com.fifteenrubles.pollsApp.entity.Poll;
import com.fifteenrubles.pollsApp.exception.ApiRequestException;
import com.fifteenrubles.pollsApp.service.MappingService;
import com.fifteenrubles.pollsApp.service.PollService;
import com.fifteenrubles.pollsApp.service.UserIdExtractorService;
import com.fifteenrubles.pollsApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/poll")
@RequiredArgsConstructor
public class PollController {

    private final PollService pollService;

    private final UserService userService;
    private final MappingService mappingService;

    private final UserIdExtractorService userIdExtractorService;

    @GetMapping("/all")
    public List<Poll> getAllPolls() {
        return pollService.findAllPolls();
    }

    @GetMapping("/{id}")
    public Poll findPollById(@Valid @PathVariable("id") Long id) {
        return pollService.findPollById(id);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Poll addPoll(@Valid @RequestBody Poll poll) {
        return pollService.addPoll(poll);
    }

    @DeleteMapping("/delete/{pollId}")
    public void deletePoll(@Valid @PathVariable("pollId") Long pollId) {
        pollService.deletePollById(pollId);
    }

    @PutMapping("/update")
    public Poll updatePoll(@Valid @RequestBody Poll poll) {
        return pollService.updatePoll(poll);
    }


    @GetMapping("/self/all")
    public List<PollDto> getAllSelfPolls() {
        Long userId = userIdExtractorService.getUserId();
        List<Poll> polls = pollService.findAllPollsByOwnerUserId(userId);
        polls.removeIf(Poll::getIsDeleted);
        return mappingService.mapListPollToPollDto(polls);
    }

    @GetMapping("/self/{id}")
    public PollDto findSelfPollById(@Valid @PathVariable("id") Long id) {
        Long userId = userIdExtractorService.getUserId();
        List<Poll> polls = pollService.findAllPollsByOwnerUserId(userId);
        Poll poll = pollService.findPollById(id);
        if(poll.getIsDeleted() != null && poll.getIsDeleted()){
            throw new ApiRequestException("Poll does not exist", HttpStatus.FORBIDDEN);
        }
        if (polls.contains(poll)) {
            return mappingService.mapToPollDto(poll);
        }
        throw new ApiRequestException("Poll is not belong to you", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/self/add")
    @ResponseStatus(HttpStatus.CREATED)
    public PollDto addSelfPoll(@Valid @RequestBody PollDto pollDto) {
        Long userId = userIdExtractorService.getUserId();
        pollDto.setOwnerUserId(userId);
        Poll newPoll = pollService.addPoll(mappingService.mapToPoll(pollDto));
        return mappingService.mapToPollDto(newPoll);
    }

    @DeleteMapping("/self/delete/{pollId}")
    public void deleteSelfPoll(@Valid @PathVariable("pollId") Long pollId) {
        Long userId = userIdExtractorService.getUserId();
        Long pollOwnerId = pollService.findPollById(pollId).getOwnerUserId();
        if (!pollOwnerId.equals(userId)) {
            throw new ApiRequestException("Poll is not belong to you", HttpStatus.FORBIDDEN);
        }
        pollService.deletePollById(pollId);
    }

    @PutMapping("/self/update")
    public PollDto updateSelfPoll(@Valid @RequestBody PollDto pollDto) {
        Long userId = userIdExtractorService.getUserId();
        Long pollOwnerId = pollService.findPollById(pollDto.getId()).getOwnerUserId();
        if (pollOwnerId.equals(userId)) {
            Poll poll = mappingService.mapToPoll(pollDto);
            poll.setOwnerUserId(userId);
            Poll newPoll = pollService.updatePoll(poll);
            return mappingService.mapToPollDto(newPoll);
        }
        throw new ApiRequestException("Poll is not belong to you", HttpStatus.FORBIDDEN);
    }

    @GetMapping("self/allowed_polls")
    public List<PollDto> getAllowedPolls() {
        Long userId = userIdExtractorService.getUserId();
        List<Long> allowedPollsId = userService.findUserById(userId).getAllowedPolls();
        if (allowedPollsId.isEmpty()) {
            throw new ApiRequestException("User don't have allowed polls", HttpStatus.BAD_REQUEST);
        }
        List<Poll> polls = new ArrayList<>();
        for (Long pollId : allowedPollsId) {
            polls.add(pollService.findPollById(pollId));
        }
        return mappingService.mapListPollToPollDto(polls);
    }

}
