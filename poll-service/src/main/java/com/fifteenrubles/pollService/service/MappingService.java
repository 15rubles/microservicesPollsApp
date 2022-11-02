package com.fifteenrubles.pollService.service;


import com.fifteenrubles.pollService.dto.PollDto;
import com.fifteenrubles.pollService.entity.Poll;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MappingService {

    private final PollService pollService;

    public PollDto mapToPollDto(Poll poll) {
        PollDto pollDto = new PollDto();
        pollDto.setId(poll.getId());
        pollDto.setName(poll.getName());
        pollDto.setOwnerUserId(poll.getOwnerUserId());
        return pollDto;
    }

    public List<PollDto> mapListPollToPollDto(List<Poll> polls) {
        List<PollDto> pollDtoList = new ArrayList<>();
        for (Poll poll : polls) {
            pollDtoList.add(mapToPollDto(poll));
        }
        return pollDtoList;
    }

    public Poll mapToPoll(PollDto pollDto) {
        Poll poll = new Poll();
        poll.setId(pollDto.getId());
        poll.setName(pollDto.getName());
        poll.setOwnerUserId(pollDto.getOwnerUserId());
        Optional<Poll> pollFromDB = pollService.findPollByIdOptional(pollDto.getId());
        Boolean isDeleted = false;
        if (pollFromDB.isPresent()) {
            isDeleted = pollFromDB.get().getIsDeleted();
        }
        poll.setIsDeleted(isDeleted);
        return poll;
    }



}
