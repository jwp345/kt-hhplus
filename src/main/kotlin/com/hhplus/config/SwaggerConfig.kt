package com.hhplus.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun swaggerApi(): OpenAPI = OpenAPI()
//        .components(Components()
//            // 여기부터 추가 부분
//            .addSecuritySchemes(SECURITY_SCHEME_NAME, SecurityScheme()
//                .name(SECURITY_SCHEME_NAME)
//                .type(SecurityScheme.Type.HTTP)
//                .scheme("bearer")
//                .bearerFormat("JWT")))
//        .addSecurityItem(SecurityRequirement().addList(SECURITY_SCHEME_NAME))
        .info(
            Info()
            .title("콘서트 예약 서비스")
            .description("콘서트 예약 서비스를 위한 api들입니다.")
            .version("1.0.0"))
}