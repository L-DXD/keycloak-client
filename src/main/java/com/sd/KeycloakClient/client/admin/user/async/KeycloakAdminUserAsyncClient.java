package com.sd.KeycloakClient.client.admin.user.async;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.user.UserQueryParams;
import org.keycloak.representations.idm.UserRepresentation;
import reactor.core.publisher.Mono;

public interface KeycloakAdminUserAsyncClient {

   /**
    * Finds a user by username using the admin API.
    *
    * @param accessToken access token
    * @param params      user query parameters
    * @return Mono of KeycloakResponse containing an array of UserRepresentation objects
    */
   Mono<KeycloakResponse<UserRepresentation[]>> searchUsers(String accessToken, UserQueryParams params);

   /**
    * Updates the user information in Keycloak.
    *
    * @param accessToken        the access token with sufficient privileges to perform the update
    * @param userRepresentation the updated user representation containing new attributes or fields
    */
   Mono<KeycloakResponse<Void>> updateUserInfo(String accessToken, UserRepresentation userRepresentation);

   /**
    * Retrieves a user's information from Keycloak by their unique user ID.
    *
    * @param accessToken the access token with sufficient privileges to access user information
    * @param userId      the unique identifier (UUID) of the user in Keycloak
    * @return a Mono wrapping the KeycloakResponse containing the UserRepresentation if found
    */
   Mono<KeycloakResponse<UserRepresentation>> findByUserId(String accessToken, String userId);
}
