package com.wombat.screenlock.unwind_be.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 활성화 설정
 * 
 * <p>이 설정을 통해 엔티티의 생성일시(@CreatedDate)와 
 * 수정일시(@LastModifiedDate)가 자동으로 관리됩니다.</p>
 * 
 * @see com.wombat.screenlock.unwind_be.domain.common.BaseTimeEntity
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    // JPA Auditing 활성화만 담당하므로 별도 Bean 정의 없음
}

