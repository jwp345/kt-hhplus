package com.hhplus.infrastructure.security.logging

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.MDC
import org.springframework.stereotype.Component

@Aspect
@Component
class LoggingAspect {
    @Before("execution(* com.hhplus.presentation..*Controller.*(..))")
    fun logMethodCall(joinPoint : JoinPoint) {
            MDC.put("className", joinPoint.signature.declaringTypeName)
            MDC.put("functionName", joinPoint.signature.name)
            val args = getMethodArguments(joinPoint)
            MDC.put("requestParams", args)

    }

    private fun getMethodArguments(joinPoint: JoinPoint): String {
        val methodSignature = joinPoint.signature as MethodSignature
        val parameterNames = methodSignature.parameterNames
        val parameterValues = joinPoint.args

        return parameterNames.zip(parameterValues)
            .filter { it.second != null }.joinToString(" ") { "${it.first}=${it.second}" }
    }
}