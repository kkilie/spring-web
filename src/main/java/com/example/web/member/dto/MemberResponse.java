package com.example.web.member.dto;

import java.time.LocalDateTime;

import com.example.web.member.Member;
import com.example.web.member.MemberRole;
import com.example.web.member.MemberStatus;

/**
 * 프론트로 내려주는 응답 데이터 (Response DTO).
 * 엔티티를 그대로 내보내지 않고 필요한 필드만 골라서 보낸다.
 */
public record MemberResponse(
        Long id,
        String name,
        String email,
        String phone,
        MemberRole role,
        MemberStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MemberResponse from(Member m) {
        return new MemberResponse(m.getId(), m.getName(), m.getEmail(), m.getPhone(),
                m.getRole(), m.getStatus(), m.getCreatedAt(), m.getUpdatedAt());
    }
}
