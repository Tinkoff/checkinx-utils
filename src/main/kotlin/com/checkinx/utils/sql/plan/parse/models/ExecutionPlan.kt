package com.checkinx.utils.sql.plan.parse.models

data class ExecutionPlan(
    val executionPlan: List<String>,
    val rootPlanNode: PlanNode
) {
    fun findInPlanTree(predicate: (PlanNode) -> Boolean): PlanNode? {
        return findInPlanTree(predicate, rootPlanNode)
    }

    private fun findInPlanTree(predicate: (PlanNode) -> Boolean, rootNode: PlanNode): PlanNode? {
        if (predicate(rootNode)) {
            return rootNode
        }

        rootNode.children.forEach {
            val result = findInPlanTree(predicate, it)
            if (result != null) {
                return result
            }
        }

        return null
    }
}
