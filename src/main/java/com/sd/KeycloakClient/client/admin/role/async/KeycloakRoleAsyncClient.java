package com.sd.KeycloakClient.client.admin.role.async;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.RoleQueryParams;
import org.keycloak.representations.idm.RoleRepresentation;
import reactor.core.publisher.Mono;

public interface KeycloakRoleAsyncClient {

   Mono<KeycloakResponse<RoleRepresentation[]>> getRoles(String accessToken, String clientUuid, RoleQueryParams queryParams);

   Mono<KeycloakResponse<RoleRepresentation[]>> getUserRole(String accessToken, String userId, String clientUuid);

   Mono<KeycloakResponse<Void>> grantRole(String accessToken, String userId, String clientUuid, RoleRepresentation[] role);
}
