package com.example.web.member.dto;

import com.example.web.member.MemberRole;
import com.example.web.member.MemberStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 등록/수정 시 프론트에서 받는 요청 데이터 (Request DTO).
 * 엔티티를 직접 받지 않고 DTO로 받아서 검증(@Valid)한다.
 */
public record MemberRequest(
        @NotBlank(message = "이름은 필수입니다.")
        @Size(max = 50, message = "이름은 50자 이하로 입력하세요.")
        String name,

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @Pattern(regexp = "^$|^01[0-9]-\\d{3,4}-\\d{4}$", message = "전화번호 형식은 010-1234-5678 입니다.")
        String phone,

        @NotNull(message = "권한을 선택하세요.")
        MemberRole role,

        @NotNull(message = "상태를 선택하세요.")
        MemberStatus status
) {
}
