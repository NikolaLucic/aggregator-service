package com.nikola.aggregatorservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import springfox.documentation.swagger.web.UiConfiguration
import springfox.documentation.swagger.web.TagsSorter
import springfox.documentation.swagger.web.OperationsSorter
import springfox.documentation.swagger.web.DocExpansion
import springfox.documentation.swagger.web.ModelRendering
import springfox.documentation.swagger.web.UiConfigurationBuilder



@Configuration
@EnableSwagger2
class SwaggerConfig {

    @Bean
    fun api() = Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.nikola.aggregatorservice.controller"))
        .paths(PathSelectors.any())
        .build()

    @Bean
    fun uiConfig(): UiConfiguration {
        return UiConfigurationBuilder.builder()
            .displayRequestDuration(true) //because I really wanted to see response times in swagger UI
            .build()
    }

}
