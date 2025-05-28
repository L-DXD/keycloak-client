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
   private String accessToken;

   @BeforeEach
   void setup() {
      adminAccessToken = TestKeycloakTokenHolder.getAdminAccessToken(keycloakClient);
      accessToken = TestKeycloakTokenHolder.getAccessToken(keycloakClient);
   }


   @Test
   @DisplayName("case1. success case: get user list - search email")
   void searchByEmail() {
      // given
      UserQueryParams query = UserQueryParams.builder()
          .email("test2@example.com")
          .build();
      Mono<KeycloakResponse<UserRepresentation[]>> searchUser = keycloakClient.adminUserAsync().searchUsers(adminAccessToken, query);

      // when & then
      StepVerifier.create(searchUser)
          .assertNext(response -> {
             assertThat(HttpResponseStatus.OK.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isPresent();

             UserRepresentation[] users = response.getBody().get();
             assertThat(users.length).isEqualTo(1);
             assertThat(users[0].getEmail()).isEqualTo("test2@example.com");
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

   @Test
   @DisplayName("case4. No admin auth")
   void admin() {
      // given
      String originalEmail = "test@example.com";
      UserQueryParams query = createQueryParams(originalEmail);

      // call search user
      Mono<KeycloakResponse<UserRepresentation[]>> searchUser = keycloakClient.adminUserAsync()
          .searchUsers(accessToken, query);

      // when
      StepVerifier.create(searchUser)
          .assertNext(response -> {
             assertThat(HttpResponseStatus.FORBIDDEN.code()).isEqualTo(response.getStatus());
          });
   }

   @Test
   @DisplayName("case5. change user info")
   void changeUserInfo() {
      // given
      String originalEmail = "test@example.com";
      String changedEmail = "changed@example.com";

      UserQueryParams query = createQueryParams(originalEmail);

      // call search user
      KeycloakResponse<UserRepresentation[]> searchUser = keycloakClient.adminUser()
          .searchUsers(adminAccessToken, query);

      UserRepresentation user = searchUser.getBody().get()[0];
      updateAttribute(user, changedEmail);

      Mono<KeycloakResponse<Void>> updateUserResponse = keycloakClient.adminUserAsync()
          .updateUserInfo(adminAccessToken, user);

      StepVerifier.create(updateUserResponse)
          .assertNext(updateResponse -> {
             assertThat(HttpResponseStatus.NO_CONTENT.code()).isEqualTo(updateResponse.getStatus());
          })
          .verifyComplete();

      UserQueryParams changedEmailQuery = createQueryParams(changedEmail);
      Mono<KeycloakResponse<UserRepresentation[]>> researchUser = keycloakClient.adminUserAsync()
          .searchUsers(adminAccessToken, changedEmailQuery);

      StepVerifier.create(researchUser)
          .assertNext(updateResponse -> {
             assertThat(HttpResponseStatus.OK.code()).isEqualTo(updateResponse.getStatus());
             assertThat(updateResponse.getBody()).isPresent();

             UserRepresentation[] updatedUser = updateResponse.getBody().get();
             assertThat(updatedUser.length).isEqualTo(1);
             assertThat(updatedUser[0].getEmail()).isEqualTo(changedEmail);
          })
          .verifyComplete();

   }

   @Test
   @DisplayName("case6. change read only attribute")
   void changeReadOnlyAttribute() {
      // given
      String originalEmail = "test@example.com";
      UserQueryParams query = createQueryParams(originalEmail);

      // call search user
      KeycloakResponse<UserRepresentation[]> searchUser = keycloakClient.adminUser()
          .searchUsers(adminAccessToken, query);
      UserRepresentation searchedUserInfo = searchUser.getBody().get()[0];

      updateReadOnlyAttribute(searchedUserInfo);
      Mono<KeycloakResponse<Void>> updateResponseMono = keycloakClient.adminUserAsync()
          .updateUserInfo(adminAccessToken, searchedUserInfo);

      StepVerifier.create(updateResponseMono)
          .assertNext(updateResponse -> {
             assertThat(HttpResponseStatus.BAD_REQUEST.code()).isEqualTo(updateResponse.getStatus());
             assertThat(updateResponse.getMessage()).contains("error-user-attribute-read-only");
          })
          .verifyComplete();

   }

   private void updateReadOnlyAttribute(UserRepresentation user) {
      user.setUsername("changedUsername");
   }

   private void updateAttribute(UserRepresentation user, String changedEmail) {
      user.setEnabled(false);
      user.setEmail(changedEmail);
   }

   /**
    * create query param by email
    */
   private UserQueryParams createQueryParams(String email) {
      return UserQueryParams.builder()
          .email(email)
          .build();
   }
}