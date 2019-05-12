package com.checkinx.utils

import com.checkinx.utils.sql.plan.parse.models.PlanNode

class CoverageLevelException(requiredLevel: Int, node: PlanNode, executionPlan: String)
    : Throwable("""Required level: $requiredLevel, node: $node, executionPlan: $executionPlan""")
