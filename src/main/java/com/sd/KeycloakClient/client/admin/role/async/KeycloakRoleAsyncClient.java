package com.sd.KeycloakClient.client.admin.role.async;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.RoleQueryParams;
import org.keycloak.representations.idm.RoleRepresentation;
import reactor.core.publisher.Mono;

public interface KeycloakRoleAsyncClient {

   Mono<KeycloakResponse<RoleRepresentation[]>> getRoles(String accessToken, String clientUuid, RoleQueryParams queryParams);
}
