package com.medicine.demo1.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("药品有效期管理系统 API")
                        .version("1.0.0")
                        .description("药品有效期管理小程序后端接口文档")
                        .contact(new Contact()
                                .name("开发者")
                                .email("dev@example.com")));
    }
}
