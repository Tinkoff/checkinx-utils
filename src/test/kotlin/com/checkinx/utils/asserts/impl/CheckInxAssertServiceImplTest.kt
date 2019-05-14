package com.checkinx.utils.asserts.impl

import com.checkinx.utils.asserts.CoverageLevel
import com.checkinx.utils.asserts.CoverageLevelException
import com.checkinx.utils.asserts.IndexNotFoundException
import com.checkinx.utils.sql.plan.parse.impl.PostgresExecutionPlanParser
import io.mockk.mockk
import org.junit.Assert.fail
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
        try {
            checkInxAssertService.assertCoverage(CoverageLevel.FULL, "ix_pets_age", plan)
        } catch (e: Exception) {
            fail("no exception expected")
        }
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
        try {
            checkInxAssertService.assertCoverage(CoverageLevel.HALF, "ix_pets_age", plan)
        } catch (e: Exception) {
            fail("no exception expected")
        }
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
}
