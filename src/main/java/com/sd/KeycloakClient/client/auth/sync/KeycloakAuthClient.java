package com.sd.KeycloakClient.client.auth.sync;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.auth.KeycloakAuthorizationResult;
import com.sd.KeycloakClient.dto.auth.KeycloakIntrospectResponse;
import com.sd.KeycloakClient.dto.auth.KeycloakTokenInfo;
import com.sd.KeycloakClient.dto.auth.VerifyTokenResult;
import java.security.interfaces.RSAPublicKey;

/**
 * @author SangWonYu
 * @date 2025-04-03
 * @description Keycloak client for sync auth
 */

public interface KeycloakAuthClient {

   /**
    * Sync Basic Auth
    *
    * @param username
    * @param password
    */
   KeycloakResponse<KeycloakTokenInfo> basicAuth(String username, String password);

   /**
    * Sync ISSUE Token
    *
    * @param code
    */
   KeycloakResponse<KeycloakTokenInfo> issueToken(String code);

   /**
    * Sync Logout
    *
    * @param idToken
    * @return
    */
   KeycloakResponse<Object> logout(String idToken);

   /**
    * Sync Reissue Token
    *
    * @param refreshToken
    * @return
    */
   KeycloakResponse<KeycloakTokenInfo> reissueToken(String refreshToken);

   /**
    * Sync Online authenticate
    *
    * @param token
    */
   KeycloakResponse<KeycloakIntrospectResponse> authenticationByIntrospect(String token);

   /**
    * Sync Offline authenticate
    *
    * @param token
    */
   KeycloakResponse<VerifyTokenResult> authenticationByOffline(String token, RSAPublicKey publicKey);

   /**
    * Sync Authorize
    *
    * @param accessToken
    * @param endpoint
    * @param method
    */
   KeycloakResponse<KeycloakAuthorizationResult> authorization(String accessToken, String endpoint, String method);

   /**
    * Sync GET Public key
    *
    * @param token
    * @return
    */
   KeycloakResponse<RSAPublicKey> getPublicKey(String token);

}
