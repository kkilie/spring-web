package com.example.web.dashboard;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web.member.MemberRepository;
import com.example.web.member.MemberRole;
import com.example.web.member.MemberStatus;
import com.example.web.member.dto.MemberResponse;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final MemberRepository memberRepository;

    public DashboardService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public DashboardResponse getSummary() {
        return new DashboardResponse(
                memberRepository.count(),
                memberRepository.countByStatus(MemberStatus.ACTIVE),
                memberRepository.countByStatus(MemberStatus.SUSPENDED),
                memberRepository.countByStatus(MemberStatus.WITHDRAWN),
                memberRepository.countByRole(MemberRole.ADMIN),
                memberRepository.findTop5ByOrderByCreatedAtDesc().stream().map(MemberResponse::from).toList());
    }
}
