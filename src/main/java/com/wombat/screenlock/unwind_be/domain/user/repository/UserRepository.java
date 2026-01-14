package com.wombat.screenlock.unwind_be.domain.user.repository;

import com.wombat.screenlock.unwind_be.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User 엔티티 Repository
 * 
 * <p>JPA 기반 데이터 접근 계층으로, Spring Data JPA Query Method를 활용합니다.</p>
 * 
 * <h3>제공 기능</h3>
 * <ul>
 *   <li>기본 CRUD (JpaRepository 상속)</li>
 *   <li>이메일로 사용자 조회</li>
 *   <li>이메일 존재 여부 확인</li>
 * </ul>
 * 
 * @see User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 조회
     * 
     * @param email 조회할 이메일
     * @return 사용자 Optional (존재하지 않으면 empty)
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일 존재 여부 확인
     * 
     * <p>회원가입 시 이메일 중복 체크에 사용</p>
     * 
     * @param email 확인할 이메일
     * @return 존재 여부 (true: 존재, false: 미존재)
     */
    boolean existsByEmail(String email);
}


