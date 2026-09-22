package com.interviewstack.auth.dto;

import com.interviewstack.entity.user.User;

import java.util.UUID;

public record UserSummary(
        UUID id,
        String email,
        String name
) {
    public static UserSummary from(User user) {
        return new UserSummary(user.getId(), user.getEmail(), user.getName());
    }
}
