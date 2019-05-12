package com.checkinx.utils

import com.checkinx.utils.sql.plan.parse.models.ExecutionPlan

object CheckInxAssert {
    @JvmStatic
    fun assertIndex(requiredLevel: CoverageLevel, target: String, plan: ExecutionPlan) {
        val node = plan.findTargetInPlanTree(target)

        if (requiredLevel == CoverageLevel.NOT_USING && node == null) {
            return
        }

        if (node == null) {
            throw IndexNotFoundException(target, plan.executionPlan.joinToString(separator = "\n"))
        }

        if (requiredLevel.level!! > node.coverageLevel.level!!) {
            throw CoverageLevelException(requiredLevel.level, node, plan.executionPlan.joinToString(separator = "\n"))
        }
    }
}
