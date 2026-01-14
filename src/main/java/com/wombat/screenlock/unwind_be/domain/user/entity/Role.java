package com.wombat.screenlock.unwind_be.domain.user.entity;

/**
 * 사용자 권한 Enum
 * 
 * <p>사용자의 역할/권한을 정의합니다.</p>
 * 
 * <ul>
 *   <li>{@link #USER} - 일반 사용자 (기본값)</li>
 *   <li>{@link #ADMIN} - 관리자</li>
 * </ul>
 */
public enum Role {
    
    /**
     * 일반 사용자
     * <p>기본 기능 사용 가능</p>
     */
    USER,
    
    /**
     * 관리자
     * <p>모든 기능 및 관리 기능 사용 가능</p>
     */
    ADMIN
}


