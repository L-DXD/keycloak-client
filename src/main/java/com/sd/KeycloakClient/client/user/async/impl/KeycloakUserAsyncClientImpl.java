package com.sd.KeycloakClient.client.user.async.impl;

import com.sd.KeycloakClient.client.user.async.KeycloakUserAsyncClient;
import com.sd.KeycloakClient.config.ClientConfiguration;
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
   public Mono<Object> getUserInfo(String accessToken) {
      return http.get(configuration.getUserInfoUrl())
          .authorizationBearer(accessToken)
          .send()
          .flatMap((response) -> Mono.just(response.getBody()));
   }

}
