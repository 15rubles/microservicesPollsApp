package com.fifteenrubles.pollsApp.service;


import com.fifteenrubles.pollsApp.dto.PollDto;
import com.fifteenrubles.pollsApp.dto.UserDto;
import com.fifteenrubles.pollsApp.entity.Poll;
import com.fifteenrubles.pollsApp.entity.Role;
import com.fifteenrubles.pollsApp.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MappingService {

    private final UserService userService;
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

    public UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setPassword("");
        userDto.setRole(user.getAuth().name());
        return userDto;

    }

    public User mapToUserFromDto(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        Optional<User> userFromDB = userService.findUserByIdOptional(userDto.getId());
        if (userFromDB.isEmpty()) {
            user.setAuth(Role.USER);
        } else {
            user.setAuth(userFromDB.get().getAuth());
        }
        return user;
    }

    public List<UserDto> mapListUserToUserDto(List<User> users) {
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : users) {
            userDtoList.add(mapToUserDto(user));
        }
        return userDtoList;
    }
}
