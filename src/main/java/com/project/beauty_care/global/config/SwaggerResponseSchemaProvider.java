package com.project.beauty_care.global.config;

import com.project.beauty_care.global.enums.SuccessCodes;
import io.swagger.v3.oas.models.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Field;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class SwaggerResponseSchemaProvider {
    private final MessageSource messageSource;

    private <T> void constructWrapSchema(Class<T> type,
                                         RequestMethod requestMethod,
                                         Schema<T> wrapperSchema,
                                         String methodName) {
        final String CODE = "successCode";
        final String MESSAGE = "successMessage";

        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);

            switch (requestMethod) {
                case GET:
                    if (field.getName().equals(CODE)) {
                        wrapperSchema.addProperty(field.getName(), new Schema<>().example(SuccessCodes.RETRIEVE_SUCCESS.getCode().value()));
                    }

                    if (field.getName().equals(MESSAGE)) {
                        wrapperSchema.addProperty(field.getName(), new Schema<>().example(SuccessCodes.RETRIEVE_SUCCESS.getMessage()));
                    }
                    break;
                case POST:
                    if (methodName.contains("create")) {
                        // 저장 - 201
                        if (field.getName().equals(CODE))
                            wrapperSchema.addProperty(field.getName(), new Schema<>().example(SuccessCodes.SAVE_SUCCESS.getCode().value()));
                        else if (field.getName().equals(MESSAGE)) {
                            String entity = methodName.replaceAll("^[a-z]+", "");
                            String localizedEntity = messageSource.getMessage(entity, null, Locale.KOREA);

                            wrapperSchema.addProperty(field.getName(),
                                    new Schema<>().example(localizedEntity + " " + SuccessCodes.SAVE_SUCCESS.getMessage()));
                        }
                    } else if (methodName.equals("login")) {
                        // 로그인 - 200
                        if (field.getName().equals(CODE))
                            wrapperSchema.addProperty(field.getName(), new Schema<>().example(SuccessCodes.LOGIN_SUCCESS.getCode().value()));
                        else if (field.getName().equals(MESSAGE)) {
                            wrapperSchema.addProperty(field.getName(), new Schema<>().example(SuccessCodes.LOGIN_SUCCESS.getMessage()));
                        }
                    } else if (methodName.contains("find")) {
                        // 조회 - 200
                        if (field.getName().equals(CODE))
                            wrapperSchema.addProperty(field.getName(), new Schema<>().example(SuccessCodes.RETRIEVE_SUCCESS.getCode().value()));
                        else if (field.getName().equals(MESSAGE)) {
                            wrapperSchema.addProperty(field.getName(), new Schema<>().example(SuccessCodes.RETRIEVE_SUCCESS.getMessage()));
                        }
                    } else if (methodName.equals("uploadTempFile") || methodName.equals("uploadFile")) {
                        if (field.getName().equals(CODE))
                            wrapperSchema.addProperty(field.getName(), new Schema<>().example(SuccessCodes.FILE_UPLOAD_SUCCESS.getCode().value()));
                        else if (field.getName().equals(MESSAGE)) {
                            wrapperSchema.addProperty(field.getName(), new Schema<>().example(SuccessCodes.FILE_UPLOAD_SUCCESS.getMessage()));
                        }
                    } else if (methodName.contains("scheduler")) {
                        if (field.getName().equals(CODE))
                            wrapperSchema.addProperty(field.getName(), new Schema<>().example(SuccessCodes.SCHEDULER_EXECUTE_SUCCESS.getCode().value()));
                        else if (field.getName().equals(MESSAGE)) {
                            wrapperSchema.addProperty(field.getName(), new Schema<>().example(SuccessCodes.SCHEDULER_EXECUTE_SUCCESS.getMessage()));
                        }
                    }
                    else
                        wrapperSchema.addProperty(field.getName(), new Schema<>().example("작업이 완료 되었습니다."));

                    break;
                case DELETE:
                    String message = "";

                    if (methodName.equals("deleteMember")) message = "탈퇴 완료 되었습니다.";
                    else message = "삭제 완료 되었습니다.";

                    if (field.getName().equals(CODE))
                        wrapperSchema.addProperty(field.getName(), new Schema<>().example("204"));

                    if (field.getName().equals(MESSAGE))
                        wrapperSchema.addProperty(field.getName(), new Schema<>().example(message));

                    break;
                case PATCH:
                    if (methodName.equals("deleteCancelMember")) {
                        if (field.getName().equals(CODE))
                            wrapperSchema.addProperty(field.getName(), new Schema<>().example("204"));


                        if (field.getName().equals(MESSAGE))
                            wrapperSchema.addProperty(field.getName(), new Schema<>().example("탈퇴 취소 되었습니다."));
                    }

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
    }

    // 공통 응답 wrapping 처리
    @SneakyThrows
    public  <T> Schema<T> wrapSchema(Schema<?> originalSchema, Class<T> type, String wrapFieldName, HandlerMethod handlerMethod) {
        final Schema<T> wrapperSchema = new Schema<>();

        // httpMethod
        RequestMethod requestMethod = handlerMethod.getMethodAnnotation(RequestMapping.class).method()[0];

        String methodName = handlerMethod.getMethod().getName();

        constructWrapSchema(type, requestMethod, wrapperSchema, methodName);
        wrapperSchema.addProperty(wrapFieldName, originalSchema);
        return wrapperSchema;
    }
}
