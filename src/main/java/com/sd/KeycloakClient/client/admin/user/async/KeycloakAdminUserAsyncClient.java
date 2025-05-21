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
}
