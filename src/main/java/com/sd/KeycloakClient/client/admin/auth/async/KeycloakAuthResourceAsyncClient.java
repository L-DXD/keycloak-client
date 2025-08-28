package com.sd.KeycloakClient.client.admin.auth.async;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.ResourceQueryParams;
import java.util.UUID;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import reactor.core.publisher.Mono;

/**
 * @author: LeeBongSeung
 * @date: 2025-08-28
 * @description: Keycloak client for async auth resource
 */
public interface KeycloakAuthResourceAsyncClient {

   /**
    * Get a single resource by ID.
    *
    * @param accessToken access token
    * @param clientUuid  keycloak client uuid
    * @param resourceId  resource id (uuid)
    */
   Mono<KeycloakResponse<ResourceRepresentation>> getResource(String accessToken, UUID clientUuid, UUID resourceId);

   /**
    * Get resources with optional filters/pagination.
    *
    * @param accessToken access token
    * @param clientUuid  keycloak client uuid
    * @param queryParams optional filters (first, max, name, deep, exactName, etc.)
    */
   Mono<KeycloakResponse<ResourceRepresentation[]>> getResources(String accessToken, UUID clientUuid, ResourceQueryParams queryParams);

   /**
    * Create a resource.
    *
    * @param accessToken            access token
    * @param clientUuid             keycloak client uuid
    * @param resourceRepresentation resource payload
    */
   Mono<KeycloakResponse<Void>> createResource(String accessToken, UUID clientUuid, ResourceRepresentation resourceRepresentation);

   /**
    * Update a resource (payload must include id).
    *
    * @param accessToken            access token
    * @param clientUuid             keycloak client uuid
    * @param resourceRepresentation resource payload (with id)
    */
   Mono<KeycloakResponse<Void>> updateResource(String accessToken, UUID clientUuid, ResourceRepresentation resourceRepresentation);

   /**
    * Delete a resource by ID.
    *
    * @param accessToken access token
    * @param clientUuid  keycloak client uuid
    * @param resourceId  resource id (uuid)
    */
   Mono<KeycloakResponse<Void>> deleteResource(String accessToken, UUID clientUuid, UUID resourceId);
}
