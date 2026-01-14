package com.wombat.screenlock.unwind_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 설정 클래스
 * 
 * <p>Redis 연결 및 RedisTemplate 설정을 담당합니다.
 * Lettuce 클라이언트를 사용하며 (Spring Data Redis 기본),
 * String 기반 Key-Value 직렬화를 사용합니다.</p>
 * 
 * @see com.wombat.screenlock.unwind_be.infrastructure.redis.RefreshTokenRepository
 */
@Configuration
public class RedisConfig {

    /**
     * RedisTemplate Bean 설정
     * 
     * <p>Key와 Value 모두 String 직렬화를 사용하여
     * Redis CLI에서도 읽기 쉬운 형태로 저장됩니다.</p>
     * 
     * @param connectionFactory Redis 연결 팩토리 (자동 주입)
     * @return 설정된 RedisTemplate
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Key와 Value 모두 String 직렬화 사용
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);
        
        template.afterPropertiesSet();
        return template;
    }
}


