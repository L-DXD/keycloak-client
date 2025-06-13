package com.sd.KeycloakClient.client.admin.role.sync;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.RoleQueryParams;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

public interface KeycloakRoleClient {

   KeycloakResponse<RoleRepresentation[]> getRoles(String accessToken, String clientUuid, RoleQueryParams queryParams);

   KeycloakResponse<RoleRepresentation[]> getUserRole(String accessToken, String userId, String clientUuid);

   KeycloakResponse<Void> grantRole(String accessToken, String userId, String clientUuid, RoleRepresentation[] role);

   KeycloakResponse<Void> removeRole(String accessToken, String userId, String clientUuid, RoleRepresentation[] role);

   KeycloakResponse<UserRepresentation[]> getUsersByClientRoleName(String accessToken, String roleName, String clientUuid,
       Boolean briefRepresentation, Integer first, Integer max);
}
