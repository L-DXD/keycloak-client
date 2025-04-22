package com.sd.KeycloakClient.annotation;


import com.sd.KeycloakClient.testExtends.MockWebServerExtension;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author SangWonYu
 * @date 2025-04
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({MockWebServerExtension.class})
@Documented
public @interface MockWebServer {

   String fieldName() default "server";
}
