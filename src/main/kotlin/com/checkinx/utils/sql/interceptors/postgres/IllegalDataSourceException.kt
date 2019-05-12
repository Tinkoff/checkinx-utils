package com.checkinx.utils.sql.interceptors.postgres

class IllegalDataSourceException(typeName: String) : Throwable(typeName)
