package com.hhplus.controller.user

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserRestController {

    @PostMapping("/{userId}/charge")
    fun chargeMoney(@PathVariable userId : Long, @RequestParam("amount") amount : Int) {
        // 접속 정보와 요청하는 userId 값이 같은지 확인

    }
}