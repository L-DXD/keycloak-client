package com.sd.KeycloakClient.client.admin.auth.sync;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.ResourceQueryParams;
import java.util.UUID;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;

/**
 * @author LeeBongSeung
 * @date: 2025-08-28
 * @description: Keycloak client for sync auth resource
 */
public interface KeycloakAuthResourceClient {

   /**
    * Retrieve a specific authorization resource by its ID.
    *
    * @param accessToken the admin or service account access token
    * @param clientUuid  the unique identifier (UUID) of the Keycloak client
    * @param resourceId  the unique identifier of the resource to retrieve
    * @return a {@link KeycloakResponse} containing the {@link ResourceRepresentation} if found
    */
   KeycloakResponse<ResourceRepresentation> getResource(String accessToken, UUID clientUuid, UUID resourceId);

   /**
    * Retrieve a list of authorization resources, optionally filtered by query parameters.
    *
    * @param accessToken the admin or service account access token
    * @param clientUuid  the unique identifier (UUID) of the Keycloak client
    * @param queryParams optional filtering and pagination parameters
    * @return a {@link KeycloakResponse} containing an array of {@link ResourceRepresentation} objects
    */
   KeycloakResponse<ResourceRepresentation[]> getResources(String accessToken, UUID clientUuid, ResourceQueryParams queryParams);

   /**
    * Create a new authorization resource for the given client.
    *
    * @param accessToken            the admin or service account access token
    * @param clientUuid             the unique identifier (UUID) of the Keycloak client
    * @param resourceRepresentation the resource definition to be created
    * @return a {@link KeycloakResponse} with no body (success or failure status only)
    */
   KeycloakResponse<Void> createResource(String accessToken, UUID clientUuid, ResourceRepresentation resourceRepresentation);

   /**
    * Update an existing authorization resource.
    *
    * @param accessToken            the admin or service account access token
    * @param clientUuid             the unique identifier (UUID) of the Keycloak client
    * @param resourceRepresentation the updated resource definition (must include the ID)
    * @return a {@link KeycloakResponse} with no body (success or failure status only)
    */
   KeycloakResponse<Void> updateResource(String accessToken, UUID clientUuid, ResourceRepresentation resourceRepresentation);

   /**
    * Delete an authorization resource by its ID.
    *
    * @param accessToken the admin or service account access token
    * @param clientUuid  the unique identifier (UUID) of the Keycloak client
    * @param resourceId  the unique identifier of the resource to delete
    * @return a {@link KeycloakResponse} with no body (success or failure status only)
    */
   KeycloakResponse<Void> deleteResource(String accessToken, UUID clientUuid, UUID resourceId);
}
