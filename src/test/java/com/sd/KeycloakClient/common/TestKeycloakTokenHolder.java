package com.sd.KeycloakClient.common;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.auth.KeycloakTokenInfo;
import com.sd.KeycloakClient.factory.KeycloakClient;
import java.util.Objects;

public class TestKeycloakTokenHolder {

   private static String accessToken;
   private static String adminAccessToken;

   public static synchronized String getAccessToken(KeycloakClient keycloakClient) {
      if (Objects.isNull(accessToken)) {
         KeycloakResponse<KeycloakTokenInfo> tokenInfo = keycloakClient.auth().basicAuth("test-user-keycloak", "1234");
         KeycloakTokenInfo keycloakTokenInfo = tokenInfo.getBody().orElseThrow(() -> new RuntimeException("fail to get token"));
         accessToken = keycloakTokenInfo.getAccessToken();
      }
      return accessToken;
   }

   public static synchronized String getAdminAccessToken(KeycloakClient keycloakClient) {
      if (Objects.isNull(adminAccessToken)) {
         KeycloakResponse<KeycloakTokenInfo> tokenInfo = keycloakClient.auth().basicAuth("test-admin-user-keycloak", "1234");
         KeycloakTokenInfo keycloakTokenInfo = tokenInfo.getBody().orElseThrow(() -> new RuntimeException("fail to get admin token"));
         adminAccessToken = keycloakTokenInfo.getAccessToken();
      }
      return adminAccessToken;
   }
}
