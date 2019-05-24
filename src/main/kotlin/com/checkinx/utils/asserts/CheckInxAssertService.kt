package com.checkinx.utils.asserts

import com.checkinx.utils.sql.plan.parse.models.ExecutionPlan
import com.checkinx.utils.sql.plan.parse.models.PlanNode

interface CheckInxAssertService {

    fun assertCoverage(requiredLevel: CoverageLevel, target: String, sqlStatement: String)

    fun assertCoverage(requiredLevel: CoverageLevel, target: String, plan: ExecutionPlan)
    fun assertCoverage(requiredLevel: CoverageLevel, sqlStatement: String)
    fun assertCoverage(requiredLevel: CoverageLevel, plan: ExecutionPlan)
    fun assertPlan(
        sqlStatement: String,
        predicate: (PlanNode) -> Boolean
    )
    fun assertPlan(
        plan: ExecutionPlan,
        predicate: (PlanNode) -> Boolean
    )
}
