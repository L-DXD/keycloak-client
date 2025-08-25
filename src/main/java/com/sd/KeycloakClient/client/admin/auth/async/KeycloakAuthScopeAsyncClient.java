package com.sd.KeycloakClient.client.admin.auth.async;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.ScopeQueryParams;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import reactor.core.publisher.Mono;

public interface KeycloakAuthScopeAsyncClient {

   Mono<KeycloakResponse<ScopeRepresentation>> getScope(String accessToken, String clientUuid, String scopeId);

   Mono<KeycloakResponse<ScopeRepresentation[]>> getScopes(String accessToken, String clientUuid, ScopeQueryParams scopeQueryParams);

   Mono<KeycloakResponse<Void>> createScope(String accessToken, String clientUuid, ScopeRepresentation scopeRepresentation);

   Mono<KeycloakResponse<Void>> updateScope(String accessToken, String clientUuid, ScopeRepresentation scopeRepresentation);

   Mono<KeycloakResponse<Void>> deleteScope(String accessToken, String clientUuid, String scopeId);
}
