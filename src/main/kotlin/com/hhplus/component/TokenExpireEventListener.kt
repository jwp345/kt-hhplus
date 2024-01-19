package com.hhplus.component

import com.hhplus.domain.repository.ValidWaitTokenRepository
import com.hhplus.domain.repository.WaitQueueRepository
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class TokenExpireEventListener(val validWaitTokenRepository: ValidWaitTokenRepository,
                               val waitQueueRepository: WaitQueueRepository) {

    @EventListener
    fun changeWaitToValid(event: TokenExpireEvent) {
        waitQueueRepository.pop()?.let { validWaitTokenRepository.add(token = it, ttl = 1, timeUnit = TimeUnit.HOURS) }
    }
}