package com.checkinx.utils.asserts.impl

import com.checkinx.utils.asserts.*
import com.checkinx.utils.sql.plan.parse.ExecutionPlanParser
import com.checkinx.utils.sql.plan.parse.models.ExecutionPlan
import com.checkinx.utils.sql.plan.parse.models.PlanNode
import com.checkinx.utils.sql.plan.query.ExecutionPlanQuery
import org.springframework.stereotype.Service

@Service
open class CheckInxAssertServiceImpl(
    private val executionPlanQuery: ExecutionPlanQuery,
    private val executionPlanParser: ExecutionPlanParser
) : CheckInxAssertService {

    override fun assertCoverage(requiredLevel: CoverageLevel, sqlStatement: String) {
        val executionPlan = executionPlanQuery.execute(sqlStatement)
        val plan = executionPlanParser.parse(executionPlan)

        assertCoverage(requiredLevel, plan)
    }

    override fun assertCoverage(requiredLevel: CoverageLevel, plan: ExecutionPlan) {
        assertPlan(
            plan
        ) { node: PlanNode ->
            node.coverageLevel.level < requiredLevel.level
        }
    }

    override fun assertPlan(
        sqlStatement: String,
        predicate: (PlanNode) -> Boolean
    ) {
        val executionPlan = executionPlanQuery.execute(sqlStatement)
        val plan = executionPlanParser.parse(executionPlan)

        assertPlan(plan, predicate)
    }

    override fun assertPlan(
        plan: ExecutionPlan,
        predicate: (PlanNode) -> Boolean
    ) {
        val violator = plan.findInPlanTree(predicate)

        if (violator != null) {
            throw PlanException(
                violator,
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

        if (requiredLevel.level > node.coverageLevel.level) {
            throw CoverageLevelException(
                requiredLevel.toString(),
                node,
                plan.executionPlan.joinToString(separator = "\n")
            )
        }
    }
}
