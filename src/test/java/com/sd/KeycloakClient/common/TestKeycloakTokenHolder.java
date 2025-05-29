package com.sd.KeycloakClient.common;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.auth.KeycloakTokenInfo;
import com.sd.KeycloakClient.factory.KeycloakClient;
import java.util.Objects;

public class TestKeycloakTokenHolder {

   public static final String GENERAL_USER_NAME = "test-user-keycloak";
   public static final String ADMIN_USER_NAME = "test-admin-user-keycloak";
   private static final String PASSWORD = "1234";

   private static String accessToken;
   private static String adminAccessToken;

   public static synchronized String getAccessToken(KeycloakClient keycloakClient) {
      if (Objects.isNull(accessToken)) {
         KeycloakResponse<KeycloakTokenInfo> tokenInfo = keycloakClient.auth().basicAuth(GENERAL_USER_NAME, PASSWORD);
         KeycloakTokenInfo keycloakTokenInfo = tokenInfo.getBody().orElseThrow(() -> new RuntimeException("fail to get token"));
         accessToken = keycloakTokenInfo.getAccessToken();
      }
      return accessToken;
   }

   public static synchronized void removeAccessToken() {
      accessToken = null;
   }

   public static synchronized String getAdminAccessToken(KeycloakClient keycloakClient) {
      if (Objects.isNull(adminAccessToken)) {
         KeycloakResponse<KeycloakTokenInfo> tokenInfo = keycloakClient.auth().basicAuth(ADMIN_USER_NAME, PASSWORD);
         KeycloakTokenInfo keycloakTokenInfo = tokenInfo.getBody().orElseThrow(() -> new RuntimeException("fail to get admin token"));
         adminAccessToken = keycloakTokenInfo.getAccessToken();
      }
      return adminAccessToken;
   }

   public static synchronized void removeAdminAccessToken() {
      adminAccessToken = null;
   }
}
