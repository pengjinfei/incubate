package com.pengjinfei.incubate.configuration.mybatis;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created on 4/1/18
 *
 * @author Pengjinfei
 */
@Configuration
@EnableTransactionManagement
@MapperScan(value = "com.pengjinfei.incubate.mapper",sqlSessionTemplateRef = "simpleSqlTemplate")
public class MybatisConfiguration {

    @Bean
    public SqlSessionTemplate batchSqlTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory, ExecutorType.BATCH);
    }

    @Bean
    public SqlSessionTemplate simpleSqlTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
