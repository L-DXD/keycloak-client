package com.sd.KeycloakClient.client.user.async;

import reactor.core.publisher.Mono;

/**
 * @author SangWonYu
 * @date 2025-04-03
 * @description Keycloak client for user
 */
public interface KeycloakUserAsyncClient {

   /**
    * Async GET USER INFO
    *
    * @param accessToken
    */
   Mono<Object> getUserInfo(String accessToken);

}
