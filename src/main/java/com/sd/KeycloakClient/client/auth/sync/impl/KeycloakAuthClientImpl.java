package com.sd.KeycloakClient.client.auth.sync.impl;


import com.sd.KeycloakClient.client.auth.async.impl.KeycloakAuthAsyncClientImpl;
import com.sd.KeycloakClient.client.auth.sync.KeycloakAuthClient;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.auth.KeycloakAuthorizationResult;
import com.sd.KeycloakClient.dto.auth.KeycloakIntrospectResponse;
import com.sd.KeycloakClient.dto.auth.KeycloakTokenInfo;
import com.sd.KeycloakClient.dto.auth.VerifyTokenResult;
import java.security.interfaces.RSAPublicKey;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;


/**
 * @author SangWonYu
 * @date 2025-04
 * @description This file is auth client for sync. The actual implementation code exists in the KeycloakAuthAsyncImpl class, and this only
 * provides a simple block() call.
 */

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class KeycloakAuthClientImpl implements KeycloakAuthClient {

   private final KeycloakAuthAsyncClientImpl keycloakAsyncClient;

   @Override
   public KeycloakResponse<KeycloakTokenInfo> basicAuth(String username, String password) {
      return keycloakAsyncClient.basicAuth(username, password).block();
   }


   @Override
   public KeycloakResponse<KeycloakTokenInfo> issueToken(String code) {
      return keycloakAsyncClient.issueToken(code).block();
   }

   @Override
   public KeycloakResponse<Object> logout(String refreshToken) {
      return keycloakAsyncClient.logout(refreshToken).block();
   }

   @Override
   public KeycloakResponse<KeycloakTokenInfo> reissueToken(String refreshToken) {
      return keycloakAsyncClient.reissueToken(refreshToken).block();
   }

   @Override
   public KeycloakResponse<KeycloakIntrospectResponse> authenticationByIntrospect(String token) {
      return keycloakAsyncClient.authenticationByIntrospect(token).block();
   }

   @Override
   public KeycloakResponse<VerifyTokenResult> authenticationByOffline(String token, RSAPublicKey publicKey) {
      return keycloakAsyncClient.authenticationByOffline(token, publicKey).block();
   }

   @Override
   public KeycloakResponse<RSAPublicKey> getPublicKey(String token) {
      return keycloakAsyncClient.getPublicKey(token).block();
   }

   @Override
   public KeycloakResponse<KeycloakAuthorizationResult> authorization(String accessToken, String endpoint, String method) {
      return keycloakAsyncClient.authorization(accessToken, endpoint, method).block();
   }

}
