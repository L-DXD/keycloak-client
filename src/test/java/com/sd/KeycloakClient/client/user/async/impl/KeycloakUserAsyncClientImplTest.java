package com.sd.KeycloakClient.client.user.async.impl;

import static com.sd.KeycloakClient.constants.TestConstants.EXPIRED_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;

import com.sd.KeycloakClient.annotation.MockKeycloakClient;
import com.sd.KeycloakClient.common.KeycloakShareTestContainer;
import com.sd.KeycloakClient.common.TestKeycloakTokenHolder;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.user.KeycloakUserInfo;
import com.sd.KeycloakClient.factory.KeycloakClient;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@MockKeycloakClient
class KeycloakUserAsyncClientImplTest extends KeycloakShareTestContainer {

   private static KeycloakClient keycloakClient;

   private String accessToken;

   @BeforeEach
   void setup() {
      accessToken = TestKeycloakTokenHolder.getAccessToken(keycloakClient);
   }

   @Test
   @DisplayName("case1. success case : get userinfo")
   void getUserInfo() {
      // given
      Mono<KeycloakResponse<KeycloakUserInfo>> userInfo = keycloakClient.userAsync().getUserInfo(accessToken);

      // when && then
      StepVerifier.create(userInfo)
          .assertNext(response -> {
             assertThat(HttpResponseStatus.OK.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isPresent();

             KeycloakUserInfo keycloakUserInfo = response.getBody().get();
             assertThat(keycloakUserInfo.getEmail()).isEqualTo("test2@example.com");
             assertThat(keycloakUserInfo.getName()).isEqualTo("Test User");
             assertThat(keycloakUserInfo.getPreferredUsername()).isEqualTo("test-user-keycloak2");
             assertThat(keycloakUserInfo.getOtherInfo().get("given_name")).isEqualTo("Test");
          })
          .verifyComplete();

   }

   @Test
   @DisplayName("case2. fail case : invalid token")
   void inValidToken() {
      // given
      Mono<KeycloakResponse<KeycloakUserInfo>> userInfo = keycloakClient.userAsync().getUserInfo("invalid accessToken");

      // when && then
      StepVerifier.create(userInfo)
          .assertNext(response -> {
             assertThat(HttpResponseStatus.UNAUTHORIZED.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isEmpty();
             assertThat(response.getMessage()).contains("Unauthorized");
          })
          .verifyComplete();
   }

   @Test
   @DisplayName("case3. fail case : expired token")
   void expiredToken() {
      // given
      Mono<KeycloakResponse<KeycloakUserInfo>> userInfo = keycloakClient.userAsync().getUserInfo(EXPIRED_TOKEN);

      // when && then
      StepVerifier.create(userInfo)
          .assertNext(response -> {
             assertThat(HttpResponseStatus.UNAUTHORIZED.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isEmpty();
             assertThat(response.getMessage()).contains("Unauthorized");
          })
          .verifyComplete();
   }
}