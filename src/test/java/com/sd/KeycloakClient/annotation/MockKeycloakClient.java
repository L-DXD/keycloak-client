package com.sd.KeycloakClient.annotation;

import com.sd.KeycloakClient.testExtends.KeycloakClientExtension;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author User
 * @date 2025-04
 * @description This annotation is keycloak client for mocking. this annotation require KeycloakShareTestContainer.
 */


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({KeycloakClientExtension.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Documented
public @interface MockKeycloakClient {

   String fieldName() default "keycloakClient";

}
