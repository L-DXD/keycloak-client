package com.sd.KeycloakClient.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ClientConfigurationTest {

   private static final String BASE_URL = "http://localhost:8080";
   private static final String REALM_NAME = "test-realm";
   private static final String RELATIVE_PATH = "keycloak";
   private static final String CLIENT_ID = "prm-client";
   private static final String REDIRECT_URI = "http://localhost:8080/callback";
   private static final String LOGOUT_REDIRECT_URI = "http://localhost:8080/logout";
   private static final String RESPONSE_TYPE = "code";
   private static final String CLIENT_SECRET = "test-client-secret";


   private static final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
       .baseUrl(BASE_URL)
       .realmName(REALM_NAME)
       .relativePath(RELATIVE_PATH)
       .clientId(CLIENT_ID)
       .redirectUri(REDIRECT_URI)
       .logoutRedirectUri(LOGOUT_REDIRECT_URI)
       .responseType(RESPONSE_TYPE)
       .clientSecret(CLIENT_SECRET)
       .build();
   private static final String SCOPE_UUID = "test-scope-uuid";

   @Test
   @DisplayName("case1. success case: get token url")
   void getAuthScopeUrl() {
      String authScopeUrl = clientConfiguration.getAuthScopeUrl(CLIENT_ID);
      final String expectedUrl =
          RELATIVE_PATH + "/admin/realms/" + clientConfiguration.getRealmName() + "/clients/" + CLIENT_ID + "/authz/resource-server/scope";

      Assertions.assertEquals(authScopeUrl, expectedUrl);
   }

   @Test
   @DisplayName("case2. success case: get auth scope url with scope uuid")
   void getAuthScopeUrlWithScopeUuid() {
      String authScopeUrl = clientConfiguration.getAuthScopeUrl(CLIENT_ID, SCOPE_UUID);
      final String expectedUrl =
          RELATIVE_PATH + "/admin/realms/" + clientConfiguration.getRealmName() + "/clients/" + CLIENT_ID + "/authz/resource-server/scope/"
              + SCOPE_UUID;

      Assertions.assertEquals(authScopeUrl, expectedUrl);
   }

   @Test
   @DisplayName("case3. success case: get auth scope search url with query params")
   void getAuthScopeSearchUrl() {
      String authScopeSearchUrl = clientConfiguration.getAuthScopeSearchUrl(CLIENT_ID, "?name=test");
      final String expectedUrl =
          RELATIVE_PATH + "/admin/realms/" + clientConfiguration.getRealmName() + "/clients/" + CLIENT_ID
              + "/authz/resource-server/scope?name=test";
      Assertions.assertEquals(authScopeSearchUrl, expectedUrl);
   }
}