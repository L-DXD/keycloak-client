package com.sd.KeycloakClient.client.admin.user.async.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.sd.KeycloakClient.annotation.MockKeycloakClient;
import com.sd.KeycloakClient.common.KeycloakShareTestContainer;
import com.sd.KeycloakClient.common.TestKeycloakTokenHolder;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.user.UserQueryParams;
import com.sd.KeycloakClient.factory.KeycloakClient;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@MockKeycloakClient
class KeycloakAdminUserAsyncClientImplTest extends KeycloakShareTestContainer {

   private static KeycloakClient keycloakClient;

   private String adminAccessToken;

   @BeforeEach
   void setup() {
      adminAccessToken = TestKeycloakTokenHolder.getAdminAccessToken(keycloakClient);
   }


   @Test
   @DisplayName("case1. success case: get user list - email")
   void getUserList() {
      // given
      UserQueryParams query = UserQueryParams.builder()
          .email("test@example.com")
          .build();
      Mono<KeycloakResponse<UserRepresentation[]>> searchUser = keycloakClient.adminUserAsync().searchUsers(adminAccessToken, query);

      // when & then
      StepVerifier.create(searchUser)
          .assertNext(response -> {
             assertThat(HttpResponseStatus.OK.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isPresent();

             UserRepresentation[] users = response.getBody().get();
             assertThat(users.length).isEqualTo(1);
             assertThat(users[0].getEmail()).isEqualTo("test@example.com");
          })
          .verifyComplete();

   }
}