package com.classroom.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI 元信息：访问 {@code http://localhost:8080/swagger-ui.html} 时首页展示。
 *
 * <p>接口清单本身由 springdoc 扫描 Controller 自动生成，这里只补"文档说明"，
 * 不做任何手工维护，避免文档与代码不一致。
 *
 * @author M3 后端 API
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI classroomOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("移动互动课堂系统 API")
                        .version("v0.1.0")
                        .description("""
                                课堂互动后端接口：登录、建课、签到、答题、抢答、数据看板。

                                - 统一响应体：{"code":0,"message":"success","data":{}}
                                - 错误码表：见 docs/04-接口契约模板.md §0.3
                                - 鉴权：请求头 Authorization: Bearer <token>
                                - 实时通道：ws://localhost:8080/ws（由 M4 实现）
                                """)
                        .contact(new Contact().name("移动互动课堂项目组").url("https://github.com/45loserrr/InteractiveClassroom"))
                        .license(new License().name("教学实训项目 · 内部使用")));
    }
}
