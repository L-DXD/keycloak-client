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
   private static final String CLIENTS_PATH = "/clients";
   private static final String ROLES_PATH = "/roles";
   private static final String ROLE_MAPPING_PATH = "/role-mappings";

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

   public String getBaseAdminPath() {
      return ADMIN_PATH + REALM_PATH + "/" + realmName;
   }

   private String attachRelativePath(String path) {
      if (relativePath != null && !relativePath.isEmpty()) {
         return relativePath + path;
      }
      return path;
   }

   private String attachQueryParam(String path, String queryParam) {
      if (queryParam != null && !queryParam.isBlank()) {
         return path + queryParam;
      }
      return path;
   }

   private String getBaseUserPath() {
      return attachRelativePath(getBaseAdminPath() + USER_PATH);
   }

   private String getBaseClientsPath() {
      return attachRelativePath(getBaseAdminPath() + CLIENTS_PATH);
   }

   public String getRoleMappingPath(String userId, String clientUuid) {
      return getBaseUserPath() + "/" + userId + ROLE_MAPPING_PATH + CLIENTS_PATH + "/" + clientUuid;
   }

   public String getClientsRolesPath(String clientUuid) {
      return getBaseClientsPath() + "/" + clientUuid + ROLES_PATH;
   }

   public String getClientSearchUrl(String queryParam) {
      return attachQueryParam(getBaseClientsPath(), queryParam);
   }

   public String getRolesUrl(String clientUuid, String queryParam) {
      return attachQueryParam(getClientsRolesPath(clientUuid), queryParam);
   }

   public String getUserSearchUrl(String queryParam) {
      return attachQueryParam(getBaseUserPath(), queryParam);
   }

   public String getUserUrl(String userId) {
      return getBaseUserPath() + "/" + userId;
   }
}
