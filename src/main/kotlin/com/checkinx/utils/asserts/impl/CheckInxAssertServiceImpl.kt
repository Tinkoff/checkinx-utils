package com.checkinx.utils.asserts.impl

import com.checkinx.utils.asserts.CheckInxAssertService
import com.checkinx.utils.asserts.CoverageLevel
import com.checkinx.utils.asserts.CoverageLevelException
import com.checkinx.utils.asserts.IndexNotFoundException
import com.checkinx.utils.sql.plan.parse.ExecutionPlanParser
import com.checkinx.utils.sql.plan.parse.models.ExecutionPlan
import com.checkinx.utils.sql.plan.parse.models.PlanNode
import com.checkinx.utils.sql.plan.query.ExecutionPlanQuery
import org.springframework.stereotype.Service

@Service
class CheckInxAssertServiceImpl(
    private val executionPlanQuery: ExecutionPlanQuery,
    private val executionPlanParser: ExecutionPlanParser
) : CheckInxAssertService {

    override fun assertCoverage(requiredLevel: CoverageLevel, sqlStatement: String) {
        val executionPlan = executionPlanQuery.execute(sqlStatement)
        val plan = executionPlanParser.parse(executionPlan)

        assertCoverage(requiredLevel, plan)
    }

    override fun assertCoverage(requiredLevel: CoverageLevel, plan: ExecutionPlan) {
        val coverageViolator = plan.findInPlanTree { node: PlanNode ->
            node.coverageLevel < requiredLevel
        }

        if (coverageViolator != null) {
            throw CoverageLevelException(
                requiredLevel.toString(),
                coverageViolator,
                plan.executionPlan.joinToString(separator = "\n")
            )
        }
    }

    override fun assertCoverage(requiredLevel: CoverageLevel, target: String, sqlStatement: String) {
        val executionPlan = executionPlanQuery.execute(sqlStatement)
        val plan = executionPlanParser.parse(executionPlan)

        assertCoverage(requiredLevel, target, plan)
    }

    override fun assertCoverage(requiredLevel: CoverageLevel, target: String, plan: ExecutionPlan) {
        val node = plan.findInPlanTree { planNode -> planNode.target == target }

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
                requiredLevel.toString(),
                node,
                plan.executionPlan.joinToString(separator = "\n")
            )
        }
    }
}
