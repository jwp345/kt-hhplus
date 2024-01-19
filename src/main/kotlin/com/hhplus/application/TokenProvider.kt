package com.hhplus.application

import com.hhplus.domain.exception.FailedCreateTokenException
import com.hhplus.domain.exception.InvalidAuthenticationException
import com.hhplus.domain.repository.OrderCounterRepository
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
import java.time.ZoneOffset
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class TokenProvider(val waitQueueRepository: WaitQueueRepository, val validWaitTokenRepository: ValidWaitTokenRepository,
    val orderCounterRepository: OrderCounterRepository) {

    @Value("\${waitToken.set.max_size}")
    private lateinit var validTokenMaxsize : String
    @Value("\${waitToken.auth.name}")
    private lateinit var waitTokenAuth : String


    private val log = KotlinLogging.logger("TokenProvider")

    fun createToken(uuid: Long): String {
        try {
            val createAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            WaitToken(
                uuid = uuid, order = orderCounterRepository.incrementAndGet(), createAt = createAt
            ).let { token ->
                if (validWaitTokenRepository.getSize() <= validTokenMaxsize.toInt()) {
                    validWaitTokenRepository.add(token = token, ttl = 10, timeUnit = TimeUnit.SECONDS)
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