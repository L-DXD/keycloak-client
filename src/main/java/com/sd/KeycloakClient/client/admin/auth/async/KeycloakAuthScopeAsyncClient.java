package com.sd.KeycloakClient.client.admin.auth.async;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.ScopeQueryParams;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import reactor.core.publisher.Mono;

/**
 * @author LeeBongSeung
 * @date: 2025-08-26
 * @description: Keycloak client for async auth scope
 */
public interface KeycloakAuthScopeAsyncClient {

   /**
    * Retrieve a specific authorization scope by its ID.
    *
    * @param accessToken the admin or service account access token
    * @param clientUuid  the unique identifier (UUID) of the Keycloak client
    * @param scopeId     the unique identifier of the scope to retrieve
    * @return a {@link Mono} emitting a {@link KeycloakResponse} containing the {@link ScopeRepresentation} if found
    */
   Mono<KeycloakResponse<ScopeRepresentation>> getScope(String accessToken, String clientUuid, String scopeId);

   /**
    * Retrieve a list of authorization scopes, optionally filtered by query parameters.
    *
    * @param accessToken      the admin or service account access token
    * @param clientUuid       the unique identifier (UUID) of the Keycloak client
    * @param scopeQueryParams optional filtering and pagination parameters
    * @return a {@link Mono} emitting a {@link KeycloakResponse} containing an array of {@link ScopeRepresentation} objects
    */
   Mono<KeycloakResponse<ScopeRepresentation[]>> getScopes(String accessToken, String clientUuid, ScopeQueryParams scopeQueryParams);

   /**
    * @param accessToken         access token
    * @param clientUuid          keycloak client uuid
    * @param scopeRepresentation scope representation
    * @return
    */
   Mono<KeycloakResponse<Void>> createScope(String accessToken, String clientUuid, ScopeRepresentation scopeRepresentation);

   /**
    * Update an existing authorization scope.
    *
    * @param accessToken         the admin or service account access token
    * @param clientUuid          the unique identifier (UUID) of the Keycloak client
    * @param scopeRepresentation the updated scope definition (must include the ID)
    * @return a {@link Mono} emitting a {@link KeycloakResponse} with no body (success or failure status only)
    */
   Mono<KeycloakResponse<Void>> updateScope(String accessToken, String clientUuid, ScopeRepresentation scopeRepresentation);

   /**
    * Delete an authorization scope by its ID.
    *
    * @param accessToken the admin or service account access token
    * @param clientUuid  the unique identifier (UUID) of the Keycloak client
    * @param scopeId     the unique identifier of the scope to delete
    * @return a {@link Mono} emitting a {@link KeycloakResponse} with no body (success or failure status only)
    */
   Mono<KeycloakResponse<Void>> deleteScope(String accessToken, String clientUuid, String scopeId);
}
