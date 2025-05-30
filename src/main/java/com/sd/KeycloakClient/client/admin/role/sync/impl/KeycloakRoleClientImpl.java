package com.sd.KeycloakClient.client.admin.role.sync.impl;

import com.sd.KeycloakClient.client.admin.role.async.KeycloakRoleAsyncClient;
import com.sd.KeycloakClient.client.admin.role.sync.KeycloakRoleClient;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.RoleQueryParams;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.RoleRepresentation;

@RequiredArgsConstructor
public class KeycloakRoleClientImpl implements KeycloakRoleClient {

   private final KeycloakRoleAsyncClient keycloakRoleClient;

   @Override
   public KeycloakResponse<RoleRepresentation[]> getRoles(String accessToken, String clientUuid, RoleQueryParams queryParams) {
      return keycloakRoleClient.getRoles(accessToken, clientUuid, queryParams).block();
   }
}
