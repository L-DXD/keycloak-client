package com.sd.KeycloakClient.client.user.sync;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.user.KeycloakUserInfo;

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
   KeycloakResponse<KeycloakUserInfo> getUserInfo(String accessToken);

}
