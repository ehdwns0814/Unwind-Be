package com.wombat.screenlock.unwind_be.domain.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 공통 시간 필드 추상 클래스
 * 
 * <p>모든 엔티티가 이 클래스를 상속하여 생성일시(createdAt)와 
 * 수정일시(updatedAt)를 자동으로 관리합니다.</p>
 * 
 * <p>JPA Auditing 기능을 사용하여 INSERT 시 createdAt, 
 * UPDATE 시 updatedAt이 자동으로 설정됩니다.</p>
 * 
 * @see com.wombat.screenlock.unwind_be.config.JpaAuditingConfig
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseTimeEntity {

    /**
     * 생성 일시
     * <p>엔티티가 처음 저장될 때 자동으로 설정되며, 이후 수정 불가</p>
     */
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     * <p>엔티티가 수정될 때마다 자동으로 갱신</p>
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

