package com.checkinx.utils.asserts

import com.checkinx.utils.sql.plan.parse.models.PlanNode

class CoverageLevelException(requiredLevel: String, violator: PlanNode, executionPlan: String)
    : Throwable("""Required level: $requiredLevel, violator: $violator, executionPlan: $executionPlan""")
