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
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Field;
import java.util.Locale;

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
    private final MessageSource messageSource;

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
                            mediaType.schema(wrapSchema(mediaType.getSchema(), type, wrapFieldName, handlerMethod));
                        });
            }
        }
    }

    // 공통 응답 wrapping 처리
    @SneakyThrows
    private <T> Schema<T> wrapSchema(Schema<?> originalSchema, Class<T> type, String wrapFieldName, HandlerMethod handlerMethod) {
        final Schema<T> wrapperSchema = new Schema<>();

        // httpMethod
        RequestMethod requestMethod = handlerMethod.getMethodAnnotation(RequestMapping.class).method()[0];

        String methodName = handlerMethod.getMethod().getName();

        final String CODE = "successCode";
        final String MESSAGE = "successMessage";

        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);

            switch (requestMethod) {
                case GET:
                    if (field.getName().equals(CODE)) {
                        wrapperSchema.addProperty(field.getName(), new Schema<>().example("200"));
                    }

                    if (field.getName().equals(MESSAGE)) {
                        wrapperSchema.addProperty(field.getName(), new Schema<>().example("조회가 완료 되었습니다."));
                    }
                    break;
                case POST:
                    if (methodName.contains("create")) {
                        // 저장 - 201
                        if (field.getName().equals(CODE))
                            wrapperSchema.addProperty(field.getName(), new Schema<>().example("201"));
                        else if (field.getName().equals(MESSAGE)) {
                            String entity = methodName.replaceAll("^[a-z]+", "");
                            String localizedEntity = messageSource.getMessage(entity, null, Locale.KOREA);

                            wrapperSchema.addProperty(field.getName(), new Schema<>().example(localizedEntity + " 저장이 완료 되었습니다."));
                        }
                    } else if (methodName.equals("login")) {
                        // 로그인 - 200
                        if (field.getName().equals(CODE))
                            wrapperSchema.addProperty(field.getName(), new Schema<>().example("200"));
                        else if (field.getName().equals(MESSAGE)) {
                            wrapperSchema.addProperty(field.getName(), new Schema<>().example("로그인 성공"));
                        }
                    } else if (methodName.contains("find")) {
                        // 조회 - 200
                        if (field.getName().equals(CODE))
                            wrapperSchema.addProperty(field.getName(), new Schema<>().example("200"));
                        else if (field.getName().equals(MESSAGE)) {
                            wrapperSchema.addProperty(field.getName(), new Schema<>().example("조회가 완료 되었습니다."));
                        }
                    }
                    else
                        wrapperSchema.addProperty(field.getName(), new Schema<>().example("작업이 완료 되었습니다."));

                    break;
                case DELETE:
                    if (field.getName().equals(CODE))
                        wrapperSchema.addProperty(field.getName(), new Schema<>().example("204"));

                    if (field.getName().equals(MESSAGE))
                        wrapperSchema.addProperty(field.getName(), new Schema<>().example("삭제가 완료되었습니다."));

                    break;
                case PUT:
                    if (field.getName().equals(CODE))
                        wrapperSchema.addProperty(field.getName(), new Schema<>().example("200"));


                    if (field.getName().equals(MESSAGE))
                        wrapperSchema.addProperty(field.getName(), new Schema<>().example("수정 완료되었습니다."));
                    break;
                default:
                    wrapperSchema.addProperty(field.getName(), new Schema<>());
            }

            field.setAccessible(false);
        }
        wrapperSchema.addProperty(wrapFieldName, originalSchema);
        return wrapperSchema;
    }
}
