package com.sd.KeycloakClient.client.admin.auth.async.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.sd.KeycloakClient.annotation.MockKeycloakClient;
import com.sd.KeycloakClient.common.KeycloakShareTestContainer;
import com.sd.KeycloakClient.common.TestKeycloakTokenHolder;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.ScopeQueryParams;
import com.sd.KeycloakClient.factory.KeycloakClient;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.time.Duration;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@MockKeycloakClient
class KeycloakAuthScopeAsyncClientImplTest extends KeycloakShareTestContainer {

   private static KeycloakClient keycloakClient;

   private String adminAccessToken;
   private String accessToken;
   private static final UUID CLIENT_UUID = UUID.fromString("de5fe303-2e50-428e-ac72-b81d1dc5139a");
   private static final String TEST_SCOPE_NAME = "GET";
   private static final int EXISTED_SCOPE_COUNT = 9;


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

   @Nested
   @TestInstance(TestInstance.Lifecycle.PER_CLASS)
   @DisplayName("Get Scope Tests")
   class GetScopeTests {

      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Success Cases")
      class SuccessCases {

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

         @DisplayName("case1: get scope by scopeId -> success")
         @Test
         void getScope() {
            // given
            Mono<String> ensureScopeId = keycloakClient.authScopeAsync()
                .getScopes(adminAccessToken, CLIENT_UUID,
                    ScopeQueryParams.builder().name(TEST_SCOPE_NAME).build()) // GET /scope/search?name=
                .flatMap(list -> Mono.just(list.getBody().get()[0].getId()));

            // when
            Mono<KeycloakResponse<ScopeRepresentation>> whenGet =
                ensureScopeId.flatMap(scopeId ->
                    keycloakClient.authScopeAsync().getScope(accessToken, CLIENT_UUID, UUID.fromString(scopeId)));

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
      }

      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Failure Cases")
      class FailureCases {

         @DisplayName("case2: get scope with null accessToken -> 401 Unauthorized")
         @Test
         void getScopeAccessTokenNull() {
            // given
            Mono<KeycloakResponse<ScopeRepresentation>> scope = keycloakClient.authScopeAsync()
                .getScope(null, UUID.fromString("de5fe303-2e50-428e-ac72-b81d1dc5139a"), UUID.randomUUID());
            // when && then
            StepVerifier.create(scope)
                .assertNext(response -> {
                   assertThat(HttpResponseStatus.UNAUTHORIZED.code()).isEqualTo(response.getStatus());
                   assertThat(response.getBody()).isEmpty();
                   assertThat(response.getMessage()).contains("Unauthorized");
                })
                .verifyComplete();
         }

         @DisplayName("case3: get scope with null clientUuid -> 404 Not Found")
         @Test
         void getScopeClientUuidNull() {
            // given
            Mono<KeycloakResponse<ScopeRepresentation>> scope = keycloakClient.authScopeAsync()
                .getScope(accessToken, null, UUID.randomUUID());
            // when && then
            StepVerifier.create(scope)
                .assertNext(response -> {
                   assertThat(HttpResponseStatus.BAD_REQUEST.code()).isEqualTo(response.getStatus());
                   assertThat(response.getBody()).isEmpty();
                   assertThat(response.getMessage()).contains("clientUuid is required");
                })
                .verifyComplete();
         }
      }
   }

   @Nested
   @TestInstance(TestInstance.Lifecycle.PER_CLASS)
   @DisplayName("Get Scopes Tests")
   class GetScopesTests {

      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Success cases")
      class SuccessCases {

         static Stream<String> nameProvider() {
            return Stream.of("GET", "POST"); // adjust to match existing test data
         }

         @ParameterizedTest(name = "case4.{index}: get scopes by name={0} -> all results match")
         @DisplayName("case4: get scopes by name -> success")
         @MethodSource("nameProvider")
         void getScopes_singleName_success(String name) {
            Mono<KeycloakResponse<ScopeRepresentation[]>> mono =
                keycloakClient.authScopeAsync().getScopes(
                    adminAccessToken,
                    CLIENT_UUID,
                    ScopeQueryParams.builder().name(name).build()
                );

            StepVerifier.create(mono.timeout(Duration.ofSeconds(5)))
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(200);
                   ScopeRepresentation[] arr = resp.getBody().orElseThrow();
                   assertThat(arr).isNotEmpty();
                   assertThat(arr).allSatisfy(s -> assertThat(s.getName()).isEqualTo(name));
                })
                .verifyComplete();
         }


