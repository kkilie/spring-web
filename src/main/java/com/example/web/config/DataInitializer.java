package com.example.web.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.web.member.Member;
import com.example.web.member.MemberRepository;
import com.example.web.member.MemberRole;
import com.example.web.member.MemberStatus;

/**
 * 서버 시작 시 테이블이 비어 있으면 샘플 회원 데이터를 넣는다 (개발용).
 * 데이터를 초기화하고 싶으면 서버를 끄고 프로젝트의 data/ 폴더를 지우면 된다.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final String[] LAST = {"김", "이", "박", "최", "정", "강", "조", "윤", "장", "임", "한", "오"};
    private static final String[] FIRST = {"민준", "서연", "도윤", "지우", "하준", "서윤", "시우", "지민",
            "예준", "하은", "주원", "수아", "지호", "채원", "준서", "유나"};

    private final MemberRepository memberRepository;

    public DataInitializer(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void run(String... args) {
        if (memberRepository.count() > 0) {
            return;
        }
        Random random = new Random(42); // 고정 시드 → 매번 같은 샘플 데이터
        List<Member> members = new ArrayList<>();
        members.add(new Member("관리자", "admin@example.com", "010-0000-0000", MemberRole.ADMIN, MemberStatus.ACTIVE));

        for (int i = 1; i <= 46; i++) {
            String name = LAST[random.nextInt(LAST.length)] + FIRST[random.nextInt(FIRST.length)];
            String phone = String.format("010-%04d-%04d", random.nextInt(10000), random.nextInt(10000));
            MemberRole role = i % 15 == 0 ? MemberRole.ADMIN : (i % 6 == 0 ? MemberRole.MANAGER : MemberRole.USER);
            int r = random.nextInt(10);
            MemberStatus status = r < 7 ? MemberStatus.ACTIVE : (r < 9 ? MemberStatus.SUSPENDED : MemberStatus.WITHDRAWN);
            members.add(new Member(name, "user" + i + "@example.com", phone, role, status));
        }
        memberRepository.saveAll(members);
    }
}
