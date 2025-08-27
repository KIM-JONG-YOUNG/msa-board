package com.jong.msa.board.support.domain.config;

import com.jong.msa.board.common.constants.PackageNames;
import com.jong.msa.board.support.domain.repository.AbstractEntityRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@ConditionalOnClass(name = "org.springframework.data.jpa.repository.JpaRepository")
@EntityScan(basePackages = PackageNames.ROOT_PACKAGE)
@EnableJpaRepositories(
    basePackages = PackageNames.ROOT_PACKAGE,
    includeFilters = {
        @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AbstractEntityRepository.class)
    })
public class PersistConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    @Bean
    DataSource dataSource(HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }

}
