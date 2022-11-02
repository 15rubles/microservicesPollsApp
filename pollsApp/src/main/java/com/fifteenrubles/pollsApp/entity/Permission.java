package com.fifteenrubles.pollsApp.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Permission {
    USER_READ("user:read"),
    USER_WRITE("user:write"),
    USER_SELF_READ("user_self:read"),
    USER_SELF_WRITE("user_self:read"),
    ANSWER_READ("answer:read"),
    ANSWER_WRITE("answer:write"),
    ANSWER_SELF_READ("answer_self:read"),
    ANSWER_SELF_WRITE("answer_self:write"),
    POLL_READ("poll:read"),
    POLL_WRITE("poll:write"),
    POLL_SELF_READ("poll_self:read"),
    POLL_SELF_WRITE("poll_self:write"),
    QUESTION_READ("question:read"),
    QUESTION_WRITE("question:write"),
    QUESTION_SELF_READ("question_self:read"),
    QUESTION_SELF_WRITE("question_self:write"),
    ALLOWED_POLLS_READ("allowed_polls:read");

    private final String permission;

}
