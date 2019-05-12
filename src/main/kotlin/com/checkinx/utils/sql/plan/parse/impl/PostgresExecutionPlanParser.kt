package com.checkinx.utils.sql.plan.parse.impl

import com.checkinx.utils.sql.plan.parse.ExecutionPlanParser
import com.checkinx.utils.sql.plan.parse.models.ExecutionPlan
import com.checkinx.utils.sql.plan.parse.models.PlanNode
import org.springframework.stereotype.Component

@Component
class PostgresExecutionPlanParser : ExecutionPlanParser {
    override fun parse(executionPlan: List<String>): ExecutionPlan {
        val root = createRootNode(executionPlan)
        val table = root.target

        root.coverage?.let {
            getTargetFromUsing(it).let {
                it.first?.let { root.target = it }
                it.second?.let { root.coverage = it }
            }
        }

        createChildNodes(executionPlan, 0, root, 2)

        return ExecutionPlan(executionPlan, table, root)
    }

    private fun createChildNodes(executionPlan: List<String>, planIndex: Int, parent: PlanNode, childMargin: Int): Int {
        var i = planIndex + 1
        while (i < executionPlan.size) {
            val item = executionPlan.get(i)

            val propertyRegex = """^\s*(?<key>.+):\s+(?<value>.+)${'$'}""".toRegex()
            when {
                """^\s{$childMargin}->\s+.*${'$'}""".toRegex().matches(item) -> {
                    val match = """\s+->\s+(?<coverage>.+) on (?<target>.*)\s{2,}\(.*${'$'}""".toRegex().find(item)
                    val node = PlanNode(
                        item,
                        match?.groups?.get("target")?.value,
                        match?.groups?.get("coverage")?.value
                    )

                    parent.children.add(node)
                    i = createChildNodes(executionPlan, i, node, childMargin + 6) - 1
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

    private fun createRootNode(
        executionPlan: List<String>
    ): PlanNode {
        val rawLine = executionPlan.first()
        val match = """^(?<coverage>.+) on (?<target>.*)\s{2,}\(.*${'$'}""".toRegex().find(rawLine)

        return PlanNode(
            rawLine,
            match?.groups?.get("target")?.value,
            match?.groups?.get("coverage")?.value
        )
    }

    private fun getTargetFromUsing(coverage: String): Pair<String?, String?> {
        val usingRegex = """^(?<coverage>.+) using (?<target>.+)${'$'}""".toRegex()

        val matchUsing = usingRegex.find(coverage)

        return Pair(
            matchUsing?.groups?.get("target")?.value,
            matchUsing?.groups?.get("coverage")?.value)
    }
}