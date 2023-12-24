package com.hhplus.infrastructure.security

import com.hhplus.component.UserWaitOrderFactory
import com.hhplus.presentation.authentication.WaitTokenResponse
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Service
class TokenProvider(val userWaitOrderFactory: UserWaitOrderFactory) {

    /* TODO: 중복 토큰 생성 방지 및 처리율 제한 위해 RateLimiter? */
    fun createToken(uuid: Long): WaitToken {
        val secretKey = "abcasdgxclkjblkefkkwlerjioasdfsdgsa"
        val expirationHours = 1L
        val userOrder = userWaitOrderFactory.getWaitOrder()
        val claims = mapOf(
            "uuid" to uuid,
            "order" to userOrder
        )

        return WaitToken(
            uuid = uuid, order = userOrder, Jwts.builder()
                .signWith(
                    SecretKeySpec(
                        secretKey.toByteArray(),
                        SignatureAlgorithm.HS512.jcaName
                    )
                ) // HS512 알고리즘을 사용하여 secretKey를 이용해 서명
                .setSubject("Waiting Job Token")   // JWT 토큰 제목
                .setClaims(claims)
                .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))    // JWT 토큰 발급 시간
                .setExpiration(
                    Date.from(
                        Instant.now().plus(expirationHours, ChronoUnit.HOURS)
                    )
                )    // JWT 토큰의 만료시간 설정(한시간)
                .compact()!!
        )
    }
}