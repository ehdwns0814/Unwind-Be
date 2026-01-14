package com.wombat.screenlock.unwind_be.domain.user.entity;

import com.wombat.screenlock.unwind_be.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 엔티티
 * 
 * <p>회원 정보 및 인증 정보를 저장합니다.
 * email을 통해 사용자를 식별하며, BCrypt로 해시된 비밀번호를 저장합니다.</p>
 * 
 * <h3>테이블 정보</h3>
 * <ul>
 *   <li>테이블명: users</li>
 *   <li>PK: id (AUTO_INCREMENT)</li>
 *   <li>UK: email (Unique Index)</li>
 * </ul>
 * 
 * @see BaseTimeEntity
 * @see Role
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "uk_users_email", columnList = "email", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    /**
     * 사용자 고유 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 이메일 (로그인 ID)
     * <p>Unique 제약조건이 적용되어 중복 불가</p>
     */
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /**
     * BCrypt 해시 비밀번호
     * <p>BCrypt 알고리즘으로 해시된 비밀번호 (60자 고정)</p>
     */
    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    /**
     * 사용자 권한
     * <p>기본값: USER</p>
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    /**
     * User 엔티티 생성자
     * 
     * @param email 이메일 (로그인 ID)
     * @param passwordHash BCrypt 해시 비밀번호
     * @param role 사용자 권한 (null인 경우 USER로 설정)
     */
    @Builder
    public User(String email, String passwordHash, Role role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = (role != null) ? role : Role.USER;
    }

    // ========== 비즈니스 메서드 ==========

    /**
     * 비밀번호 변경
     * 
     * @param newPasswordHash 새로운 BCrypt 해시 비밀번호
     */
    public void updatePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    /**
     * 권한 변경
     * 
     * @param newRole 새로운 권한
     */
    public void changeRole(Role newRole) {
        this.role = newRole;
    }
}


