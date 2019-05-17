package com.checkinx.utils.sql.interceptors.postgres

import com.checkinx.utils.sql.interceptors.SqlInterceptor
import net.ttddyy.dsproxy.support.ProxyDataSource

open class PostgresInterceptor(private val dataSource: ProxyDataSource) : SqlInterceptor {
    private var statementsList: MutableList<String> = mutableListOf()

    private val sqlListener = SqlStatementQueryExecutionListener(statementsList)

    override val statements: List<String>
        get() = statementsList.toList()

    override fun startInterception() {
        statementsList.clear()
        dataSource.addListener(sqlListener)
    }

    override fun stopInterception() {
        dataSource.proxyConfig.queryListener.listeners.removeIf { listener ->
            listener is SqlStatementQueryExecutionListener && listener.identifier == sqlListener.identifier}
    }
}
