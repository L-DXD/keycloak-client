package com.sd.KeycloakClient.client.admin.role.sync.impl;

import com.sd.KeycloakClient.client.admin.role.async.KeycloakRoleAsyncClient;
import com.sd.KeycloakClient.client.admin.role.sync.KeycloakRoleClient;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.RoleQueryParams;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

@RequiredArgsConstructor
public class KeycloakRoleClientImpl implements KeycloakRoleClient {

   private final KeycloakRoleAsyncClient keycloakRoleClient;

   @Override
   public KeycloakResponse<RoleRepresentation[]> getRoles(String accessToken, UUID clientUuid, RoleQueryParams queryParams) {
      return keycloakRoleClient.getRoles(accessToken, clientUuid, queryParams).block();
   }

   @Override
   public KeycloakResponse<RoleRepresentation[]> getUserRole(String accessToken, UUID userId, UUID clientUuid) {
      return keycloakRoleClient.getUserRole(accessToken, userId, clientUuid).block();
   }

   @Override
   public KeycloakResponse<Void> grantRole(String accessToken, UUID userId, UUID clientUuid, RoleRepresentation[] role) {
      return keycloakRoleClient.grantRole(accessToken, userId, clientUuid, role).block();
   }

   @Override
   public KeycloakResponse<Void> removeRole(String accessToken, UUID userId, UUID clientUuid, RoleRepresentation[] role) {
      return keycloakRoleClient.removeRole(accessToken, userId, clientUuid, role).block();
   }

   @Override
   public KeycloakResponse<UserRepresentation[]> getUsersByClientRoleName(String accessToken, String roleName, UUID clientUuid,
       Boolean briefRepresentation, Integer first, Integer max) {
      return keycloakRoleClient.getUsersByClientRoleName(accessToken, roleName, clientUuid, briefRepresentation, first, max).block();
   }
}
