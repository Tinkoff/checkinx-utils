package com.checkinx.utils.sql.plan.parse.impl

import org.junit.Assert.*
import org.junit.Before

import org.junit.Test

class PostgresExecutionPlanParserTest {

    private lateinit var parserPostgres: PostgresExecutionPlanParser

    @Before
    fun setUp() {
        parserPostgres = PostgresExecutionPlanParser()
    }

    @Test
    fun testParseWhenUsingInFirstLineThenTableAndIndexNotSame() {
        // ARRANGE
        val plan = listOf(
            "Index Scan using ix_pets_age on pets  (cost=0.29..8.77 rows=1 width=36)",
            "  Index Cond: (age < 10)",
            "  Filter: ((name)::text = 'Jack'::text)"
        )

        // ACT
        val model = parserPostgres.parse(plan)

        // ASSERT
        assertNotNull(model)
    }

    @Test
    fun testParseWhenDeepTree() {
        // ARRANGE
        val plan = listOf(
            "Nested Loop Semi Join  (cost=0.00..3.11 rows=1 width=50)",
            "  Join Filter: ((pets.name)::text = (pets_1.name)::text)",
            "  ->  Seq Scan on pets  (cost=0.00..1.02 rows=2 width=50)",
            "  ->  Materialize  (cost=0.00..2.06 rows=1 width=32)",
            "        ->  Nested Loop Semi Join  (cost=0.00..2.06 rows=1 width=32)",
            "              ->  Seq Scan on pets pets_1  (cost=0.00..1.02 rows=1 width=34)",
            "                    Filter: (age = 2)",
            "              ->  Seq Scan on pets pets_2  (cost=0.00..1.02 rows=1 width=2)",
            "                    Filter: (age = 2)"
        )

        // ACT
        val model = parserPostgres.parse(plan)

        // ASSERT
        assertNotNull(model)
    }
}
