package com.sd.KeycloakClient.client.admin.auth.async.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sd.KeycloakClient.annotation.MockKeycloakClient;
import com.sd.KeycloakClient.common.KeycloakShareTestContainer;
import com.sd.KeycloakClient.common.TestKeycloakTokenHolder;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.ScopeQueryParams;
import com.sd.KeycloakClient.factory.KeycloakClient;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.time.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@MockKeycloakClient
class KeycloakAuthScopeAsyncClientImplTest extends KeycloakShareTestContainer {

   private static KeycloakClient keycloakClient;

   private String adminAccessToken;
   private String accessToken;
   private static final String clientUuid = "de5fe303-2e50-428e-ac72-b81d1dc5139a";
   private static final String TEST_SCOPE_NAME = "GET";


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

   @DisplayName("case1. success case: get scope by scopeId")
   @Test
   void getScope() {
      // given
      Mono<String> ensureScopeId = keycloakClient.authScopeAsync()
          .getScopes(adminAccessToken, clientUuid, ScopeQueryParams.builder().name(TEST_SCOPE_NAME).build()) // GET /scope/search?name=
          .flatMap(list -> Mono.just(list.getBody().get()[0].getId()));

      // when
      Mono<KeycloakResponse<ScopeRepresentation>> whenGet =
          ensureScopeId.flatMap(scopeId ->
              keycloakClient.authScopeAsync().getScope(accessToken, clientUuid, scopeId));

      // then
      StepVerifier.create(whenGet)
          .assertNext(resp -> {
             assertThat(resp.getStatus()).isEqualTo(200);
             assertThat(resp.getBody()).isPresent();

             ScopeRepresentation body = resp.getBody().get();
             assertThat(body.getId()).isNotBlank();
             assertThat(body.getName()).isEqualTo(TEST_SCOPE_NAME);
          })
          .verifyComplete();
   }

