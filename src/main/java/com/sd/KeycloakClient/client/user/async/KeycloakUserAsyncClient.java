package com.sd.KeycloakClient.client.user.async;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.user.KeycloakUserInfo;
import reactor.core.publisher.Mono;

public interface KeycloakUserAsyncClient {

   /**
    * Async get user info
    *
    * @param accessToken access token
    */
   Mono<KeycloakResponse<KeycloakUserInfo>> getUserInfo(String accessToken);


}
