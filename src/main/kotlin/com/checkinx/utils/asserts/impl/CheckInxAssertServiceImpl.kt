package com.checkinx.utils.asserts.impl

import com.checkinx.utils.asserts.CheckInxAssertService
import com.checkinx.utils.asserts.CoverageLevel
import com.checkinx.utils.asserts.CoverageLevelException
import com.checkinx.utils.asserts.IndexNotFoundException
import com.checkinx.utils.sql.plan.parse.ExecutionPlanParser
import com.checkinx.utils.sql.plan.parse.models.ExecutionPlan
import com.checkinx.utils.sql.plan.query.ExecutionPlanQuery
import org.springframework.stereotype.Service

@Service
class CheckInxAssertServiceImpl(
    private val executionPlanQuery: ExecutionPlanQuery,
    private val executionPlanParser: ExecutionPlanParser
) : CheckInxAssertService {

    override fun assertIndex(requiredLevel: CoverageLevel, target: String, sqlStatement: String) {
        val executionPlan = executionPlanQuery.execute(sqlStatement)
        val plan = executionPlanParser.parse(executionPlan)

        assertIndex(requiredLevel, target, plan)
    }

    override fun assertIndex(requiredLevel: CoverageLevel, target: String, plan: ExecutionPlan) {
        val node = plan.findTargetInPlanTree(target)

        if (requiredLevel == CoverageLevel.NOT_USING && node == null) {
            return
        }

        if (node == null) {
            throw IndexNotFoundException(
                target,
                plan.executionPlan.joinToString(separator = "\n")
            )
        }

        if (requiredLevel.level!! > node.coverageLevel.level!!) {
            throw CoverageLevelException(
                requiredLevel.level,
                node,
                plan.executionPlan.joinToString(separator = "\n")
            )
        }
    }
}
