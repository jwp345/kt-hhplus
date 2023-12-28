package com.hhplus.infrastructure.security

import com.hhplus.domain.repository.WaitOrderRepository
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class TokenProvider(val waitOrderRepository: WaitOrderRepository) {

    @Value("\${jwt.secret}")
    lateinit var secretKey: String
    private val key by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)) }
    private val auth = "auth_booking"

    /* TODO: 중복 토큰 생성 방지 및 처리율 제한 위해 RateLimiter? */
    fun createToken(uuid: Long): WaitToken {
        val expirationHours = 1L
        val userOrder = waitOrderRepository.getWaitOrder()
        val claims = mapOf(
            "uuid" to uuid,
            "order" to userOrder,
            "auth" to auth
        )
        waitOrderRepository.save(uuid = uuid, order = userOrder)
        return WaitToken(
            uuid = uuid, order = userOrder, Jwts.builder()
                .signWith(
                    key, SignatureAlgorithm.HS256
                )
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

    fun getAuthentication(token: String): Authentication {
        val claims: Claims = getClaims(token)

        val uuid = claims["uuid"] ?: throw RuntimeException("잘못된 토큰입니다.")
        claims["order"] ?: throw RuntimeException("잘못된 토큰입니다.")
        if(claims["auth"] == null || !claims["auth"]?.equals(auth)!!) {
            throw RuntimeException("잘못된 토큰입니다.")
        }

        val authorities: Collection<GrantedAuthority> = (auth)
            .split(",")
            .map { SimpleGrantedAuthority(it) }

        val principal: UserDetails = User(uuid.toString(), "", authorities)

        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    fun validateToken(token: String): Boolean {
        try {
            val claims = getClaims(token)
            val uuid = claims["uuid"].toString().toLong()
            val order = claims["order"].toString().toLong()
            return waitOrderRepository.findWaitOrderByUuid(uuid = uuid) == order
        } catch (e: Exception) { // exception 발생 시 403
            when (e) {
                is SecurityException -> {}  // Invalid JWT Token
                is MalformedJwtException -> {}  // Invalid JWT Token
                is ExpiredJwtException -> {}    // Expired JWT Token
                is UnsupportedJwtException -> {}    // Unsupported JWT Token
                is IllegalArgumentException -> {}   // JWT claims string is empty
                else -> {}  // else
            }
        }
        return false
    }

    private fun getClaims(token: String): Claims =
        Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
}