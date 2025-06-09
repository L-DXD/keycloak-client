package com.sd.KeycloakClient.client.admin.user.sync;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.user.UserQueryParams;
import org.keycloak.representations.idm.UserRepresentation;

public interface KeycloakAdminUserClient {


   /**
    * Finds a user by username using the admin API.
    *
    * @param accessToken access token
    * @param params      user query parameters
    * @return Mono of KeycloakResponse containing an array of UserRepresentation objects
    */
   KeycloakResponse<UserRepresentation[]> searchUsers(String accessToken, UserQueryParams params);

   /**
    * Updates the user information in Keycloak.
    *
    * @param accessToken        the access token with sufficient privileges to perform the update
    * @param userRepresentation the updated user representation containing new attributes or fields
    */
   KeycloakResponse<Void> updateUserInfo(String accessToken, UserRepresentation userRepresentation);

   /**
    * Retrieves a user's information from Keycloak by their unique user ID.
    *
    * @param accessToken the access token with sufficient privileges to access user information
    * @param userId      the unique identifier (UUID) of the user in Keycloak
    */
   KeycloakResponse<UserRepresentation> findByUserId(String accessToken, String userId);

   /**
    * Creates a new user in the Keycloak realm using the provided access token and user information.
    *
    * @param accessToken        A valid admin access token with permissions to create users.
    * @param userRepresentation The representation of the user to be created, including username, email, credentials (password), and
    *                           optional attributes.
    * @return A {@link } emitting the {@link KeycloakResponse} object containing the HTTP response status and message. The body is
    * {@code null} since the creation API does not return a payload.
    *
    * <p>Typical HTTP response codes:
    * <ul>
    *   <li>201 Created – User was successfully created.</li>
    *   <li>409 Conflict – A user with the same username or email already exists.</li>
    *   <li>401 Unauthorized – Invalid or missing access token.</li>
    *   <li>403 Forbidden – Insufficient permissions to create a user.</li>
    * </ul>
    * @see org.keycloak.representations.idm.UserRepresentation
    */
   KeycloakResponse<Void> createUser(String accessToken, UserRepresentation userRepresentation);
}
