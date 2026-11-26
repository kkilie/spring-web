package com.example.web.member;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web.common.BusinessException;
import com.example.web.common.PageResponse;
import com.example.web.member.dto.MemberRequest;
import com.example.web.member.dto.MemberResponse;

/**
 * 비즈니스 로직 담당.
 * - 컨트롤러는 요청/응답만, 서비스는 "규칙"(중복 체크, 존재 여부 등)을 처리한다.
 * - @Transactional: 메서드 하나가 하나의 트랜잭션. 예외가 나면 자동 롤백.
 */
@Service
@Transactional(readOnly = true) // 기본은 조회 전용 (성능 이점)
public class MemberService {

    private final MemberRepository memberRepository;

    // 생성자 주입: 스프링이 MemberRepository 구현체를 자동으로 넣어준다
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public PageResponse<MemberResponse> search(String keyword, MemberStatus status, MemberRole role,
                                               int page, int size) {
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return PageResponse.of(memberRepository.search(kw, status, role, pageable), MemberResponse::from);
    }

    public MemberResponse get(Long id) {
        return MemberResponse.from(findOrThrow(id));
    }

    @Transactional // 쓰기 작업은 readOnly 해제
    public MemberResponse create(MemberRequest req) {
        if (memberRepository.existsByEmail(req.email())) {
            throw BusinessException.conflict("이미 사용 중인 이메일입니다: " + req.email());
        }
        Member saved = memberRepository.save(
                new Member(req.name(), req.email(), blankToNull(req.phone()), req.role(), req.status()));
        return MemberResponse.from(saved);
    }

    @Transactional
    public MemberResponse update(Long id, MemberRequest req) {
        Member member = findOrThrow(id);
        if (memberRepository.existsByEmailAndIdNot(req.email(), id)) {
            throw BusinessException.conflict("이미 사용 중인 이메일입니다: " + req.email());
        }
        // 엔티티 값만 바꾸면 트랜잭션 종료 시 JPA가 UPDATE 쿼리를 자동 실행 (변경 감지, Dirty Checking)
        member.update(req.name(), req.email(), blankToNull(req.phone()), req.role(), req.status());
        // flush: UPDATE를 지금 바로 실행 → @PreUpdate가 돌아서 응답에 최신 updatedAt이 담긴다
        memberRepository.flush();
        return MemberResponse.from(member);
    }

    @Transactional
    public MemberResponse changeStatus(Long id, MemberStatus status) {
        Member member = findOrThrow(id);
        member.changeStatus(status);
        memberRepository.flush();
        return MemberResponse.from(member);
    }

    @Transactional
    public void delete(Long id) {
        memberRepository.delete(findOrThrow(id));
    }

    private Member findOrThrow(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("회원을 찾을 수 없습니다. id=" + id));
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
