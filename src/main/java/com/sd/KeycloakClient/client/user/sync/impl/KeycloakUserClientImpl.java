package com.sd.KeycloakClient.client.user.sync.impl;

import com.sd.KeycloakClient.client.user.async.impl.KeycloakUserAsyncClientImpl;
import com.sd.KeycloakClient.client.user.sync.KeycloakUserClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KeycloakUserClientImpl implements KeycloakUserClient {

   private final KeycloakUserAsyncClientImpl keycloakUserAsyncClient;

   @Override
   public Object getUserInfo(String accessToken) {
      return keycloakUserAsyncClient.getUserInfo(accessToken).block();
   }

}
