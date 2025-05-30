package com.sd.KeycloakClient.client.admin.role.sync;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.RoleQueryParams;
import org.keycloak.representations.idm.RoleRepresentation;

public interface KeycloakRoleClient {

   KeycloakResponse<RoleRepresentation[]> getRoles(String accessToken, String clientUuid, RoleQueryParams queryParams);

}
