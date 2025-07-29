package com.edumanager.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN("관리자"),
    TEACHER("강사"),
    STUDENT("학생"),
    PARENT("학부모");

    private final String description;
}
