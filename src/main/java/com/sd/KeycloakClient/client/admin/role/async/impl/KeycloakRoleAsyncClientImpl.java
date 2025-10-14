package com.sd.KeycloakClient.client.admin.role.async.impl;

import com.sd.KeycloakClient.client.admin.role.async.KeycloakRoleAsyncClient;
import com.sd.KeycloakClient.config.ClientConfiguration;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.RoleQueryParams;
import com.sd.KeycloakClient.http.Http;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import reactor.core.publisher.Mono;

public class KeycloakRoleAsyncClientImpl implements KeycloakRoleAsyncClient {

   private final ClientConfiguration configuration;
   private final Http http;

   public KeycloakRoleAsyncClientImpl(ClientConfiguration configuration) {
      this.configuration = configuration;
      this.http = new Http(configuration);
   }

   @Override
   public Mono<KeycloakResponse<RoleRepresentation[]>> getRoles(String accessToken, UUID clientUuid, RoleQueryParams queryParams) {
      String rolesUrl = configuration.getRolesUrl(clientUuid, queryParams.toQueryString());
      return http.<RoleRepresentation[]>get(rolesUrl)
          .applicationJson()
          .authorizationBearer(accessToken)
          .responseType(RoleRepresentation[].class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<RoleRepresentation[]>> getUserRole(String accessToken, UUID userId, UUID clientUuid) {
      String rolesUrl = configuration.getRoleMappingPath(userId, clientUuid);
      return http.<RoleRepresentation[]>get(rolesUrl)
          .applicationJson()
          .authorizationBearer(accessToken)
          .responseType(RoleRepresentation[].class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<UserRepresentation[]>> getUsersByClientRoleName(String accessToken, String roleName,
       UUID clientUuid, Boolean briefRepresentation, Integer first, Integer max) {
      String clientsUsers = configuration.getClientsRolesUsersPath(clientUuid, roleName, briefRepresentation, first, max);

      return http.<UserRepresentation[]>get(clientsUsers)
          .applicationJson()
          .authorizationBearer(accessToken)
          .responseType(UserRepresentation[].class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<Void>> grantRole(String accessToken, UUID userId, UUID clientUuid,
       RoleRepresentation[] roles) {
      String roleMappingPath = configuration.getRoleMappingPath(userId, clientUuid);
      boolean notFoundAttribute = Arrays.stream(roles).anyMatch(role -> Objects.isNull(role.getName()) || Objects.isNull(role.getId()));
      if (notFoundAttribute) {
         return Mono.just(KeycloakResponse.of(HttpResponseStatus.BAD_REQUEST.code(), "Role id or role name is required", null, null));
      }
      return http.<Void>post(roleMappingPath)
          .applicationJson()
          .entities(roles)
          .authorizationBearer(accessToken)
          .responseType(Void.class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<Void>> removeRole(String accessToken, UUID userId, UUID clientUuid,
       RoleRepresentation[] roles) {
      String roleMappingPath = configuration.getRoleMappingPath(userId, clientUuid);
      boolean notFoundAttribute = Arrays.stream(roles).anyMatch(role -> Objects.isNull(role.getName()) || Objects.isNull(role.getId()));
      if (notFoundAttribute) {
         return Mono.just(KeycloakResponse.of(HttpResponseStatus.BAD_REQUEST.code(), "Role id or role name is required", null, null));
      }
      return http.<Void>delete(roleMappingPath)
          .applicationJson()
          .entities(roles)
          .authorizationBearer(accessToken)
          .responseType(Void.class)
          .send();
   }
}
