package com.sd.KeycloakClient.testExtends;

import static org.keycloak.OAuth2Constants.CODE;

import com.sd.KeycloakClient.annotation.MockKeycloakClient;
import com.sd.KeycloakClient.common.KeycloakShareTestContainer;
import com.sd.KeycloakClient.config.ClientConfiguration;
import com.sd.KeycloakClient.factory.KeycloakClient;
import com.sd.KeycloakClient.properties.KeycloakProperties;
import java.lang.reflect.Field;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author SangWonYu
 * @date 2025-04
 * @description this is extension for default keycloak client settings.
 */
public class KeycloakClientExtension implements BeforeAllCallback {

   private static final String REDIRECT_URI = "http://localhost:8080/redirect";
   private static final String LOGOUT_URI = "http://localhost:8080/logout";

   @Override
   public void beforeAll(ExtensionContext context) throws Exception {
      try {
         Class<?> testClass = context.getRequiredTestClass();
         Object testInstance = context.getRequiredTestInstance();

         if (!testClass.isAnnotationPresent(MockKeycloakClient.class)) {
            return;
         }

         MockKeycloakClient annotation = testClass.getAnnotation(MockKeycloakClient.class);
         KeycloakClient keycloakClient = createKeycloakClient();

         injectKeycloakClient(testClass, testInstance, annotation.fieldName(), keycloakClient);

      } catch (NoSuchFieldException e) {
         throw new IllegalStateException(
             "📛 Not Found fieldName. " +
                 "Please check '@MockKeycloakClient(fieldName = \"keycloakClient\")'", e);
      }
   }

   private KeycloakClient createKeycloakClient() throws Exception {
      KeycloakProperties properties = new KeycloakProperties();
      ClientConfiguration config = ClientConfiguration.builder()
          .baseUrl(KeycloakShareTestContainer.authServerUrl())
          .realmName(properties.getRealmName())
          .clientId(properties.getClientId())
          .clientSecret(properties.getClientSecret())
          .redirectUri(REDIRECT_URI)
          .logoutRedirectUri(LOGOUT_URI)
          .responseType(CODE)
          .build();

      return new KeycloakClient(config);
   }

   private void injectKeycloakClient(Class<?> testClass, Object testInstance, String fieldName, KeycloakClient client)
       throws NoSuchFieldException, IllegalAccessException {

      Field clientField = testClass.getDeclaredField(fieldName);
      clientField.setAccessible(true);
      clientField.set(testInstance, client);
   }
}
