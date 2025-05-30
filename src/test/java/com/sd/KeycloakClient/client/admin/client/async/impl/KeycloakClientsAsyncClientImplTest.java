package com.sd.KeycloakClient.client.admin.client.async.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.sd.KeycloakClient.annotation.MockKeycloakClient;
import com.sd.KeycloakClient.common.KeycloakShareTestContainer;
import com.sd.KeycloakClient.common.TestKeycloakTokenHolder;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.ClientQueryParams;
import com.sd.KeycloakClient.factory.KeycloakClient;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.ClientRepresentation;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@MockKeycloakClient
class KeycloakClientsAsyncClientImplTest extends KeycloakShareTestContainer {

   private static KeycloakClient keycloakClient;

   private String adminAccessToken;
   private String accessToken;

   @BeforeEach
   void setup() {
      adminAccessToken = TestKeycloakTokenHolder.getAdminAccessToken(keycloakClient);
      accessToken = TestKeycloakTokenHolder.getAccessToken(keycloakClient);
   }

   @AfterAll
   static void afterAll() {
      TestKeycloakTokenHolder.removeAccessToken();
      TestKeycloakTokenHolder.removeAdminAccessToken();
   }

   @Test
   @DisplayName("case1. success case: get client info")
   void searchClientByClientId() {
      // given
      ClientQueryParams query = ClientQueryParams.builder()
          .clientId("prm-client")
          .build();
      Mono<KeycloakResponse<ClientRepresentation[]>> searchClient = keycloakClient.clientsAsync().getClientsInfo(adminAccessToken, query);

      // when & then
      StepVerifier.create(searchClient)
          .assertNext(response -> {
             assertThat(HttpResponseStatus.OK.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isPresent();

             ClientRepresentation[] clientRepresentations = response.getBody().get();
             assertThat(clientRepresentations.length).isEqualTo(1);
             assertThat(clientRepresentations[0].getClientId()).isEqualTo("prm-client");

          })
          .verifyComplete();

   }

   @Test
   @DisplayName("case2. No admin auth")
   void noAdminAuth() {
      // given
      ClientQueryParams query = ClientQueryParams.builder()
          .clientId("account")
          .build();
      Mono<KeycloakResponse<ClientRepresentation[]>> searchClient = keycloakClient.clientsAsync().getClientsInfo(accessToken, query);
      // when
      StepVerifier.create(searchClient)
          .assertNext(response -> {
             assertThat(HttpResponseStatus.FORBIDDEN.code()).isEqualTo(response.getStatus());
          });
   }

   @Test
   @DisplayName("case3. no clientId")
   void notFoundClientId() {
      // given
      ClientQueryParams query = ClientQueryParams.builder()
          .clientId("prm-client-test")
          .build();
      Mono<KeycloakResponse<ClientRepresentation[]>> searchClient = keycloakClient.clientsAsync().getClientsInfo(adminAccessToken, query);

      // when & then
      StepVerifier.create(searchClient)
          .assertNext(response -> {
             assertThat(HttpResponseStatus.OK.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isPresent();

             ClientRepresentation[] clientRepresentations = response.getBody().get();
             assertThat(clientRepresentations.length).isEqualTo(0);

          })
          .verifyComplete();

   }
}