   @DisplayName("case2. accessToken is null (401 Unauthorized)")
   @Test
   void getScopeAccessTokenNull() {
      // given
      Mono<KeycloakResponse<ScopeRepresentation>> scope = keycloakClient.authScopeAsync()
          .getScope(null, "prm-client", "TEST_SCOPE_ID");
      // when && then
      StepVerifier.create(scope)
          .assertNext(response -> {
             assertThat(HttpResponseStatus.UNAUTHORIZED.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isEmpty();
             assertThat(response.getMessage()).contains("Unauthorized");
          })
          .verifyComplete();
   }

   @DisplayName("case3. clientUuid is null (404 Not Found)")
   @Test
   void getScopeClientUuidNull() {
      // given
      Mono<KeycloakResponse<ScopeRepresentation>> scope = keycloakClient.authScopeAsync()
          .getScope(accessToken, null, "TEST_SCOPE_ID");
      // when && then
      StepVerifier.create(scope)
          .assertNext(response -> {
             assertThat(HttpResponseStatus.NOT_FOUND.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isEmpty();
             assertThat(response.getMessage()).contains("Not Found");
          })
          .verifyComplete();
   }


   @DisplayName("case4. success case: get scope by scopeId")
   @Test
   void getScopes() {
      // given
      Mono<KeycloakResponse<ScopeRepresentation[]>> scopeResponse = keycloakClient.authScopeAsync().getScopes(
          adminAccessToken,
          clientUuid,
          ScopeQueryParams.builder().name(TEST_SCOPE_NAME).build()
      );

      StepVerifier.create(scopeResponse)
          .assertNext(resp -> {
             assertThat(resp.getStatus()).isEqualTo(200);
             assertThat(resp.getBody()).isPresent();
             ScopeRepresentation body = resp.getBody().get()[0];
             assertThat(body.getId()).isNotBlank();
             assertThat(body.getName()).isEqualTo(TEST_SCOPE_NAME);
          })
          .verifyComplete();
   }

   @DisplayName("case5. success case: get scopes with empty ScopeQueryParams")
   @Test
   void getScopesWithEmptyScopeQueryParams() {
      // given
      Mono<KeycloakResponse<ScopeRepresentation[]>> scopeResponse = keycloakClient.authScopeAsync().getScopes(
          adminAccessToken,
          clientUuid,
          ScopeQueryParams.builder().build()
      );

      // when && then
      StepVerifier.create(scopeResponse)
          .assertNext(resp -> {
             assertThat(resp.getStatus()).isEqualTo(200);
             assertThat(resp.getBody()).isPresent();
             assertThat(resp.getBody().get()).isNotEmpty();
             assertThat(resp.getBody().get().length).isEqualTo(9);
          })
          .verifyComplete();
   }

   @DisplayName("case6. exception case: get scopes with null ScopeQueryParams")
   @Test
   void getScopesWithNullScopeQueryParams() {
      // given
      assertThrows(NullPointerException.class, () ->
          keycloakClient.authScopeAsync().getScopes(adminAccessToken, clientUuid, null)
      );
   }


   @DisplayName("case7. exception case: get scopes with null adminAccessToken")
   @Test
   void getScopesWithNullAdminAccessToken() {
      // given
      Mono<KeycloakResponse<ScopeRepresentation[]>> scopeResponse = keycloakClient.authScopeAsync().getScopes(
          null,
          clientUuid,
          ScopeQueryParams.builder().name(TEST_SCOPE_NAME).build()
      );

      // when && then
      StepVerifier.create(scopeResponse)
          .assertNext(response -> {
             assertThat(HttpResponseStatus.UNAUTHORIZED.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isEmpty();
             assertThat(response.getMessage()).contains("Unauthorized");
          })
          .verifyComplete();
   }

   @DisplayName("case8. success case: create scope")
   @Test
   void createScope() {
      // given
      String testName = "TestScope";
      String testIconUri = "http://example.com/icon.png";
      String testDisplayName = "This is a test scope";
      ScopeRepresentation newScope = new ScopeRepresentation();
      newScope.setName(testName);
      newScope.setIconUri(testIconUri);
      newScope.setDisplayName(testDisplayName);
      Mono<KeycloakResponse<Void>> scopeResponse = keycloakClient.authScopeAsync().createScope(
          adminAccessToken,
          clientUuid,
          newScope
      );

      // when
      StepVerifier.create(scopeResponse)
          .assertNext(resp -> {
             assertThat(resp.getStatus()).isEqualTo(201);
             assertThat(resp.getBody()).isEmpty();
          })
          .verifyComplete();

      // then
      Mono<KeycloakResponse<ScopeRepresentation[]>> getScopesResponse = keycloakClient.authScopeAsync().getScopes(
          adminAccessToken,
          clientUuid,
          ScopeQueryParams.builder().name(testName).build()
      );
      StepVerifier.create(getScopesResponse)
          .assertNext(resp -> {
             assertThat(resp.getStatus()).isEqualTo(200);
             assertThat(resp.getBody()).isPresent();
             ScopeRepresentation[] scopes = resp.getBody().get();
             assertThat(scopes).isNotEmpty();
             assertThat(scopes[0].getName()).isEqualTo(testName);
             assertThat(scopes[0].getIconUri()).isEqualTo(testIconUri);
             assertThat(scopes[0].getDisplayName()).isEqualTo(testDisplayName);
          })
          .verifyComplete();
   }

   @DisplayName("case9. exception case: create scope with null clientUuid")
   @Test
   void createScopeWithNullClientUuid() {
      // given
      ScopeRepresentation newScope = new ScopeRepresentation();
      newScope.setName("TestScope");
      Mono<KeycloakResponse<Void>> scopeResponse = keycloakClient.authScopeAsync().createScope(
          adminAccessToken,
          null,
          newScope
      );

      // when && then
      StepVerifier.create(scopeResponse)
          .assertNext(response -> {
             assertThat(HttpResponseStatus.NOT_FOUND.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isEmpty();
             assertThat(response.getMessage()).contains("Not Found");
          })
          .verifyComplete();
   }

   @DisplayName("case10. success case: create scope with empty ScopeRepresentation")
   @Test
   void createScopeWithEmptyScopeRepresentation() {
      // given
      ScopeRepresentation newScope = new ScopeRepresentation();
      keycloakClient.authScopeAsync().createScope(
          adminAccessToken,
          clientUuid,
          newScope
      ).block();

      // when & then
      Mono<KeycloakResponse<ScopeRepresentation[]>> getScopesResponse = keycloakClient.authScopeAsync().getScopes(
          adminAccessToken,
          clientUuid,
          ScopeQueryParams.builder().name(newScope.getName()).build()
      );
      StepVerifier.create(getScopesResponse)
          .assertNext(resp -> {
             assertThat(resp.getStatus()).isEqualTo(200);
             assertThat(resp.getBody()).isPresent();
             ScopeRepresentation[] scopes = resp.getBody().get();
             assertThat(scopes).isNotEmpty();
             assertThat(scopes[0].getId()).isNotBlank();
          })
          .verifyComplete();
   }

   @DisplayName("case11. exception case: create scope with null name")
   @Test
   void createScopeWithNullName() {
      // given
      ScopeRepresentation newScope = new ScopeRepresentation();
      newScope.setName(null);
      Mono<KeycloakResponse<Void>> scopeResponse = keycloakClient.authScopeAsync().createScope(
          adminAccessToken,
          clientUuid,
          newScope
      );

      // when && then
      StepVerifier.create(scopeResponse)
          .assertNext(response -> {
             assertThat(HttpResponseStatus.CONFLICT.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isEmpty();
             assertThat(response.getMessage()).contains("unknown_error");
          })
          .verifyComplete();
   }


   @Test
   @DisplayName("case12. success case: update scope")
   void updateScope() {

      // given : before create a new scope
      String newScopeName = "TestScopeToUpdate";
      String updateScopeName = "UpdatedScopeName";
      ScopeRepresentation newScope = new ScopeRepresentation();
      newScope.setName(newScopeName);

      keycloakClient.authScopeAsync().createScope(
          adminAccessToken,
          clientUuid,
          newScope
      ).block();

      // scope get ScopeRepresentation
      ScopeRepresentation getScope = keycloakClient.authScopeAsync()
          .getScopes(adminAccessToken, clientUuid,
              ScopeQueryParams.builder().name(newScope.getName()).build())
          .map(KeycloakResponse::getBody)
          .switchIfEmpty(Mono.error(new IllegalStateException("empty body")))
          .block(Duration.ofSeconds(5))
          .get()[0];
      getScope.setName(updateScopeName);

      // when: update the created scope, then verify the update
      Mono<KeycloakResponse<Void>> updateScope = keycloakClient.authScopeAsync().updateScope(
          adminAccessToken,
          clientUuid,
          getScope);
      StepVerifier.create(updateScope)
          .assertNext(response -> {
             assertThat(response.getStatus()).isEqualTo(204);
             assertThat(response.getBody()).isEmpty();
          })
          .verifyComplete();
   }

   @Test
   @DisplayName("case13. exception case: update scope with null accessToken")
   void updateScopeWithNullAccessToken() {
      // given
      ScopeRepresentation scopeToUpdate = new ScopeRepresentation();
      scopeToUpdate.setName("ScopeToUpdate");

      // when
      Mono<KeycloakResponse<Void>> updateScope = keycloakClient.authScopeAsync().updateScope(
          null, // accessToken is null
          clientUuid,
          scopeToUpdate);

      // then
      StepVerifier.create(updateScope)
          .assertNext(response -> {
             assertThat(HttpResponseStatus.UNAUTHORIZED.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isEmpty();
             assertThat(response.getMessage()).contains("Unauthorized");
          })
          .verifyComplete();
   }

   // deleteScope 200 OK
   @Test
   @DisplayName("case14. success case: delete scope")
   void deleteScopeSuccess() {
      // given : create a new scope
      String scopeName = "ScopeToDelete";
      ScopeRepresentation newScope = new ScopeRepresentation();
      newScope.setName(scopeName);

      keycloakClient.authScopeAsync().createScope(
          adminAccessToken,
          clientUuid,
          newScope
      ).block();
      String scopeId = keycloakClient.authScopeAsync().getScopes(
          accessToken,
          clientUuid,
          ScopeQueryParams.builder().name(scopeName).build()
      ).block().getBody().get()[0].getId();// Ensure the scope is created

      // then: delete the created scope
      Mono<KeycloakResponse<Void>> deleteScopeResponse = keycloakClient.authScopeAsync().deleteScope(
          adminAccessToken,
          clientUuid,
          scopeId
      );

      StepVerifier.create(deleteScopeResponse)
          .assertNext(response -> {
             assertThat(response.getStatus()).isEqualTo(204);
             assertThat(response.getBody()).isEmpty();
          })
          .verifyComplete();
   }

   @Test
   @DisplayName("case15. exception case: delete scope with null accessToken")
   void deleteScopeWithNullAccessToken() {
      // given
      String scopeId = "test-scope-id";

      // when
      Mono<KeycloakResponse<Void>> deleteScopeResponse = keycloakClient.authScopeAsync().deleteScope(
          null, // accessToken is null
          clientUuid,
          scopeId
      );

      // then
      StepVerifier.create(deleteScopeResponse)
          .assertNext(response -> {
             assertThat(HttpResponseStatus.UNAUTHORIZED.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isEmpty();
             assertThat(response.getMessage()).contains("Unauthorized");
          })
          .verifyComplete();
   }
}