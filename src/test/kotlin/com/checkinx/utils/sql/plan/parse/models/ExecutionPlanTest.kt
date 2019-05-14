package com.checkinx.utils.sql.plan.parse.models

import com.checkinx.utils.asserts.CoverageLevel
import com.checkinx.utils.sql.plan.parse.impl.PostgresExecutionPlanParser
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ExecutionPlanTest {

    private lateinit var parser: PostgresExecutionPlanParser

    @Before
    fun setUp() {
        parser = PostgresExecutionPlanParser()
    }

    @Test
    fun testFindTargetInPlanTreeGivenTargetIsExistingIndexWhenCoverageFullThenCheckEqLevel() {
        // ARRANGE
        val plan = parser.parse(listOf(
            "Index Only Scan using ix_pets_age on pets  (cost=0.29..8.36 rows=4 width=4)",
            "  Index Cond: (age = 1)"
        ))

        // ACT
        val result = plan.findTargetInPlanTree("ix_pets_age")

        // ASSERT
        assertEquals(CoverageLevel.FULL, result?.coverageLevel)
    }

    @Test
    fun testFindTargetInPlanTreeGivenTargetIsExistingIndexWhenCoverageHalfThenCheckEqLevel() {
        // ARRANGE
        val plan = parser.parse(listOf(
            "Bitmap Heap Scan on pets  (cost=4.18..12.65 rows=1 width=80)",
            "  Recheck Cond: ((location)::text = 'Moscow'::text)",
            "  Filter: ((name)::text = 'Nick'::text)",
            "  ->  Bitmap Index Scan on ix_location  (cost=0.00..4.18 rows=4 width=0)",
            "        Index Cond: ((location)::text = 'Moscow'::text)"
        ))

        // ACT
        val result = plan.findTargetInPlanTree("ix_location")

        // ASSERT
        assertEquals(CoverageLevel.HALF, result?.coverageLevel)
    }

    @Test
    fun testFindTargetInPlanTreeGivenTargetIsTableWhenCoverageZeroThenCheckEqLevel() {
        // ARRANGE
        val plan = parser.parse(listOf(
            "Seq Scan on pets  (cost=0.00..19.38 rows=4 width=80)",
            "  Filter: ((name)::text = 'Nick'::text)"
        ))

        // ACT
        val result = plan.findTargetInPlanTree("pets")

        // ASSERT
        assertEquals(CoverageLevel.ZERO, result?.coverageLevel)
    }
}
