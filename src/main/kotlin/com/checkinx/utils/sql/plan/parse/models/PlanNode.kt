package com.checkinx.utils.sql.plan.parse.models

import com.checkinx.utils.asserts.CoverageLevel

data class PlanNode(
    val raw: String,
    val table: String?,
    var target: String?,
    var coverage: String?,
    val children: MutableList<PlanNode> = mutableListOf(),
    val properties: MutableList<Pair<String, String>> = mutableListOf(),
    val others: MutableList<String> = mutableListOf()
) {
    val coverageLevel: CoverageLevel
        get() {
            return when {
                coverage?.contains("Index Only Scan") ?: false -> CoverageLevel.FULL
                coverage?.contains("Index Scan") ?: false -> CoverageLevel.HALF
                coverage?.contains("Seq Scan") ?: false -> CoverageLevel.ZERO
                else -> CoverageLevel.UNKNOWN
            }
        }
}
