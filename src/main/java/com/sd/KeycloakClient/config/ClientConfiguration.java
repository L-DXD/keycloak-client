package com.sd.KeycloakClient.config;

import java.time.Duration;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ClientConfiguration {

   private String clientId;
   private String redirectUri;
   private String logoutRedirectUri;
   private String responseType;
   private String clientSecret;
   private String realmName;
   private String baseUrl;
   private String relativePath;

   private final Integer MAX_CONNECTIONS = 100;
   private final Integer PENDING_ACQUIRE_MAX_COUNT = 1000;
   private final Duration PENDING_ACQUIRE_TIMEOUT = Duration.ofSeconds(30);

   private static final String TOKEN_PATH = "/token";
   private static final String INTROSPECT_PATH = "/introspect";
   private static final String LOGOUT_PATH = "/logout";
   private static final String USER_INFO_PATH = "/userinfo";
   private static final String CERTS = "/certs";
   private static final String ADMIN_PATH = "/admin";
   private static final String REALM_PATH = "/realms";
   private static final String USER_PATH = "/users";

   private String getOidcUrl() {
      String oidcUrl = "";
      if (Objects.nonNull(relativePath) && !relativePath.isEmpty()) {
         oidcUrl += relativePath;
      }
      oidcUrl += "/realms/" + realmName + "/protocol/openid-connect";
      return oidcUrl;
   }

   public String getTokenUrl() {
      return getOidcUrl() + TOKEN_PATH;
   }

   public String getJwksUrl() {
      return getOidcUrl() + CERTS;
   }

   public String getIntrospectionUrl() {
      return getOidcUrl() + TOKEN_PATH + INTROSPECT_PATH;
   }

   public String getLogoutUrl() {
      return getOidcUrl() + LOGOUT_PATH;
   }

   public String getUserInfoUrl() {
      return getOidcUrl() + USER_INFO_PATH;
   }

   private String buildUserEndpointBasePath() {
      String base = ADMIN_PATH + REALM_PATH + "/" + realmName + USER_PATH;
      if (relativePath != null && !relativePath.isEmpty()) {
         return relativePath + base;
      }
      return base;
   }

   public String getUserSearchUrl(String queryParam) {
      String basePath = buildUserEndpointBasePath();
      if (queryParam != null && !queryParam.isBlank()) {
         return basePath + queryParam;
      }
      return basePath;
   }

   public String getUserUrl(String userId) {
      return buildUserEndpointBasePath() + "/" + userId;
   }

}
