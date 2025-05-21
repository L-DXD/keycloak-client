package com.sd.KeycloakClient.client.user.async.impl;

import com.sd.KeycloakClient.client.user.async.KeycloakUserAsyncClient;
import com.sd.KeycloakClient.config.ClientConfiguration;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.user.KeycloakUserInfo;
import com.sd.KeycloakClient.http.Http;
import reactor.core.publisher.Mono;

public class KeycloakUserAsyncClientImpl implements KeycloakUserAsyncClient {

   private final ClientConfiguration configuration;
   private final Http http;

   public KeycloakUserAsyncClientImpl(ClientConfiguration configuration) {
      this.configuration = configuration;
      this.http = new Http(configuration);
   }

   @Override
   public Mono<KeycloakResponse<KeycloakUserInfo>> getUserInfo(String accessToken) {
      return http.<KeycloakUserInfo>get(configuration.getUserInfoUrl())
          .authorizationBearer(accessToken)
          .responseType(KeycloakUserInfo.class)
          .send();
   }


}
