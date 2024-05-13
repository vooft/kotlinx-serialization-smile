package io.github.vooft.kotlinsmile

import kotlinx.serialization.Serializable

 @Serializable
data class LogMessage(
    val timestampEpoch: Long,
    val level: String,
    val message: String,
    val threadName: String,
    val loggerName: String,
    val traceId: String,
    val spanId: String,
    val context: LogMessageContext
)

@Serializable
data class LogMessageContext(val username: String, val url: String, val userAgent: String)

