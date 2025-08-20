package kkukmoa.kkukmoa.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslConfig {
    @PersistenceContext private EntityManager em;

    @Bean
    public com.querydsl.jpa.impl.JPAQueryFactory jpaQueryFactory() {
        return new com.querydsl.jpa.impl.JPAQueryFactory(em);
    }
}
