package com.sd.KeycloakClient.client.admin.user.async;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.user.UserQueryParams;
import org.keycloak.representations.idm.CredentialRepresentation;
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
    * It can be called in three different ways. 1. Don’t specify any criteria and pass {@code null}. The number of all users within that
    * realm will be returned. <p> 2. If {@code search} is specified other criteria such as {@code last} will be ignored even though you set
    * them. The {@code search} string will be matched against the first and last name, the username and the email of a user. <p> 3. If
    * {@code search} is unspecified but any of {@code last}, {@code first}, {@code email} or {@code username} those criteria are matched
    * against their respective fields on a user entity. Combined with a logical and.
    */
   Mono<KeycloakResponse<Integer>> getUsersCount(String accessToken, UserQueryParams params);


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

   /**
    * Creates a new user in the Keycloak realm using the provided access token and user information.
    *
    * @param accessToken        A valid admin access token with permissions to create users.
    * @param userRepresentation The representation of the user to be created, including username, email, credentials (password), and
    *                           optional attributes.
    * @return A {@link Mono} emitting the {@link KeycloakResponse} object containing the HTTP response status and message. The body is
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
   Mono<KeycloakResponse<Void>> createUser(String accessToken, UserRepresentation userRepresentation);

   /**
    * Set up a new password for the user.
    */
   Mono<KeycloakResponse<Void>> resetPassword(String accessToken, String userId, CredentialRepresentation credentialRepresentation);
}
