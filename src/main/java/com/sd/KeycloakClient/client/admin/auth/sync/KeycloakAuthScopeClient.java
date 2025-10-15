package com.sd.KeycloakClient.client.admin.auth.sync;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.ScopeQueryParams;
import java.util.UUID;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;

/**
 * @author LeeBongSeung
 * @date: 2025-08-26
 * @description: Keycloak client for sync auth scope
 */
public interface KeycloakAuthScopeClient {

   /**
    * Retrieve a specific authorization scope by its ID.
    *
    * @param accessToken the admin or service account access token
    * @param clientUuid  the unique identifier (UUID) of the Keycloak client
    * @param scopeId     the unique identifier of the scope to retrieve
    * @return a {@link KeycloakResponse} containing the {@link ScopeRepresentation} if found
    */
   KeycloakResponse<ScopeRepresentation> getScope(String accessToken, UUID clientUuid, UUID scopeId);

   /**
    * Retrieve a list of authorization scopes, optionally filtered by query parameters.
    *
    * @param accessToken      the admin or service account access token
    * @param clientUuid       the unique identifier (UUID) of the Keycloak client
    * @param scopeQueryParams optional filtering and pagination parameters
    * @return a {@link KeycloakResponse} containing an array of {@link ScopeRepresentation} objects
    */
   KeycloakResponse<ScopeRepresentation[]> getScopes(String accessToken, UUID clientUuid, ScopeQueryParams scopeQueryParams);

   /**
    * Create a new authorization scope for the given client.
    *
    * @param accessToken         the admin or service account access token
    * @param clientUuid          the unique identifier (UUID) of the Keycloak client
    * @param scopeRepresentation the scope definition to be created
    * @return a {@link KeycloakResponse} with no body (success or failure status only)
    */
   KeycloakResponse<Void> createScope(String accessToken, UUID clientUuid, ScopeRepresentation scopeRepresentation);

   /**
    * Update an existing authorization scope.
    *
    * @param accessToken         the admin or service account access token
    * @param clientUuid          the unique identifier (UUID) of the Keycloak client
    * @param scopeRepresentation the updated scope definition (must include the ID)
    * @return a {@link KeycloakResponse} with no body (success or failure status only)
    */
   KeycloakResponse<Void> updateScope(String accessToken, UUID clientUuid, ScopeRepresentation scopeRepresentation);

   /**
    * Delete an authorization scope by its ID.
    *
    * @param accessToken the admin or service account access token
    * @param clientUuid  the unique identifier (UUID) of the Keycloak client
    * @param scopeId     the unique identifier of the scope to delete
    * @return a {@link KeycloakResponse} with no body (success or failure status only)
    */
   KeycloakResponse<Void> deleteScope(String accessToken, UUID clientUuid, UUID scopeId);
}
