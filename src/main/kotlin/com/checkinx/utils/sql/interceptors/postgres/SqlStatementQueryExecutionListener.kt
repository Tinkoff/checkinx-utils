package com.checkinx.utils.sql.interceptors.postgres

import com.zaxxer.hikari.pool.HikariProxyResultSet
import net.ttddyy.dsproxy.ExecutionInfo
import net.ttddyy.dsproxy.QueryInfo
import net.ttddyy.dsproxy.listener.QueryExecutionListener
import org.postgresql.jdbc.PgResultSet
import java.util.*

class SqlStatementQueryExecutionListener(private val statementsList: MutableList<String>) : QueryExecutionListener {
    val identifier: UUID = UUID.randomUUID()

    override fun beforeQuery(execInfo: ExecutionInfo, queryInfoList: List<QueryInfo>) = Unit

    override fun afterQuery(execInfo: ExecutionInfo, queryInfoList: List<QueryInfo>) {
        if (execInfo.result !is HikariProxyResultSet) {
            return
        }

        val sql = (execInfo.result as HikariProxyResultSet).unwrap<PgResultSet>(PgResultSet::class.java)
            .statement.toString()

        statementsList.add(sql)
    }
}
