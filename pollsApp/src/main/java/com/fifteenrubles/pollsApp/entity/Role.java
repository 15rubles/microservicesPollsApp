package com.fifteenrubles.pollsApp.entity;

import com.google.common.collect.Sets;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.fifteenrubles.pollsApp.entity.Permission.*;

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

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority(rolePrefix + this.name()));
        return permissions;
    }

}
