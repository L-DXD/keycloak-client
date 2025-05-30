package com.sd.KeycloakClient.client.admin.role.async.impl;

import com.sd.KeycloakClient.client.admin.role.async.KeycloakRoleAsyncClient;
import com.sd.KeycloakClient.config.ClientConfiguration;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.RoleQueryParams;
import com.sd.KeycloakClient.http.Http;
import org.keycloak.representations.idm.RoleRepresentation;
import reactor.core.publisher.Mono;

public class KeycloakRoleAsyncClientImpl implements KeycloakRoleAsyncClient {

   private final ClientConfiguration configuration;
   private final Http http;

   public KeycloakRoleAsyncClientImpl(ClientConfiguration configuration) {
      this.configuration = configuration;
      this.http = new Http(configuration);
   }

   @Override
   public Mono<KeycloakResponse<RoleRepresentation[]>> getRoles(String accessToken, String clientUuid, RoleQueryParams queryParams) {
      String rolesUrl = configuration.getRolesUrl(clientUuid, queryParams.toQueryString());
      return http.<RoleRepresentation[]>get(rolesUrl)
          .applicationJson()
          .authorizationBearer(accessToken)
          .responseType(RoleRepresentation[].class)
          .send();
   }
}
