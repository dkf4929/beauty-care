package com.project.beauty_care.global.config;

import com.project.beauty_care.global.SuccessResponse;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "Bearer"
)
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
    private static final String TITLE = "BEAUTY CARE";
    private static final String DESCRIPTION = "BEAUTY CARE REST API DOCUMENTATION";
    private static final String VERSION = "1.0.0";
    private final SwaggerResponseSchemaProvider provider;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title(TITLE)
                .description(DESCRIPTION)
                .version(VERSION);
    }

    // 공통 응답 처리
    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            this.addResponseBodyWrapperSchemaExample(operation, SuccessResponse.class, "data", handlerMethod);
            return operation;
        };
    }

    private void addResponseBodyWrapperSchemaExample(Operation operation,
                                                     Class<?> type,
                                                     String wrapFieldName,
                                                     HandlerMethod handlerMethod) {
        for (String responseCode : new String[]{"200", "201", "204"}) {
            Content content =
                    operation.getResponses().get(responseCode) != null ? operation.getResponses().get(responseCode).getContent() : null;

            if (content != null) {
                content.keySet()
                        .forEach(mediaTypeKey -> {
                            final MediaType mediaType = content.get(mediaTypeKey);
                            mediaType.schema(provider.wrapSchema(mediaType.getSchema(), type, wrapFieldName, handlerMethod));
                        });
            }
        }
    }
}
