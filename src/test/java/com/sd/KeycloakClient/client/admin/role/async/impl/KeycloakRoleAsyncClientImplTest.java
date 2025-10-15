package com.sd.KeycloakClient.client.admin.role.async.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.sd.KeycloakClient.annotation.MockKeycloakClient;
import com.sd.KeycloakClient.common.KeycloakShareTestContainer;
import com.sd.KeycloakClient.common.TestKeycloakTokenHolder;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.RoleQueryParams;
import com.sd.KeycloakClient.factory.KeycloakClient;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.Arrays;
import java.util.UUID;
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
   private static final UUID TEST_CLIENT_UUID = UUID.fromString("de5fe303-2e50-428e-ac72-b81d1dc5139a");
   private static final UUID TEST_INVALID_CLIENT_UUID = UUID.fromString("aaaaaa-2e50-428e-ac72-b81d1dc5139a");
   private static final UUID TEST_ROLE_ID = UUID.fromString("7a0c8b2d-5e9f-41a3-b0c6-3d7f4b8a2e1d");
   private static final UUID TEST_GRANTED_USER = UUID.fromString("a9a6359c-f4a3-4268-8133-1a64a894416d");
   private static final String TEST_GRANTED_ROLE = "ADMIN";

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
             assertThat(Arrays.stream(roles).anyMatch(r -> r.getName().equals("PRM"))).isTrue();
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

   @Test
   @DisplayName("case3. role mapping")
   void roleMapping() {
      // given
      RoleRepresentation role = new RoleRepresentation();
      role.setId(TEST_ROLE_ID.toString());
      role.setName(TEST_GRANTED_ROLE);

      Mono<KeycloakResponse<Void>> grantRoleResponse = keycloakClient.roleAsync()
          .grantRole(adminAccessToken, TEST_GRANTED_USER, TEST_CLIENT_UUID, new RoleRepresentation[]{role});
      Mono<KeycloakResponse<RoleRepresentation[]>> userRole = keycloakClient.roleAsync()
          .getUserRole(adminAccessToken, TEST_GRANTED_USER, TEST_CLIENT_UUID);

      // when && then
      StepVerifier.create(grantRoleResponse)
          .assertNext((response) -> {
             assertThat(HttpResponseStatus.NO_CONTENT.code()).isEqualTo(response.getStatus());
          })
          .verifyComplete();

      StepVerifier.create(userRole)
          .assertNext((response) -> {
             assertThat(HttpResponseStatus.OK.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isPresent();

             RoleRepresentation[] roles = response.getBody().get();
             assertThat(Arrays.stream(roles).anyMatch(r -> r.getName().equals(TEST_GRANTED_ROLE))).isTrue();
          })
          .verifyComplete();
   }

   @Test
   @DisplayName("case4. required field error")
   void noRoleIdOrName() {
      // given
      RoleRepresentation role = new RoleRepresentation();

      Mono<KeycloakResponse<Void>> grantRoleResponse = keycloakClient.roleAsync()
          .grantRole(adminAccessToken, TEST_GRANTED_USER, TEST_CLIENT_UUID, new RoleRepresentation[]{role});

      // when && then
      StepVerifier.create(grantRoleResponse)
          .assertNext((response) -> {
             assertThat(HttpResponseStatus.BAD_REQUEST.code()).isEqualTo(response.getStatus());
             assertThat(response.getMessage()).contains("Role id or role name is required");
          })
          .verifyComplete();

   }

   @Test
   @DisplayName("case5. does not exist role mapping")
   void notRoleMapping() {
      // given
      RoleRepresentation role = new RoleRepresentation();
      role.setId("NONE");
      role.setName("ADMIN");

      Mono<KeycloakResponse<Void>> grantRoleResponse = keycloakClient.roleAsync()
          .grantRole(adminAccessToken, TEST_GRANTED_USER, TEST_CLIENT_UUID, new RoleRepresentation[]{role});

      // when && then
      StepVerifier.create(grantRoleResponse)
          .assertNext((response) -> {
             assertThat(HttpResponseStatus.NOT_FOUND.code()).isEqualTo(response.getStatus());
             assertThat(response.getMessage()).contains("Role not found");
          })
          .verifyComplete();
   }

   @Test
   @DisplayName("case6. grant role & remove role")
   void grantAndRemoveRole() {
      // given
      RoleRepresentation role = new RoleRepresentation();
      role.setId(TEST_ROLE_ID.toString());
      role.setName(TEST_GRANTED_ROLE);

      Mono<KeycloakResponse<Void>> grantRoleResponse = keycloakClient.roleAsync()
          .grantRole(adminAccessToken, TEST_GRANTED_USER, TEST_CLIENT_UUID, new RoleRepresentation[]{role});
      Mono<KeycloakResponse<Void>> removeRoleResponse = keycloakClient.roleAsync()
          .removeRole(adminAccessToken, TEST_GRANTED_USER, TEST_CLIENT_UUID, new RoleRepresentation[]{role});
      Mono<KeycloakResponse<RoleRepresentation[]>> userRole = keycloakClient.roleAsync()
          .getUserRole(adminAccessToken, TEST_GRANTED_USER, TEST_CLIENT_UUID);

      // when && then
      StepVerifier.create(grantRoleResponse)
          .assertNext((response) -> {
             assertThat(HttpResponseStatus.NO_CONTENT.code()).isEqualTo(response.getStatus());
          })
          .verifyComplete();

      StepVerifier.create(userRole)
          .assertNext((response) -> {
             assertThat(HttpResponseStatus.OK.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isPresent();

             RoleRepresentation[] roles = response.getBody().get();
             assertThat(Arrays.stream(roles).anyMatch(r -> r.getName().equals(TEST_GRANTED_ROLE))).isTrue();

          })
          .verifyComplete();

      StepVerifier.create(removeRoleResponse)
          .assertNext((response) -> {
             assertThat(HttpResponseStatus.NO_CONTENT.code()).isEqualTo(response.getStatus());
          })
          .verifyComplete();
      StepVerifier.create(userRole)
          .assertNext((response) -> {
             assertThat(HttpResponseStatus.OK.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isPresent();

             RoleRepresentation[] roles = response.getBody().get();
             assertThat(Arrays.stream(roles).anyMatch(r -> r.getName().equals(TEST_GRANTED_ROLE))).isFalse();
          })
          .verifyComplete();

   }
}