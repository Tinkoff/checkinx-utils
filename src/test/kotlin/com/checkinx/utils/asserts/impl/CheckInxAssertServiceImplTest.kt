package com.checkinx.utils.asserts.impl

import com.checkinx.utils.asserts.CoverageLevel
import com.checkinx.utils.asserts.CoverageLevelException
import com.checkinx.utils.asserts.IndexNotFoundException
import com.checkinx.utils.asserts.PlanException
import com.checkinx.utils.sql.plan.parse.impl.PostgresExecutionPlanParser
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class CheckInxAssertServiceImplTest {

    private lateinit var checkInxAssertService: CheckInxAssertServiceImpl

    @Before
    fun setUp() {
        checkInxAssertService = CheckInxAssertServiceImpl(
            mockk(), mockk()
        )
    }

    @Test
    fun testAssertIndexGivenIndexOnlyScanWhenRequireFullThenSuccess() {
        // ARRANGE
        val plan = PostgresExecutionPlanParser().parse(listOf(
            "Index Only Scan using ix_pets_age on pets  (cost=0.29..8.36 rows=4 width=4)",
            "  Index Cond: (age = 1)"
        ))

        // ACT & ASSERT
        checkInxAssertService.assertCoverage(CoverageLevel.FULL, "ix_pets_age", plan)
    }

    @Test(expected = CoverageLevelException::class)
    fun testAssertIndexGivenIndexScanWhenRequireFullThenLevelException() {
        // ARRANGE
        val plan = PostgresExecutionPlanParser().parse(listOf(
            "Index Scan using ix_pets_age on pets  (cost=0.29..8.72 rows=25 width=36)",
            "  Index Cond: (age < 10)"
        ))

        // ACT
        checkInxAssertService.assertCoverage(CoverageLevel.FULL, "ix_pets_age", plan)
    }

    @Test
    fun testAssertIndexGivenIndexScanWhenRequireHalfThenSuccess() {
        // ARRANGE
        val plan = PostgresExecutionPlanParser().parse(listOf(
            "Index Scan using ix_pets_age on pets  (cost=0.29..8.72 rows=25 width=36)",
            "  Index Cond: (age < 10)"
        ))

        // ACT & ASSERT
        checkInxAssertService.assertCoverage(CoverageLevel.HALF, "ix_pets_age", plan)
    }

    @Test(expected = IndexNotFoundException::class)
    fun testAssertIndexGivenIndexScanWhenNotExistingIndexThenNotFoundException() {
        // ARRANGE
        val plan = PostgresExecutionPlanParser().parse(listOf(
            "Index Scan using ix_pets_age on pets  (cost=0.29..8.72 rows=25 width=36)",
            "  Index Cond: (age < 10)"
        ))

        // ACT
        checkInxAssertService.assertCoverage(CoverageLevel.HALF, "ix_not_existing", plan)
    }

    @Test
    fun testAssertIndexGivenIndexScanWhenUsingIndexNotRootThenTargetAndTableFound() {
        val plan = PostgresExecutionPlanParser().parse(listOf(
            "Limit  (cost=0.29..8.30 rows=1 width=36)",
            "  ->  Index Scan using ix_pets_age on pets  (cost=0.29..8.30 rows=1 width=36)",
            "        Index Cond: (age = 5000)"
        ))

        checkInxAssertService.assertCoverage(CoverageLevel.HALF, "ix_pets_age", plan)
    }

    @Test
    fun testAssertIndexGivenTableWhenUsingNotRootThenTargetFound() {
        val plan = PostgresExecutionPlanParser().parse(listOf(
            "Limit  (cost=0.29..8.30 rows=1 width=36)",
            "  ->  Index Scan on some_table tbl  (cost=0.14..8.17 rows=1 width=562)",
            "        Index Cond: (age = 5000)"
        ))

        checkInxAssertService.assertCoverage(CoverageLevel.HALF, "some_table tbl", plan)
    }

    @Test(expected = PlanException::class)
    fun testAssertCoverageGivenSeqScanWhenLevelHalfThenCoverageLevelException() {
        // ARRANGE
        val plan = PostgresExecutionPlanParser().parse(listOf(
            "Limit  (cost=11.64..11.65 rows=1 width=40)",
            "  ->  Sort  (cost=11.63..11.64 rows=1 width=40)",
            "        Sort Key: some_date",
            "        ->  Seq Scan on some_table  (cost=0.00..11.62 rows=1 width=40)",
            "              Filter: ((some_id IS NULL) AND ('2019-05-21 15:01:11.301'::timestamp without time zone <= some_date))"
        ))

        // ACT
        checkInxAssertService.assertCoverage(CoverageLevel.HALF, plan)
    }

    @Test
    fun testAssertCoverageGivenUsingIndexScanWhenLevelHalfThenSuccess() {
        // ARRANGE
        val plan = PostgresExecutionPlanParser().parse(listOf(
            "Limit  (cost=11.64..11.65 rows=1 width=40)",
            "  ->  Sort  (cost=11.63..11.64 rows=1 width=40)",
            "        Sort Key: some_date",
            "        ->  Index Scan using ix_some_index on some_table  (cost=0.00..11.62 rows=1 width=40)",
            "              Filter: ((some_id IS NULL) AND ('2019-05-21 15:01:11.301'::timestamp without time zone <= some_date))"
        ))

        // ACT
        checkInxAssertService.assertCoverage(CoverageLevel.HALF, plan)
    }

    @Test
    fun testAssertCoverageGivenOnIndexScanWhenLevelHalfThenSuccess() {
        // ARRANGE
        val plan = PostgresExecutionPlanParser().parse(listOf(
            "Limit  (cost=11.64..11.65 rows=1 width=40)",
            "  ->  Sort  (cost=11.63..11.64 rows=1 width=40)",
            "        Sort Key: some_date",
            "        ->  Index Scan on ix_some_index  (cost=0.00..11.62 rows=1 width=40)",
            "              Filter: ((some_id IS NULL) AND ('2019-05-21 15:01:11.301'::timestamp without time zone <= some_date))"
        ))

        // ACT
        checkInxAssertService.assertCoverage(CoverageLevel.HALF, plan)
    }

    @Test
    fun testAssertPredicateGivenOnIndexScanWhenLevelHalfThenSuccess() {
        // ARRANGE
        val plan = PostgresExecutionPlanParser().parse(listOf(
            "Limit  (cost=11.64..11.65 rows=1 width=40)",
            "  ->  Sort  (cost=11.63..11.64 rows=1 width=40)",
            "        Sort Key: some_date",
            "        ->  Index Scan on ix_some_index  (cost=0.00..11.62 rows=1 width=40)",
            "              Filter: ((some_id IS NULL) AND ('2019-05-21 15:01:11.301'::timestamp without time zone <= some_date))"
        ))

        // ACT
        checkInxAssertService.assertPlan(plan) {
            it.coverageLevel.level < CoverageLevel.HALF.level
        }
    }

    @Test(expected = PlanException::class)
    fun testAssertPlanWhenIndexScanWhenLevelFullThenPlanException() {
        // ARRANGE
        val plan = PostgresExecutionPlanParser().parse(listOf(
            "Limit  (cost=11.64..11.65 rows=1 width=40)",
            "  ->  Sort  (cost=11.63..11.64 rows=1 width=40)",
            "        Sort Key: some_date",
            "        ->  Index Scan on ix_some_index  (cost=0.00..11.62 rows=1 width=40)",
            "              Filter: ((some_id IS NULL) AND ('2019-05-21 15:01:11.301'::timestamp without time zone <= some_date))"
        ))

        // ACT
        checkInxAssertService.assertPlan(plan) {
            it.coverageLevel.level < CoverageLevel.FULL.level
        }
    }
}
