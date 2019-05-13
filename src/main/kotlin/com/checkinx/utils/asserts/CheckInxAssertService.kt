package com.checkinx.utils.asserts

import com.checkinx.utils.sql.plan.parse.models.ExecutionPlan

interface CheckInxAssertService {

    fun assertIndex(requiredLevel: CoverageLevel, target: String, sqlStatement: String)

    fun assertIndex(requiredLevel: CoverageLevel, target: String, plan: ExecutionPlan)
}