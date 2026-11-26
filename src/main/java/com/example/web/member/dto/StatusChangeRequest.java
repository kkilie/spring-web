package com.example.web.member.dto;

import com.example.web.member.MemberStatus;

import jakarta.validation.constraints.NotNull;

public record StatusChangeRequest(
        @NotNull(message = "상태를 선택하세요.")
        MemberStatus status
) {
}
