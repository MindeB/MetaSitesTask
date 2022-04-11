package com.metasites.MetaSitesTask.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.stereotype.Component;

@Component
@OpenAPIDefinition(
        info = @Info(title = "${springdoc.swagger-ui.title:}", version = "${springdoc.swagger-ui.version:}", description = "${springdoc.swagger-ui.description:}"))
public class DefaultOpenAPIConfig {
}
