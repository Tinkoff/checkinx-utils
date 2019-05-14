package com.checkinx.utils.sql.plan.query

interface ExecutionPlanQuery {
    fun execute(sqlStatement: String): List<String>
}
