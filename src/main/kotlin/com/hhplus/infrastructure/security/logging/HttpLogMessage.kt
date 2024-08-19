package com.hhplus.infrastructure.security.logging

import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.web.util.ContentCachingResponseWrapper

data class HttpLogMessage(
    val httpMethod: String,
    val requestUrl: StringBuffer,
    val httpStatus: HttpStatus,
    val requestParam: String,
    val requestBody: String,
    val responseBody: String,
    val traceId: String,
) {
    companion object {
        fun createInstance(
            requestWrapper: RepeatableContentCachingRequestWrapper,
            responseWrapper: ContentCachingResponseWrapper,
            traceId : String
        ): HttpLogMessage {
            return HttpLogMessage(
                httpMethod = requestWrapper.method,
                requestUrl = requestWrapper.requestURL,
                httpStatus = HttpStatus.valueOf(responseWrapper.status),
                requestParam = requestWrapper.parameterMap.toString(),
                requestBody = requestWrapper.readInputAndDuplicate(),
                responseBody = String(responseWrapper.contentAsByteArray),
                traceId = traceId,
//                className = className,
//                functionName = requestWrapper.
            )
        }
    }

    fun toPrettierLog(): String {
        return """
        |
        |{
	    |    "traceId": ${this.traceId}
        |    "url" : ${this.requestUrl}
        |    "level": "info"
        |    "payload": {
        |        "className": ${MDC.get("className")}
        |        "functionName": ${MDC.get("functionName")}
        |        "parameter" : ${MDC.get("requestParams")},
        |    },
        |    "method": ${this.httpMethod},
        |    "request": ${this.requestBody} 
        |    "response": ${this.responseBody}
        |}
        """.trimMargin()
    }
}