package com.checkinx.utils.configs

import com.checkinx.utils.asserts.CheckInxAssertService
import com.checkinx.utils.asserts.impl.CheckInxAssertServiceImpl
import com.checkinx.utils.sql.interceptors.SqlInterceptor
import com.checkinx.utils.sql.interceptors.postgres.PostgresInterceptor
import com.checkinx.utils.sql.plan.parse.ExecutionPlanParser
import com.checkinx.utils.sql.plan.parse.impl.PostgresExecutionPlanParser
import com.checkinx.utils.sql.plan.query.ExecutionPlanQuery
import com.checkinx.utils.sql.plan.query.impl.PostgresExecutionPlanQuery
import net.ttddyy.dsproxy.support.ProxyDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@Configuration
open class PostgresConfig {
    @Bean
    open fun dataSourceWrapperBeanPostProcessor(): DataSourceWrapper {
        return DataSourceWrapper()
    }

    @Bean
    open fun sqlInterceptor(dataSource: DataSource): SqlInterceptor {
        return PostgresInterceptor(dataSource as ProxyDataSource)
    }

    @Bean
    open fun executionPlanParser(): ExecutionPlanParser {
        return PostgresExecutionPlanParser()
    }

    @Bean
    open fun executionPlanQuery(jdbcTemplate: JdbcTemplate): ExecutionPlanQuery {
        return PostgresExecutionPlanQuery(jdbcTemplate)
    }

    @Bean
    open fun checkInxAssertService(query: ExecutionPlanQuery, parser: ExecutionPlanParser): CheckInxAssertService {
        return CheckInxAssertServiceImpl(query, parser)
    }
}