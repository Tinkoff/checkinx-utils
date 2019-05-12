package com.checkinx.utils

class IndexNotFoundException(indexName: String, executionPlan: String)
    : Throwable("""Index name: $indexName, executionPlan: $executionPlan""")
