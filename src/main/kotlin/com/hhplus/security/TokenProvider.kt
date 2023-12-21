package com.hhplus.security

import com.hhplus.controller.authentication.WaitTokenDto
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import javax.crypto.spec.SecretKeySpec

@Service
class TokenProvider {

    private lateinit var orderNum : AtomicInteger

    @PostConstruct
    fun init() {
        this.orderNum = AtomicInteger(0);
    }

    /* TODO: 중복 토큰 생성 방지 및 처리율 제한 위해 RateLimiter 어떻게 만들까? */
    fun createToken(): WaitTokenDto {
        val secretKey = "abcasdgxclkjblkefkkwlerjioasdfsdgsa"
        val expirationHours = 1L
        val userUUID = UUID.randomUUID().toString()
        val userWaitNum = this.orderNum.getAndIncrement()
        val claims = mapOf<String, Any>("uuid" to userUUID,
            "order" to userWaitNum)

        return WaitTokenDto(userUUID, userWaitNum, Jwts.builder()
            .signWith(
                SecretKeySpec(
                    secretKey.toByteArray(),
                    SignatureAlgorithm.HS512.jcaName
                )
            ) // HS512 알고리즘을 사용하여 secretKey를 이용해 서명
            .setSubject("Waiting Job Token")   // JWT 토큰 제목
            .setClaims(claims)
            .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))    // JWT 토큰 발급 시간
            .setExpiration(Date.from(Instant.now().plus(expirationHours, ChronoUnit.HOURS)))    // JWT 토큰의 만료시간 설정(한시간)
            .compact()!!)    // JWT 토큰 생성
    }
}