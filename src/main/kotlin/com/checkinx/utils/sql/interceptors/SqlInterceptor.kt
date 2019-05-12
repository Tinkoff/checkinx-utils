package com.checkinx.utils.sql.interceptors

interface SqlInterceptor {

    val statements: List<String>

    fun startInterception()
    fun stopInterception()

}
