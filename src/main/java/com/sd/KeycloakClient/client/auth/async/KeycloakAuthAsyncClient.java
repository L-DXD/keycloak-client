package com.sd.KeycloakClient.client.auth.async;


import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.auth.KeycloakAuthorizationResult;
import com.sd.KeycloakClient.dto.auth.KeycloakIntrospectResponse;
import com.sd.KeycloakClient.dto.auth.KeycloakTokenInfo;
import com.sd.KeycloakClient.dto.auth.VerifyTokenResult;
import java.security.interfaces.RSAPublicKey;
import reactor.core.publisher.Mono;

/**
 * @author SangWonYu
 * @date 2025-04-03
 * @description Keycloak client for async auth
 */
public interface KeycloakAuthAsyncClient {

   /**
    * Async Basic Auth
    *
    * @param username
    * @param password
    */
   Mono<KeycloakResponse<KeycloakTokenInfo>> basicAuth(String username, String password);

   /**
    * Async Issue Token
    *
    * @param code
    */
   Mono<KeycloakResponse<KeycloakTokenInfo>> issueToken(String code);

   /**
    * Async Logout
    *
    * @param refreshToken
    * @return
    */
   Mono<KeycloakResponse<Object>> logout(String refreshToken);

   /**
    * Async Reissue Token
    *
    * @param refreshToken
    * @return
    */
   Mono<KeycloakResponse<KeycloakTokenInfo>> reissueToken(String refreshToken);

   /**
    * Async Online Authenticate
    *
    * @param token
    */
   Mono<KeycloakResponse<KeycloakIntrospectResponse>> authenticationByIntrospect(String token);

   /**
    * Async Offline Authenticate
    *
    * @param token
    */
   Mono<KeycloakResponse<VerifyTokenResult>> authenticationByOffline(String token, RSAPublicKey publicKey);

   /**
    * Async Authorize
    *
    * @param accessToken
    * @param endpoint
    * @param method
    */
   Mono<KeycloakResponse<KeycloakAuthorizationResult>> authorization(String accessToken, String endpoint, String method);

   /**
    * Async GET Public key
    *
    * @param token
    * @return
    */
   Mono<KeycloakResponse<RSAPublicKey>> getPublicKey(String token);
}
