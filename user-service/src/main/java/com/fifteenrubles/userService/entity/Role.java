package com.fifteenrubles.userService.entity;

import com.google.common.collect.Sets;
import lombok.Getter;

import java.util.Set;

import static com.fifteenrubles.userService.entity.Permission.*;

@Getter
public enum Role {
    USER(Sets.newHashSet(ANSWER_SELF_READ, ANSWER_SELF_WRITE, USER_SELF_READ, USER_SELF_WRITE, ALLOWED_POLLS_READ)),
    ADMIN(Sets.newHashSet(USER_READ, USER_WRITE, POLL_READ, POLL_WRITE, QUESTION_READ, QUESTION_WRITE, ANSWER_READ, ANSWER_WRITE, USER_SELF_READ, USER_SELF_WRITE)),
    LEAD(Sets.newHashSet(POLL_SELF_READ, POLL_SELF_WRITE, QUESTION_SELF_READ, QUESTION_SELF_WRITE, ANSWER_SELF_READ, ANSWER_SELF_WRITE, USER_SELF_READ, USER_SELF_WRITE, ALLOWED_POLLS_READ));

    private final Set<Permission> permissions;
    private final static String rolePrefix ="ROLE_";

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }


}
