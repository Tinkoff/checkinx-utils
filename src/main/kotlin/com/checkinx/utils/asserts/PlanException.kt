package com.checkinx.utils.asserts

import com.checkinx.utils.sql.plan.parse.models.PlanNode

class PlanException(violator: PlanNode, executionPlan: String)
    : Throwable("""Violator: $violator, executionPlan: $executionPlan""")
