package com.lovely4k.backend.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(
            Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES) //첫 번재 접근 후 10분 경과 후 제거
                .initialCapacity(200) //초기 크기 설정
                .softValues() // 값 객체에 대한 부드러움 참조: 메모리가 부족할 때만 GC가 일어남. GC가 수집 대상으로 판단하더라도 GC가 일어나지 않음
                .maximumSize(1000) // 최대 크기 설정 (개수임)
                .recordStats() // 캐시 지표 기록
                .removalListener((key ,value, cause) -> log.debug("key: {}, value: {}가 제거 되었습니다. cause: {}", key, value, cause))
        );
        return cacheManager;
    }

}
