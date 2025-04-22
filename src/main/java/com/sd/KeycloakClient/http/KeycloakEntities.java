package com.sd.KeycloakClient.http;


import static org.keycloak.OAuth2Constants.AUDIENCE;
import static org.keycloak.OAuth2Constants.AUTHORIZATION_CODE;
import static org.keycloak.OAuth2Constants.CLIENT_ID;
import static org.keycloak.OAuth2Constants.CLIENT_SECRET;
import static org.keycloak.OAuth2Constants.CODE;
import static org.keycloak.OAuth2Constants.GRANT_TYPE;
import static org.keycloak.OAuth2Constants.PASSWORD;
import static org.keycloak.OAuth2Constants.REDIRECT_URI;
import static org.keycloak.OAuth2Constants.REFRESH_TOKEN;
import static org.keycloak.OAuth2Constants.TOKEN;
import static org.keycloak.OAuth2Constants.UMA_GRANT_TYPE;
import static org.keycloak.OAuth2Constants.USERNAME;

import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class KeycloakEntities {

   public static final String MATCHING_URI = "permission_resource_matching_uri";
   public static final String PERMISSION_RESOURCE_FORMAT = "permission_resource_format";
   public static final String PERMISSION = "permission";
   public static final String URI_FORMAT = "uri";

   public static Map<String, Object> passwordEntities(String username, String password) {
      return Map.of(GRANT_TYPE, PASSWORD, USERNAME, username, PASSWORD, password);
   }

   public static Map<String, Object> tokenEntities(String clientId, String secret, String redirectUri, String code) {
      return Map.of(GRANT_TYPE, AUTHORIZATION_CODE, CLIENT_ID, clientId, CLIENT_SECRET, secret, CODE, code, REDIRECT_URI, redirectUri);
   }

   public static Map<String, Object> refreshTokenEntities(String clientId, String secret, String refreshToken) {
      return Map.of(GRANT_TYPE, REFRESH_TOKEN, CLIENT_ID, clientId, CLIENT_SECRET, secret, REFRESH_TOKEN, refreshToken);
   }

   public static Map<String, Object> logoutEntities(String clientId, String secret, String refreshToken) {
      return Map.of(CLIENT_ID, clientId, CLIENT_SECRET, secret, REFRESH_TOKEN, refreshToken);
   }

   public static Map<String, Object> authenticationEntities(String clientId, String secret, String token) {
      return Map.of(CLIENT_ID, clientId, CLIENT_SECRET, secret, TOKEN, token);
   }

   public static Map<String, Object> authorizationEntities(String clientId, String uri) {
      return Map.of(GRANT_TYPE, UMA_GRANT_TYPE, AUDIENCE, clientId, PERMISSION_RESOURCE_FORMAT, URI_FORMAT, PERMISSION, uri,
          MATCHING_URI, true);
   }
}

