package com.checkinx.utils.sql.plan.query.impl

import com.checkinx.utils.sql.plan.query.ExecutionPlanException
import com.checkinx.utils.sql.plan.query.ExecutionPlanQuery
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class PostgresExecutionPlanQuery(private val jdbcTemplate: JdbcTemplate) : ExecutionPlanQuery {
    override fun execute(sqlStatement: String): List<String> {
        val executionPlanSqlQuery = "explain $sqlStatement"
        val result = jdbcTemplate.queryForList(executionPlanSqlQuery)

        if (result.isEmpty() || result[0].isEmpty()) {
            throw ExecutionPlanException(
                "Couldn't get execution plan by sql query $executionPlanSqlQuery")
        }

        return result
            .map { it.values.elementAt(0).toString() }
            .toList()
    }
}
