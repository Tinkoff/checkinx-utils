package com.checkinx.utils.asserts

enum class CoverageLevel(val level: Int) {
    /* Index isn't using at all. */
    NOT_USING(Int.MIN_VALUE),

    /* Seq Scan */
    ZERO(0),

    /* Index Scan */
    HALF(1),

    /* Index Only Scan */
    FULL(2),

    UNKNOWN(Int.MAX_VALUE)
}
