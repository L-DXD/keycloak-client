package com.sd.KeycloakClient.client.user.sync;

/**
 * @author SangWonYu
 * @date 2025-04
 * @description Keycloak client for user
 */
public interface KeycloakUserClient {

   /**
    * Sync Get User info
    *
    * @param accessToken
    */
   Object getUserInfo(String accessToken);

}