         static Stream<Arguments> andSuccessProvider() {
            return Stream.of(
                Arguments.of("GET", 0, 100, 1),
                Arguments.of("GET", 0, 1, 1),
                Arguments.of("GET", 1, 100, 0) // skipping the first element -> possibly 0
            );
         }

         @ParameterizedTest(name = "case5.{index}: name={0}, first={1}, max={2} -> AND satisfied & size ≤ max")
         @DisplayName("case5: get scopes with multiple conditions (AND) -> success")
         @MethodSource("andSuccessProvider")
         void getScopes_andCombination_success(String name, Integer first, Integer max, int expectedMin) {
            Mono<KeycloakResponse<ScopeRepresentation[]>> mono =
                keycloakClient.authScopeAsync().getScopes(
                    adminAccessToken,
                    CLIENT_UUID,
                    ScopeQueryParams.builder().name(name).first(first).max(max).build()
                );

            StepVerifier.create(mono.timeout(Duration.ofSeconds(5)))
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(200);
                   ScopeRepresentation[] arr = resp.getBody().orElseThrow();
                   assertThat(arr.length).isBetween(0, max);
                   assertThat(arr.length).isGreaterThanOrEqualTo(expectedMin);
                   assertThat(arr).allSatisfy(s -> assertThat(s.getName()).isEqualTo(name));
                })
                .verifyComplete();
         }

      }

      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Failure cases")
      class FailureCases {

         @DisplayName("case7: get scopes with null adminAccessToken -> 401 Unauthorized")
         @Test
         void getScopesWithNullAdminAccessToken() {
            // given
            Mono<KeycloakResponse<ScopeRepresentation[]>> scopeResponse = keycloakClient.authScopeAsync().getScopes(
                null,
                CLIENT_UUID,
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
      }

      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Boundary Cases")
      class BoundaryCases {

         @ParameterizedTest(name = "case6.{index}: first={0}, max={1} -> 200 & size ≤ max")
         @DisplayName("case6: get scopes with paging boundaries -> success")
         @CsvSource({
             "0,1",
             "0,100",
             "1,1",
             "5,2"
         })
         void getScopes_boundary_paging(Integer first, Integer max) {
            Mono<KeycloakResponse<ScopeRepresentation[]>> mono =
                keycloakClient.authScopeAsync().getScopes(
                    adminAccessToken,
                    CLIENT_UUID,
                    ScopeQueryParams.builder().first(first).max(max).build()
                );

            StepVerifier.create(mono.timeout(Duration.ofSeconds(5)))
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(200);
                   ScopeRepresentation[] arr = resp.getBody().orElseThrow();
                   assertThat(arr.length).isBetween(0, max);
                })
                .verifyComplete();
         }
      }
   }

   @Nested
   @TestInstance(TestInstance.Lifecycle.PER_CLASS)
   @DisplayName("Create Scope Tests")
   class CreateScopeTests {

      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Success Cases")
      class SuccessCases {

         @DisplayName("case8: create scope -> success")
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
                CLIENT_UUID,
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
                CLIENT_UUID,
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


         @DisplayName("case9: create scope with empty ScopeRepresentation -> success")
         @Test
         void createScopeWithEmptyScopeRepresentation() {
            // given
            ScopeRepresentation newScope = new ScopeRepresentation();
            keycloakClient.authScopeAsync().createScope(
                adminAccessToken,
                CLIENT_UUID,
                newScope
            ).block();

            // when & then
            Mono<KeycloakResponse<ScopeRepresentation[]>> getScopesResponse = keycloakClient.authScopeAsync().getScopes(
                adminAccessToken,
                CLIENT_UUID,
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
      }

      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Failure Cases")
      class FailureCases {

         @DisplayName("case10 : create scope with null accessToken -> 401 Unauthorized")
         @Test
         void createScopeWithNullAccessToken() {
            // given
            ScopeRepresentation newScope = new ScopeRepresentation();
            newScope.setName("TestScope");
            Mono<KeycloakResponse<Void>> scopeResponse = keycloakClient.authScopeAsync().createScope(
                null,
                CLIENT_UUID,
                newScope
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


         @DisplayName("case11: create scope with null clientUuid -> 404 Not Found")
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
                   assertThat(HttpResponseStatus.BAD_REQUEST.code()).isEqualTo(response.getStatus());
                   assertThat(response.getBody()).isEmpty();
                   assertThat(response.getMessage()).contains("clientUuid is required");
                })
                .verifyComplete();
         }

         @DisplayName("case12: create scope with null name -> 409 Conflict")
         @Test
         void createScopeWithNullName() {
            // given
            ScopeRepresentation newScope = new ScopeRepresentation();
            newScope.setName(null);
            Mono<KeycloakResponse<Void>> scopeResponse = keycloakClient.authScopeAsync().createScope(
                adminAccessToken,
                CLIENT_UUID,
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
      }
   }


   @Nested
   @TestInstance(TestInstance.Lifecycle.PER_CLASS)
   @DisplayName("Update Scope Tests")
   class UpdateScopeTests {

      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Success Cases")
      class SuccessCases {

         @Test
         @DisplayName("case12: update scope -> success")
         void updateScope() {

            // given :   create a new scope
            String newScopeName = "TestScopeToUpdate";
            String updateScopeName = "UpdatedScopeName";
            ScopeRepresentation newScope = new ScopeRepresentation();
            newScope.setName(newScopeName);

            keycloakClient.authScopeAsync().createScope(
                adminAccessToken,
                CLIENT_UUID,
                newScope
            ).block();

            // scope get ScopeRepresentation
            ScopeRepresentation getScope = keycloakClient.authScopeAsync()
                .getScopes(adminAccessToken, CLIENT_UUID,
                    ScopeQueryParams.builder().name(newScope.getName()).build())
                .map(KeycloakResponse::getBody)
                .switchIfEmpty(Mono.error(new IllegalStateException("empty body")))
                .block(Duration.ofSeconds(5))
                .get()[0];
            getScope.setName(updateScopeName);

            // when: update the created scope, then verify the update
            Mono<KeycloakResponse<Void>> updateScope = keycloakClient.authScopeAsync().updateScope(
                adminAccessToken,
                CLIENT_UUID,
                getScope);
            StepVerifier.create(updateScope)
                .assertNext(response -> {
                   assertThat(response.getStatus()).isEqualTo(204);
                   assertThat(response.getBody()).isEmpty();
                })
                .verifyComplete();
         }
      }

      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Failure Cases")
      class FailureCases {

         @Test
         @DisplayName("case13: update scope with null accessToken -> 401 Unauthorized")
         void updateScopeWithNullAccessToken() {
            // given
            ScopeRepresentation scopeToUpdate = new ScopeRepresentation();
            scopeToUpdate.setId(UUID.randomUUID().toString());
            scopeToUpdate.setName("ScopeToUpdate");

            // when
            Mono<KeycloakResponse<Void>> updateScope = keycloakClient.authScopeAsync().updateScope(
                null, // accessToken is null
                CLIENT_UUID,
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
      }
   }

   @Nested
   @TestInstance(TestInstance.Lifecycle.PER_CLASS)
   @DisplayName("Delete Scope Tests")
   class deleteScopeTests {

      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Success Cases")
      class SuccessCases {

         @Test
         @DisplayName("case14: delete scope -> success")
         void deleteScopeSuccess() {
            // given : create a new scope
            String scopeName = "ScopeToDelete";
            ScopeRepresentation newScope = new ScopeRepresentation();
            newScope.setName(scopeName);

            keycloakClient.authScopeAsync().createScope(
                adminAccessToken,
                CLIENT_UUID,
                newScope
            ).block();
            String scopeId = keycloakClient.authScopeAsync().getScopes(
                accessToken,
                CLIENT_UUID,
                ScopeQueryParams.builder().name(scopeName).build()
            ).block().getBody().get()[0].getId();// Ensure the scope is created

            // then: delete the created scope
            Mono<KeycloakResponse<Void>> deleteScopeResponse = keycloakClient.authScopeAsync().deleteScope(
                adminAccessToken,
                CLIENT_UUID,
                UUID.fromString(scopeId)
            );

            StepVerifier.create(deleteScopeResponse)
                .assertNext(response -> {
                   assertThat(response.getStatus()).isEqualTo(204);
                   assertThat(response.getBody()).isEmpty();
                })
                .verifyComplete();
         }
      }

      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Failure Cases")
      class FailureCases {

         @Test
         @DisplayName("case15: delete scope with null accessToken -> 401 Unauthorized")
         void deleteScopeWithNullAccessToken() {
            // given
            UUID scopeId = UUID.randomUUID();

            // when
            Mono<KeycloakResponse<Void>> deleteScopeResponse = keycloakClient.authScopeAsync().deleteScope(
                null, // accessToken is null
                CLIENT_UUID,
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
   }
}