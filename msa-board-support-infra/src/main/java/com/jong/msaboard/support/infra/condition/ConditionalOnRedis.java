package com.jong.msaboard.support.infra.condition;

import com.jong.msaboard.support.infra.condition.ConditionalOnRedis.OnNotWebOrWebMvcCondition;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Conditional;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Conditional(OnNotWebOrWebMvcCondition.class)
@ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
public @interface ConditionalOnRedis {

    class OnNotWebOrWebMvcCondition extends AnyNestedCondition {

        public OnNotWebOrWebMvcCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnNotWebApplication
        public static class OnNotWebApplication {}

        @ConditionalOnWebApplication(type = Type.SERVLET)
        public static class OnWebMvcApplication {}
    }

}
