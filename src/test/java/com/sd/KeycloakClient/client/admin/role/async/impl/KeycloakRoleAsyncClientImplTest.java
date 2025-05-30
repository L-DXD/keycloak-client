package com.sd.KeycloakClient.client.admin.role.async.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.sd.KeycloakClient.annotation.MockKeycloakClient;
import com.sd.KeycloakClient.common.KeycloakShareTestContainer;
import com.sd.KeycloakClient.common.TestKeycloakTokenHolder;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.RoleQueryParams;
import com.sd.KeycloakClient.factory.KeycloakClient;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.RoleRepresentation;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@MockKeycloakClient
class KeycloakRoleAsyncClientImplTest extends KeycloakShareTestContainer {

   private static KeycloakClient keycloakClient;
   private static final String TEST_CLIENT_UUID = "de5fe303-2e50-428e-ac72-b81d1dc5139a";
   private static final String TEST_INVALID_CLIENT_UUID = "aaaaaa-2e50-428e-ac72-b81d1dc5139a";

   private String adminAccessToken;

   @BeforeEach
   void setup() {
      adminAccessToken = TestKeycloakTokenHolder.getAdminAccessToken(keycloakClient);
   }

   @AfterAll
   static void afterAll() {
      TestKeycloakTokenHolder.removeAdminAccessToken();
   }

   @Test
   @DisplayName("case1. success get roles")
   void getRoles() {
      // given
      RoleQueryParams params = RoleQueryParams.builder()
          .briefRepresentation(false)
          .build();
      Mono<KeycloakResponse<RoleRepresentation[]>> rolesResponse = keycloakClient.roleAsync()
          .getRoles(adminAccessToken, TEST_CLIENT_UUID, params);

      // when && then
      StepVerifier.create(rolesResponse)
          .assertNext((response) -> {
             assertThat(HttpResponseStatus.OK.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isPresent();

             RoleRepresentation[] roles = response.getBody().get();
             assertThat(roles.length).isGreaterThan(1);
             assertThat(roles[1].getName()).isEqualTo("PRM");
          })
          .verifyComplete();
   }

   @Test
   @DisplayName("case2. fail invalid client id")
   void invalidClientId() {
      // given
      RoleQueryParams params = RoleQueryParams.builder()
          .briefRepresentation(false)
          .build();
      Mono<KeycloakResponse<RoleRepresentation[]>> rolesResponse = keycloakClient.roleAsync()
          .getRoles(adminAccessToken, TEST_INVALID_CLIENT_UUID, params);

      // when && then
      StepVerifier.create(rolesResponse)
          .assertNext((response) -> {
             assertThat(HttpResponseStatus.NOT_FOUND.code()).isEqualTo(response.getStatus());
          })
          .verifyComplete();
   }
}