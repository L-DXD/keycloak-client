package com.sd.KeycloakClient.testExtends;


import com.sd.KeycloakClient.annotation.MockWebServer;
import java.lang.reflect.Field;
import java.util.Objects;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author SangWonYu
 * @date 2025-04
 */
public class MockWebServerExtension implements BeforeEachCallback, AfterEachCallback {

   @Override
   public void beforeEach(ExtensionContext context) throws Exception {
      okhttp3.mockwebserver.MockWebServer mockWebServer = getMockWebServer(context);
      if (Objects.nonNull(mockWebServer)) {
         mockWebServer.start();
      }
   }

   @Override
   public void afterEach(ExtensionContext context) throws Exception {
      okhttp3.mockwebserver.MockWebServer mockWebServer = getMockWebServer(context);
      if (Objects.nonNull(mockWebServer)) {
         mockWebServer.shutdown();
      }
   }

   private okhttp3.mockwebserver.MockWebServer getMockWebServer(ExtensionContext context)
       throws NoSuchFieldException, IllegalAccessException {
      try {
         Class<?> testClass = context.getRequiredTestClass();
         if (testClass.isAnnotationPresent(MockWebServer.class)) {
            Object testInstance = context.getRequiredTestInstance();
            MockWebServer annotation = testClass.getAnnotation(MockWebServer.class);

            Field field = testClass.getDeclaredField(annotation.fieldName());
            field.setAccessible(true);
            if (Objects.isNull(field.get(testInstance))) {
               field.set(testInstance, new okhttp3.mockwebserver.MockWebServer());
            }
            return (okhttp3.mockwebserver.MockWebServer) field.get(testInstance);
         }
         ;
         return null;
      } catch (Exception e) {
         if (e instanceof NoSuchFieldException) {
            throw new IllegalStateException(
                "Not found fieldName " +
                    "check @MockWebServerTest(fieldName = \"server\").", e);
         }

         throw e;
      }
   }

   ;
}
