package com.example.web.member;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.web.common.PageResponse;
import com.example.web.member.dto.MemberRequest;
import com.example.web.member.dto.MemberResponse;
import com.example.web.member.dto.StatusChangeRequest;

import jakarta.validation.Valid;

/**
 * 회원 관리 REST API
 *
 * GET    /api/admin/members           목록 (검색 + 페이징)
 * GET    /api/admin/members/{id}      상세
 * POST   /api/admin/members           등록
 * PUT    /api/admin/members/{id}      전체 수정
 * PATCH  /api/admin/members/{id}/status  상태만 변경
 * DELETE /api/admin/members/{id}      삭제
 */
@RestController // @Controller + @ResponseBody: 리턴값을 JSON으로 변환해서 응답
@RequestMapping("/api/admin/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 예) /api/admin/members?keyword=kim&status=ACTIVE&page=0&size=10
    @GetMapping
    public PageResponse<MemberResponse> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) MemberStatus status,
            @RequestParam(required = false) MemberRole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return memberService.search(keyword, status, role, Math.max(page, 0), Math.clamp(size, 1, 100));
    }

    @GetMapping("/{id}")
    public MemberResponse get(@PathVariable Long id) {
        return memberService.get(id);
    }

    // @RequestBody: JSON 본문 → MemberRequest 객체로 변환, @Valid: 검증 규칙 실행
    @PostMapping
    public ResponseEntity<MemberResponse> create(@Valid @RequestBody MemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.create(request)); // 201
    }

    @PutMapping("/{id}")
    public MemberResponse update(@PathVariable Long id, @Valid @RequestBody MemberRequest request) {
        return memberService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public MemberResponse changeStatus(@PathVariable Long id, @Valid @RequestBody StatusChangeRequest request) {
        return memberService.changeStatus(id, request.status());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        memberService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
