package com.zhangls.base.retrofit.common

import com.blankj.utilcode.util.LogUtils
import okhttp3.logging.HttpLoggingInterceptor

/**
 * 将请求日志打印到控制台或者日志文件中
 *
 * @author zhangls
 */
class RequestLogger(private val maxLength: Int = 2000) : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        val msg = if (message.length > maxLength) {
            // 截断长日志
            message.substring(0, maxLength) + "...(超长日志内容，已省略)"
        } else {
            message
        }
        LogUtils.i(msg)
    }
}