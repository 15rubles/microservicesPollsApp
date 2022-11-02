package com.fifteenrubles.pollsApp.controller;

import com.fifteenrubles.pollsApp.dto.UserDto;
import com.fifteenrubles.pollsApp.entity.Role;
import com.fifteenrubles.pollsApp.entity.User;
import com.fifteenrubles.pollsApp.exception.ApiRequestException;
import com.fifteenrubles.pollsApp.service.MappingService;
import com.fifteenrubles.pollsApp.service.PollService;
import com.fifteenrubles.pollsApp.service.UserIdExtractorService;
import com.fifteenrubles.pollsApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MappingService mappingService;
    private final PollService pollService;
    private final UserIdExtractorService userIdExtractorService;

    @GetMapping("/all")
    public List<UserDto> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return mappingService.mapListUserToUserDto(users);
    }

    @GetMapping("/find/{id}")
    public UserDto findUserById(@Valid @PathVariable("id") Long id) {
        User user = userService.findUserById(id);
        return mappingService.mapToUserDto(user);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        User user = mappingService.mapToUserFromDto(userDto);
        switch (userDto.getRole()) {
            case "USER" -> user.setAuth(Role.USER);
            case "LEAD" -> user.setAuth(Role.LEAD);
            case "ADMIN" -> user.setAuth(Role.ADMIN);
        }
        User newUser = userService.addUser(user);
        return mappingService.mapToUserDto(newUser);
    }

    @PutMapping("/update")
    public UserDto updateUser(@Valid @RequestBody UserDto userDto) {
        User user = mappingService.mapToUserFromDto(userDto);
        switch (userDto.getRole()) {
            case "USER" -> user.setAuth(Role.USER);
            case "LEAD" -> user.setAuth(Role.LEAD);
            case "ADMIN" -> user.setAuth(Role.ADMIN);
        }
        User newUser = userService.updateUser(user);
        return mappingService.mapToUserDto(newUser);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@Valid @PathVariable("id") Long id) {
        Long userId = userIdExtractorService.getUserId();
        if(id.equals(userId)) {
            throw new ApiRequestException("You dont have permissions to delete this user", HttpStatus.FORBIDDEN);
        }
        userService.deleteUser(id);
    }

    @GetMapping("/self")
    public UserDto findSelfUser() {
        Long userId = userIdExtractorService.getUserId();
        User userFromDB = userService.findUserById(userId);
        return mappingService.mapToUserDto(userFromDB);
    }

    @GetMapping("/self/all_users")
    public List<UserDto> findAllUsersSelf(){
        List<User> users = userService.findAllUsers();
        return mappingService.mapListUserToUserDto(users);
    }

    @PutMapping("/self/update_user_allowed_polls")
    public Boolean updateUserAllowedPolls(
            @Valid
            @RequestBody String username,
            @RequestParam(name = "pollId") Long pollId,
            @RequestParam(name = "isAllowed") Boolean isAllowed){
        Long userId = userIdExtractorService.getUserId();
        Long pollOwnerId = pollService.findPollById(pollId).getOwnerUserId();
        if(!pollOwnerId.equals(userId)){
            throw new ApiRequestException("User dont own the poll", HttpStatus.FORBIDDEN);
        }
        Optional<User> userFromDB = userService.findUserByUsername(username);
        if (userFromDB.isEmpty()) {
            throw new ApiRequestException("User with this username doesn't exist", HttpStatus.BAD_REQUEST);
        }
        User user = userFromDB.get();
        List<Long> allowedPolls= user.getAllowedPolls();
        if(allowedPolls.contains(pollId) && isAllowed){
            throw new ApiRequestException("User already allowed to poll", HttpStatus.BAD_REQUEST);
        }
        if(!allowedPolls.contains(pollId) && !isAllowed){
            throw new ApiRequestException("User already dont allowed to poll", HttpStatus.BAD_REQUEST);
        }
        if(isAllowed){
            allowedPolls.add(pollId);

        }
        else{
            allowedPolls.remove(pollId);
        }
        user.setAllowedPolls(allowedPolls);
        userService.updateUserAllowedPolls(user);
        return isAllowed;
    }

    @PutMapping("/self/update")
    public UserDto updateSelfUser(@Valid @RequestBody UserDto userDto) {
        Long userId = userIdExtractorService.getUserId();
        User userFromDB = userService.findUserById(userId);
        Optional<User> userFromDBCheck = userService.findUserByUsername(userDto.getUsername());
        if (userFromDB.getUsername().equals(userDto.getUsername()) || userFromDBCheck.isEmpty()) {
            User user = mappingService.mapToUserFromDto(userDto);
            Role role = userService.findUserById(userId).getAuth();
            user.setAuth(role);
            user.setId(userId);
            user.setAllowedPolls(userFromDB.getAllowedPolls());

            return mappingService.mapToUserDto(userService.updateUser(user));
        }
        throw new ApiRequestException("Username is taken", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto registerUser(@Valid @RequestBody UserDto userDto) {
        User user = mappingService.mapToUserFromDto(userDto);
        user.setAuth(Role.USER);
        User newUser = userService.addUser(user);
        return mappingService.mapToUserDto(newUser);
    }

}