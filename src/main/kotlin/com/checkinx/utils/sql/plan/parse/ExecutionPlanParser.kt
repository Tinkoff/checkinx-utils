package com.checkinx.utils.sql.plan.parse

import com.checkinx.utils.sql.plan.parse.models.ExecutionPlan

interface ExecutionPlanParser {
    fun parse(executionPlan: List<String>): ExecutionPlan
}
