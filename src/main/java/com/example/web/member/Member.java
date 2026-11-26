package com.example.web.member;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * 회원 엔티티 = DB의 members 테이블과 1:1로 매핑되는 클래스.
 * ddl-auto=update 설정 덕분에 이 클래스를 보고 JPA가 테이블을 자동으로 만든다.
 */
@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING) // enum을 'ADMIN' 같은 문자열로 저장
    @Column(nullable = false, length = 20)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // JPA는 기본 생성자가 필요하다 (외부에서 막 쓰지 못하게 protected)
    protected Member() {
    }

    public Member(String name, String email, String phone, MemberRole role, MemberStatus status) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
    }

    /** 수정은 setter 대신 의미 있는 메서드로 한 번에 처리 */
    public void update(String name, String email, String phone, MemberRole role, MemberStatus status) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
    }

    public void changeStatus(MemberStatus status) {
        this.status = status;
    }

    @PrePersist // INSERT 직전에 자동 호출
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate // UPDATE 직전에 자동 호출
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public MemberRole getRole() { return role; }
    public MemberStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
