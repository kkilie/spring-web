package com.example.web.dashboard;

import java.util.List;

import com.example.web.member.dto.MemberResponse;

public record DashboardResponse(
        long totalMembers,
        long activeMembers,
        long suspendedMembers,
        long withdrawnMembers,
        long adminCount,
        List<MemberResponse> recentMembers
) {
}
