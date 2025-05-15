package com.sd.KeycloakClient.client.user.sync.impl;

import com.sd.KeycloakClient.client.user.async.impl.KeycloakUserAsyncClientImpl;
import com.sd.KeycloakClient.client.user.sync.KeycloakUserClient;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.user.KeycloakUserInfo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KeycloakUserClientImpl implements KeycloakUserClient {

   private final KeycloakUserAsyncClientImpl keycloakUserAsyncClient;

   @Override
   public KeycloakResponse<KeycloakUserInfo> getUserInfo(String accessToken) {
      return keycloakUserAsyncClient.getUserInfo(accessToken).block();
   }

}
