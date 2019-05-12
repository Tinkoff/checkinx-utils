package com.checkinx.utils.sql.plan.parse.models

data class ExecutionPlan(
    val executionPlan: List<String>,
    val table: String?,
    val rootPlanNode: PlanNode
) {
    fun findTargetInPlanTree(target: String): PlanNode? {
        return findTargetInPlanTree(target, rootPlanNode)
    }

    private fun findTargetInPlanTree(target: String, rootNode: PlanNode): PlanNode? {
        if (rootNode.target == target) {
            return rootNode
        }

        rootNode.children.forEach {
            val result = findTargetInPlanTree(target, it)
            if (result != null) {
                return result
            }
        }

        return null
    }
}
