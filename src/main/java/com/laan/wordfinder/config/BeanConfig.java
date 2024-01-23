package com.laan.wordfinder.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class BeanConfig {

    @Bean
    public Tika tika() {
        return new Tika();
    }

    @Bean
    public Caffeine<Object, Object> caffeine() {
        return Caffeine
                .newBuilder()
                .expireAfterWrite(120, TimeUnit.SECONDS)
                .maximumSize(256)
                .removalListener((key, value, cause) -> log.info("Removing cache values \nKey: {} \nValue: {} \nCause: {}", key, value, cause));
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

}
