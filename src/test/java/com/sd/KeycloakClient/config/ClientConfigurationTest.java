package com.sd.KeycloakClient.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sd.KeycloakClient.dto.admin.ScopeQueryParams;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ClientConfigurationTest {

   private static final String BASE_URL = "http://localhost:8080";
   private static final String REALM_NAME = "test-realm";
   private static final String RELATIVE_PATH = "keycloak";
   private static final String CLIENT_ID = "de5fe303-2e50-428e-ac72-b81d1dc5139a";
   private static final UUID CLIENT_UUID = UUID.fromString("de5fe303-2e50-428e-ac72-b81d1dc5139a");
   private static final String REDIRECT_URI = "http://localhost:8080/callback";
   private static final String LOGOUT_REDIRECT_URI = "http://localhost:8080/logout";
   private static final String RESPONSE_TYPE = "code";
   private static final String CLIENT_SECRET = "test-client-secret";

   private static final ClientConfiguration cfg = ClientConfiguration.builder()
       .baseUrl(BASE_URL)
       .realmName(REALM_NAME)
       .relativePath(RELATIVE_PATH)
       .clientId(CLIENT_ID)
       .redirectUri(REDIRECT_URI)
       .logoutRedirectUri(LOGOUT_REDIRECT_URI)
       .responseType(RESPONSE_TYPE)
       .clientSecret(CLIENT_SECRET)
       .build();

   @Test
   @DisplayName("1. should return scope URL for client")
   void getAuthScopeUrl() {
      String url = cfg.getAuthScopeUrl(CLIENT_UUID);
      String expected = RELATIVE_PATH + "/admin/realms/" + REALM_NAME
          + "/clients/" + CLIENT_ID + "/authz/resource-server/scope";
      assertEquals(expected, url);
   }

   @Test
   @DisplayName("2. should return scope URL including scope UUID")
   void getAuthScopeUrlWithScopeUuid() {
      UUID scopeUuid = UUID.randomUUID();
      String url = cfg.getAuthScopeUrl(CLIENT_UUID, scopeUuid);
      String expected = RELATIVE_PATH + "/admin/realms/" + REALM_NAME
          + "/clients/" + CLIENT_ID + "/authz/resource-server/scope/" + scopeUuid;
      assertEquals(expected, url);
   }

   @Test
   @DisplayName("3. should append query params as-is to scope search URL")
   void getAuthScopeSearchUrl_withRawQueryString() {
      String url = cfg.getAuthScopeSearchUrl(CLIENT_UUID, "?name=test");
      String expected = RELATIVE_PATH + "/admin/realms/" + REALM_NAME
          + "/clients/" + CLIENT_ID + "/authz/resource-server/scope?name=test";
      assertEquals(expected, url);
   }

   @Test
   @DisplayName("4. should append query params created by ScopeQueryParams (integration smoke)")
   void getAuthScopeSearchUrl_withScopeQueryParams() {
      String qs = ScopeQueryParams.builder().first(5).max(20).name("scopeX").build().toQueryString();
      String url = cfg.getAuthScopeSearchUrl(CLIENT_UUID, qs);
      String expected = RELATIVE_PATH + "/admin/realms/" + REALM_NAME
          + "/clients/" + CLIENT_ID + "/authz/resource-server/scope?first=5&max=20&name=scopeX";
      assertEquals(expected, url);
   }
}
