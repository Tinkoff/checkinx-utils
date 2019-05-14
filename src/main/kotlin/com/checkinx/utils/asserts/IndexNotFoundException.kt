package com.checkinx.utils.asserts

class IndexNotFoundException(indexName: String, executionPlan: String)
    : Throwable("""Index name: $indexName, executionPlan: $executionPlan""")
