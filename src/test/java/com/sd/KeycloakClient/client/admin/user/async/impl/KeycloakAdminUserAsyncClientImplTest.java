package com.sd.KeycloakClient.client.admin.user.async.impl;

import static com.sd.KeycloakClient.constants.TestConstants.EXPIRED_TOKEN;
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
import reactor.core.publisher.Flux;
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
   @DisplayName("case1. success case: get user list - search email")
   void searchByEmail() {
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

   @Test
   @DisplayName("case2. success case: get user list - paging")
   void searchPaging() {
      // given
      UserQueryParams queryMax2 = UserQueryParams.builder()
          .first(0)
          .max(2)
          .build();
      UserQueryParams queryMax4 = UserQueryParams.builder()
          .first(0)
          .max(4)
          .build();
      Flux<KeycloakResponse<UserRepresentation[]>> search = Flux.concat(
          keycloakClient.adminUserAsync().searchUsers(adminAccessToken, queryMax2),
          keycloakClient.adminUserAsync().searchUsers(adminAccessToken, queryMax4));

      // when & then
      StepVerifier.create(search)
          .assertNext(response -> {
             assertThat(HttpResponseStatus.OK.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isPresent();

             UserRepresentation[] users = response.getBody().get();
             assertThat(users.length).isEqualTo(2);
          }).assertNext(response -> {
             assertThat(HttpResponseStatus.OK.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isPresent();

             UserRepresentation[] users = response.getBody().get();
             assertThat(users.length).isEqualTo(4);
          })
          .verifyComplete();

   }

   @Test
   @DisplayName("case3. fail case : invalid token")
   void inValidToken() {
      // given
      UserQueryParams query = UserQueryParams.builder()
          .email("test@example.com")
          .build();
      Mono<KeycloakResponse<UserRepresentation[]>> searchUser = keycloakClient.adminUserAsync().searchUsers(EXPIRED_TOKEN, query);

      // when && then
      StepVerifier.create(searchUser)
          .assertNext(response -> {
             assertThat(HttpResponseStatus.UNAUTHORIZED.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isEmpty();
             assertThat(response.getMessage()).contains("Unauthorized");
          })
          .verifyComplete();
   }
}