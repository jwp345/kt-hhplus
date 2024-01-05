package com.hhplus.application

import com.hhplus.domain.exception.FailedCreateTokenException
import com.hhplus.domain.exception.InvalidAuthenticationException
import com.hhplus.domain.repository.ValidWaitTokenRepository
import com.hhplus.domain.repository.WaitQueueRepository
import com.hhplus.infrastructure.security.CustomUser
import com.hhplus.infrastructure.security.WaitToken
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicLong

@Service
class TokenProvider(val waitQueueRepository: WaitQueueRepository, val validWaitTokenRepository: ValidWaitTokenRepository) {

    @Value("\${waitToken.set.max_size}")
    private lateinit var validTokenMaxsize : String
    @Value("\${waitToken.auth.name}")
    private lateinit var waitTokenAuth : String
    private var order : AtomicLong = AtomicLong(0L)

    private val log = KotlinLogging.logger("TokenProvider")

    /* TODO: 중복 토큰 생성 방지 및 처리율 제한 위해(따닥 방지위해) RateLimiter? */
    fun createToken(uuid: Long): String {
        try {
            WaitToken(
                uuid = uuid, order = this.order.incrementAndGet(), createAt = LocalDateTime.now()
            ).let { token ->

                if (validWaitTokenRepository.getSize() <= validTokenMaxsize.toInt()) {
                    validWaitTokenRepository.add(token)
                } else {
                    waitQueueRepository.add(token)
                }

                return ByteArrayOutputStream().use { byteArrayOutputStream ->
                    ObjectOutputStream(byteArrayOutputStream).use {
                        it.writeObject(token)
                    }
                    Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
                }
            }
        } catch (e : Exception) {
            log.error { e.cause.toString() }
            throw FailedCreateTokenException()
        }

    }

    fun getAuthentication(token: WaitToken): Authentication {
        try {
        val authorities: Collection<GrantedAuthority> = waitTokenAuth
            .split(",")
            .map { SimpleGrantedAuthority(it) }
        val principal: UserDetails = CustomUser(token = token, userName = token.uuid.toString(), password = "", authorities = authorities)


            return UsernamePasswordAuthenticationToken(principal, "", authorities)
        } catch (e : Exception) {
            log.error { e.cause.toString() }
            throw InvalidAuthenticationException()
        }
    }

    fun validateToken(token: WaitToken): Boolean {
        return validWaitTokenRepository.contains(token)
    }

}