package com.checkinx.utils.sql.plan.parse.impl

import com.checkinx.utils.sql.plan.parse.ExecutionPlanParser
import com.checkinx.utils.sql.plan.parse.models.ExecutionPlan
import com.checkinx.utils.sql.plan.parse.models.PlanNode

open class PostgresExecutionPlanParser : ExecutionPlanParser {
    override fun parse(executionPlan: List<String>): ExecutionPlan {
        val root = getTargetFromUsingOrOn(executionPlan.first())

        createChildNodes(executionPlan, 0, root, 2)

        return ExecutionPlan(executionPlan, root)
    }

    private fun createChildNodes(executionPlan: List<String>, planIndex: Int, parent: PlanNode, childMargin: Int): Int {
        var i = planIndex + 1
        while (i < executionPlan.size) {
            val item = executionPlan.get(i)

            val propertyRegex = """^\s*(?<key>.+):\s+(?<value>.+)${'$'}""".toRegex()
            when {
                """^\s{$childMargin}->\s+.*${'$'}""".toRegex().matches(item) -> {
                    val node = getTargetFromUsingOrOn(item)

                    parent.children.add(node)
                    i = createChildNodes(executionPlan, i, node, childMargin + MARGIN_STEP) - 1
                }
                propertyRegex.matches(item) -> {
                    val matchProperty = propertyRegex.find(item)

                    parent.properties.add(Pair(
                        matchProperty?.groups?.get("key")?.value!!,
                        matchProperty.groups.get("value")?.value!!))
                }
                !item.contains("->") -> {
                    parent.others.add(item)
                }
                else -> return i
            }

            i++
        }

        return i
    }

    private fun getTargetFromUsingOrOn(planLine: String): PlanNode {
        return getTargetFromUsing(planLine).let {
            when (it.target) {
                null -> return@let getTargetFromOn(it.raw)
                else -> return@let it
            }
        }
    }

    private fun getTargetFromOn(
        planLine: String
    ): PlanNode {
        val match = """^(\s+->\s+|)(?<coverage>.+) on (?<target>.*)\s{2,}\(.*${'$'}""".toRegex().find(planLine)

        return PlanNode(
            planLine,
            null,
            match?.groups?.get("target")?.value,
            match?.groups?.get("coverage")?.value
        )
    }

    private fun getTargetFromUsing(planLine: String): PlanNode {
        val match = """^(\s+->\s+|)(?<coverage>.+) using (?<target>.+) on (?<table>.*)\s{2,}\(.*${'$'}"""
            .toRegex()
            .find(planLine)

        return PlanNode(
            planLine,
            match?.groups?.get("table")?.value,
            match?.groups?.get("target")?.value,
            match?.groups?.get("coverage")?.value)
    }

    companion object {
        const val MARGIN_STEP = 6
    }
}
