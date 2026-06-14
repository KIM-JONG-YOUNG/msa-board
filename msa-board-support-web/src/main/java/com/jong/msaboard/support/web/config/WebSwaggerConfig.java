package com.jong.msaboard.support.web.config;

import com.jong.msaboard.common.constants.HeaderNames;
import com.jong.msaboard.support.web.condition.ConditionalOnSwagger;
import com.jong.msaboard.support.web.constants.SecurityExpressions;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Arrays;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.customizers.PropertyCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.prepost.PreAuthorize;

@Configuration
@ConditionalOnSwagger
public class WebSwaggerConfig {

    @Bean
    OpenApiCustomizer openApiCustomizer() {
        return openApi -> openApi.getComponents()
            .addSecuritySchemes(HeaderNames.ACCESS_TOKEN, new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name(HeaderNames.ACCESS_TOKEN));
    }

    @Bean
    OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            var annotation = AnnotationUtils.getAnnotation(handlerMethod.getMethod(), PreAuthorize.class);
            if (annotation != null) {
                if (!SecurityExpressions.IS_ANONYMOUS.equals(annotation.value())) {
                    operation.addSecurityItem(new SecurityRequirement().addList(HeaderNames.ACCESS_TOKEN));
                }
            }
            return operation;
        };
    }

    @Bean
    PropertyCustomizer propertyCustomizer() {
        return (schema, type) -> {
            if (type.getType() instanceof Class<?> clazz && clazz.isEnum()) {
                schema.setEnum(Arrays.stream(clazz.getEnumConstants())
                    .map(Enum.class::cast)
                    .map(Enum::name)
                    .toList());
            }
            return schema;
        };
    }

}
