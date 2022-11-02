package com.fifteenrubles.userService.service;


import com.fifteenrubles.userService.dto.UserDto;
import com.fifteenrubles.userService.entity.Role;
import com.fifteenrubles.userService.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MappingService {

    private final UserService userService;

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
