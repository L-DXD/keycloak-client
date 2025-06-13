package com.sd.KeycloakClient.client.admin.role.async;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.RoleQueryParams;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import reactor.core.publisher.Mono;

public interface KeycloakRoleAsyncClient {

   Mono<KeycloakResponse<RoleRepresentation[]>> getRoles(String accessToken, String clientUuid, RoleQueryParams queryParams);

   Mono<KeycloakResponse<RoleRepresentation[]>> getUserRole(String accessToken, String userId, String clientUuid);

   Mono<KeycloakResponse<UserRepresentation[]>> getUsersByClientRoleName(String accessToken, String roleName, String clientUuid,
       Boolean briefRepresentation, Integer first, Integer max);

   Mono<KeycloakResponse<Void>> grantRole(String accessToken, String userId, String clientUuid, RoleRepresentation[] role);

   Mono<KeycloakResponse<Void>> removeRole(String accessToken, String userId, String clientUuid, RoleRepresentation[] role);
}
