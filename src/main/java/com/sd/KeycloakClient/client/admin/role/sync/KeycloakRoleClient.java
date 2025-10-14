package com.sd.KeycloakClient.client.admin.role.sync;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.RoleQueryParams;
import java.util.UUID;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

public interface KeycloakRoleClient {

   KeycloakResponse<RoleRepresentation[]> getRoles(String accessToken, UUID clientUuid, RoleQueryParams queryParams);

   KeycloakResponse<RoleRepresentation[]> getUserRole(String accessToken, UUID userId, UUID clientUuid);

   KeycloakResponse<Void> grantRole(String accessToken, UUID userId, UUID clientUuid, RoleRepresentation[] role);

   KeycloakResponse<Void> removeRole(String accessToken, UUID userId, UUID clientUuid, RoleRepresentation[] role);

   KeycloakResponse<UserRepresentation[]> getUsersByClientRoleName(String accessToken, String roleName, UUID clientUuid,
       Boolean briefRepresentation, Integer first, Integer max);
}
