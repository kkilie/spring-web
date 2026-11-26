package com.example.web.member;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * JpaRepository를 상속하면 save, findById, findAll, delete 등이 자동으로 생긴다.
 * 메서드 이름 규칙(existsByEmail 등)만 지켜도 쿼리가 자동 생성된다.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    long countByStatus(MemberStatus status);

    long countByRole(MemberRole role);

    List<Member> findTop5ByOrderByCreatedAtDesc();

    /** 검색 조건이 null이면 해당 조건은 무시 (JPQL: 테이블이 아니라 엔티티 기준으로 작성) */
    @Query("""
            select m from Member m
            where (:status is null or m.status = :status)
              and (:role is null or m.role = :role)
              and (:keyword is null
                   or lower(m.name) like lower(concat('%', :keyword, '%'))
                   or lower(m.email) like lower(concat('%', :keyword, '%')))
            """)
    Page<Member> search(@Param("keyword") String keyword,
                        @Param("status") MemberStatus status,
                        @Param("role") MemberRole role,
                        Pageable pageable);
}
