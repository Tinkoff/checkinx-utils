package com.checkinx.utils.configs

import javax.sql.DataSource

import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder

open class DataSourceWrapper : BeanPostProcessor {
    override fun postProcessBeforeInitialization(bean: Any, beanName: String?): Any? {
        return if (bean is DataSource) {
            ProxyDataSourceBuilder
                .create(bean)
                .build()
        } else bean
    }

    @Throws(BeansException::class)
    override fun postProcessAfterInitialization(bean: Any, beanName: String?): Any? {
        return bean
    }
}
