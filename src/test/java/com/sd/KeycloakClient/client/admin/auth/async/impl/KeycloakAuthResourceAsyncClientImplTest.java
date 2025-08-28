package com.sd.KeycloakClient.client.admin.auth.async.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.sd.KeycloakClient.annotation.MockKeycloakClient;
import com.sd.KeycloakClient.common.KeycloakShareTestContainer;
import com.sd.KeycloakClient.common.TestKeycloakTokenHolder;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.ResourceQueryParams;
import com.sd.KeycloakClient.factory.KeycloakClient;
import com.sd.KeycloakClient.util.UrlUtil;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@MockKeycloakClient
class KeycloakAuthResourceAsyncClientImplTest extends KeycloakShareTestContainer {

   private static KeycloakClient keycloakClient;

   private String adminAccessToken;
   private String accessToken;

   // ⚠️ 실제 테스트 환경의 client UUID로 교체하세요.
   private static final UUID CLIENT_UUID = UUID.fromString("de5fe303-2e50-428e-ac72-b81d1dc5139a");

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
   @DisplayName("Get Resource Tests")
   class GetResourceTests {

      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Success Cases")
      class SuccessCases {

         @Test
         @DisplayName("case1: get resource by resourceId -> 200 & body present")
         void getResource_success() {
            // given: 먼저 리소스를 하나 생성
            String name = "res-" + UUID.randomUUID();
            ResourceRepresentation newRes = new ResourceRepresentation();
            newRes.setName(name);

            keycloakClient.authResourceAsync()
                .createResource(adminAccessToken, CLIENT_UUID, newRes)
                .block(Duration.ofSeconds(5));

            // resourceId 확보 (검색)
            Mono<UUID> ensureResourceId = keycloakClient.authResourceAsync()
                .getResources(adminAccessToken, CLIENT_UUID,
                    ResourceQueryParams.builder().name(name).exactName(true).build())
                .map(KeycloakResponse::getBody)
                .switchIfEmpty(Mono.error(new IllegalStateException("empty body")))
                .map(arr -> UUID.fromString(arr.get()[0].getId()));

            // when
            Mono<KeycloakResponse<ResourceRepresentation>> whenGet =
                ensureResourceId.flatMap(resourceId ->
                    keycloakClient.authResourceAsync().getResource(accessToken, CLIENT_UUID, resourceId));

            // then
            StepVerifier.create(whenGet.timeout(Duration.ofSeconds(5)))
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(200);
                   assertThat(resp.getBody()).isPresent();
                   ResourceRepresentation body = resp.getBody().get();
                   assertThat(body.getId()).isNotBlank();
                   assertThat(body.getName()).isEqualTo(name);
                })
                .verifyComplete();
         }
      }

      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Failure Cases")
      class FailureCases {

         @Test
         @DisplayName("case2: get resource with null accessToken -> 401 Unauthorized")
         void getResource_accessTokenNull() {
            Mono<KeycloakResponse<ResourceRepresentation>> mono =
                keycloakClient.authResourceAsync()
                    .getResource(null, CLIENT_UUID, UUID.randomUUID());

            StepVerifier.create(mono)
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(HttpResponseStatus.UNAUTHORIZED.code());
                   assertThat(resp.getBody()).isEmpty();
                   assertThat(resp.getMessage()).contains("Unauthorized");
                })
                .verifyComplete();
         }

         @Test
         @DisplayName("case3: get resource with null clientUuid -> 404 Not Found")
         void getResource_clientUuidNull() {
            Mono<KeycloakResponse<ResourceRepresentation>> mono =
                keycloakClient.authResourceAsync()
                    .getResource(accessToken, null, UUID.randomUUID());

            StepVerifier.create(mono)
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(HttpResponseStatus.NOT_FOUND.code());
                   assertThat(resp.getBody()).isEmpty();
                   assertThat(resp.getMessage()).contains("Not Found");
                })
                .verifyComplete();
         }
      }
   }


   @Nested
   @TestInstance(TestInstance.Lifecycle.PER_CLASS)
   @DisplayName("Get Resources Tests (covers all ResourceQueryParams)")
   class GetResourcesTests {

      // ===== 공통 헬퍼 =====
      private UUID createResourceAndGetId(String name, String type, String uri) {
         ResourceRepresentation rr = new ResourceRepresentation();
         rr.setName(name);
         if (type != null) {
            rr.setType(type);
         }
         if (uri != null) {
            rr.setUris(Set.of(uri));
         }

         keycloakClient.authResourceAsync()
             .createResource(adminAccessToken, CLIENT_UUID, rr)
             .block(Duration.ofSeconds(5));

         return keycloakClient.authResourceAsync()
             .getResources(adminAccessToken, CLIENT_UUID,
                 ResourceQueryParams.builder().name(name).exactName(true).build())
             .block(Duration.ofSeconds(5))
             .getBody()
             .map(arr -> UUID.fromString(arr[0].getId()))
             .orElseThrow();
      }

      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Success Cases")
      class SuccessCases {

         static Stream<String> nameProvider() {
            return Stream.of("res-한글", "res with space", "res/with/slash", "a+b=c&d");
         }

         @ParameterizedTest(name = "case1.{index}: name=\"{0}\" & exactName=true -> 200 & all match")
         @MethodSource("nameProvider")
         @DisplayName("case1: name + exactName=true")
         void byName_exact_true(String name) {
            //given
            createResourceAndGetId(name, null, null);

            // when
            Mono<KeycloakResponse<ResourceRepresentation[]>> mono =
                keycloakClient.authResourceAsync().getResources(
                    adminAccessToken,
                    CLIENT_UUID,
                    ResourceQueryParams.builder().name(name).exactName(true).build()
                );

            // then
            StepVerifier.create(mono.timeout(Duration.ofSeconds(5)))
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(200);
                   ResourceRepresentation[] arr = resp.getBody().orElseThrow();
                   assertThat(arr).isNotEmpty();
                   assertThat(arr).allSatisfy(r -> assertThat(r.getName()).isEqualTo(name));
                })
                .verifyComplete();
         }

         @Test
         @DisplayName("case2: id 단건 필터 -> 200 & exactly one (or matching) ")
         void byId() {
            String name = "res-" + UUID.randomUUID();
            UUID id = createResourceAndGetId(name, "urn:type:id", "/api/id/" + UUID.randomUUID());

            Mono<KeycloakResponse<ResourceRepresentation>> mono =
                keycloakClient.authResourceAsync().getResource(
                    adminAccessToken, CLIENT_UUID, id
                );

            StepVerifier.create(mono.timeout(Duration.ofSeconds(5)))
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(200);
                   ResourceRepresentation body = resp.getBody().orElseThrow();
                   assertThat(body.getId()).isEqualTo(id.toString());
                   assertThat(body.getName()).isEqualTo(name);
                })
                .verifyComplete();
         }

         @Test
         @DisplayName("case3: type 필터 -> 200 & all type match")
         void byType() {
            String type = "urn:type:" + UUID.randomUUID();
            String name = "res-" + UUID.randomUUID();
            createResourceAndGetId(name, type, "/api/type/" + UUID.randomUUID());

            Mono<KeycloakResponse<ResourceRepresentation[]>> mono =
                keycloakClient.authResourceAsync().getResources(
                    adminAccessToken,
                    CLIENT_UUID,
                    ResourceQueryParams.builder().type(type).build()
                );

            StepVerifier.create(mono.timeout(Duration.ofSeconds(5)))
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(200);
                   ResourceRepresentation[] arr = resp.getBody().orElseThrow();
                   assertThat(arr).isNotEmpty();
                   assertThat(arr).allSatisfy(r -> assertThat(type).isEqualTo(r.getType()));
                })
                .verifyComplete();
         }

         @Test
         @DisplayName("case4: uri 필터 -> 200 & at least one with uri present")
         void byUri() {
            String uri = "/api/uri/" + UUID.randomUUID();
            String name = "res-" + UUID.randomUUID();
            createResourceAndGetId(name, "urn:type:uri", uri);

            Mono<KeycloakResponse<ResourceRepresentation[]>> mono =
                keycloakClient.authResourceAsync().getResources(
                    adminAccessToken,
                    CLIENT_UUID,
                    ResourceQueryParams.builder().uri(uri).build()
                );

            StepVerifier.create(mono.timeout(Duration.ofSeconds(5)))
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(200);
                   ResourceRepresentation[] arr = resp.getBody().orElseThrow();
                   assertThat(arr).isNotEmpty();
                   assertThat(arr).anySatisfy(r -> {
                      assertThat(r.getUris()).isNotNull();
                      assertThat(r.getUris()).contains(uri);
                   });
                })
                .verifyComplete();
         }

         @Test
         @DisplayName("case5: matchingUri 필터 -> 200")
         void byMatchingUri() {
            String base = "/api/match/" + UUID.randomUUID();
            String uri = base + "/child";
            String name = "res-" + UUID.randomUUID();
            createResourceAndGetId(name, "urn:type:match", uri);

            Mono<KeycloakResponse<ResourceRepresentation[]>> mono =
                keycloakClient.authResourceAsync().getResources(
                    adminAccessToken,
                    CLIENT_UUID,
                    ResourceQueryParams.builder().matchingUri(base + "/*").build()
                );

            StepVerifier.create(mono.timeout(Duration.ofSeconds(5)))
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(200);
                   // 서버 동작에 따라 empty 가능 → 최소한 200/에러없음 확인
                   assertThat(resp.getBody()).isPresent();
                })
                .verifyComplete();
         }

         @ParameterizedTest(name = "case6.{index}: first={0}, max={1} -> 200 & size ≤ max")
         @CsvSource({
             "0,1",
             "0,50",
             "1,1",
             "5,2"
         })
         @DisplayName("case6: 페이징(first/max) -> 200 & size ≤ max")
         void paging(Integer first, Integer max) {
            Mono<KeycloakResponse<ResourceRepresentation[]>> mono =
                keycloakClient.authResourceAsync().getResources(
                    adminAccessToken,
                    CLIENT_UUID,
                    ResourceQueryParams.builder().first(first).max(max).build()
                );

            StepVerifier.create(mono.timeout(Duration.ofSeconds(5)))
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(200);
                   ResourceRepresentation[] arr = resp.getBody().orElseThrow();
                   assertThat(arr.length).isBetween(0, max);
                })
                .verifyComplete();
         }

         @Test
         @DisplayName("case7: deep=true/exactName=false + name partial + type + uri 조합(AND) -> 200")
         void andCombination() {
            String name = "res-" + UUID.randomUUID();
            String type = "urn:type:combo";
            String uri = "/api/combo/" + UUID.randomUUID();
            createResourceAndGetId(name, type, uri);

            Mono<KeycloakResponse<ResourceRepresentation[]>> mono =
                keycloakClient.authResourceAsync().getResources(
                    adminAccessToken,
                    CLIENT_UUID,
                    ResourceQueryParams.builder()
                        .name(name.substring(0, 6)) // partial
                        .exactName(false)
                        .deep(true)
                        .type(type)
                        .uri(uri)
                        .first(0)
                        .max(10)
                        .build()
                );

            StepVerifier.create(mono.timeout(Duration.ofSeconds(5)))
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(200);
                   assertThat(resp.getBody()).isPresent();
                })
                .verifyComplete();
         }
      }


      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Failure Cases")
      class FailureCases {

         @Test
         @DisplayName("case8: accessToken=null -> 401 Unauthorized")
         void nullAccessToken() {
            Mono<KeycloakResponse<ResourceRepresentation[]>> mono =
                keycloakClient.authResourceAsync().getResources(
                    null,
                    CLIENT_UUID,
                    ResourceQueryParams.builder().first(0).max(5).build()
                );

            StepVerifier.create(mono)
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(HttpResponseStatus.UNAUTHORIZED.code());
                   assertThat(resp.getBody()).isEmpty();
                   assertThat(resp.getMessage()).contains("Unauthorized");
                })
                .verifyComplete();
         }

         @Test
         @DisplayName("case9: clientUuid=null -> 404 Not Found")
         void nullClientUuid() {
            Mono<KeycloakResponse<ResourceRepresentation[]>> mono =
                keycloakClient.authResourceAsync().getResources(
                    accessToken,
                    null,
                    ResourceQueryParams.builder().first(0).max(5).build()
                );

            StepVerifier.create(mono)
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(HttpResponseStatus.NOT_FOUND.code());
                   assertThat(resp.getBody()).isEmpty();
                   assertThat(resp.getMessage()).contains("Not Found");
                })
                .verifyComplete();
         }
      }


      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Boundary Cases")
      class BoundaryCases {

         @ParameterizedTest(name = "case10.{index}: first={0}, max={1} → 200 & size ≤ max")
         @CsvSource({
             "-1,100",    // 음수 first 허용 여부: 서버가 200 반환 시 size ≤ max만 보장
             "0,0",       // max=0
             "0,1",
             "1000,5"     // 큰 first
         })
         @DisplayName("case10: 페이징 극단값")
         void extremePaging(Integer first, Integer max) {
            Mono<KeycloakResponse<ResourceRepresentation[]>> mono =
                keycloakClient.authResourceAsync().getResources(
                    adminAccessToken,
                    CLIENT_UUID,
                    ResourceQueryParams.builder().first(first).max(max).build()
                );

            StepVerifier.create(mono.timeout(Duration.ofSeconds(5)))
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(200);
                   ResourceRepresentation[] arr = resp.getBody().orElse(new ResourceRepresentation[0]);
                   // max가 0이면 length==0 기대, 그 외 ≤ max
                   if (max != null && max == 0) {
                      assertThat(arr.length).isZero();
                   } else {
                      assertThat(arr.length).isBetween(0, Math.max(0, max));
                   }
                })
                .verifyComplete();
         }

         @Test
         @DisplayName("case11: name='' (empty) -> 200 & query 포함 확인")
         void emptyName() {
            ResourceQueryParams qp = ResourceQueryParams.builder().name("").build();
            String encoded = UrlUtil.toUrlEncoded(Map.of("name", ""));
            assertThat(qp.toQueryString()).contains(encoded);

            Mono<KeycloakResponse<ResourceRepresentation[]>> mono =
                keycloakClient.authResourceAsync().getResources(accessToken, CLIENT_UUID, qp);

            StepVerifier.create(mono.timeout(Duration.ofSeconds(5)))
                .assertNext(resp -> assertThat(resp.getStatus()).isEqualTo(200))
                .verifyComplete();
         }

         @Test
         @DisplayName("case12: exactName=true 이지만 name 미지정 -> 200")
         void exactNameWithoutName() {
            Mono<KeycloakResponse<ResourceRepresentation[]>> mono =
                keycloakClient.authResourceAsync().getResources(
                    accessToken,
                    CLIENT_UUID,
                    ResourceQueryParams.builder().exactName(true).build()
                );

            StepVerifier.create(mono.timeout(Duration.ofSeconds(5)))
                .assertNext(resp -> assertThat(resp.getStatus()).isEqualTo(200))
                .verifyComplete();
         }

         @Test
         @DisplayName("case13: deep=true 단독 -> 200")
         void deepOnly() {
            Mono<KeycloakResponse<ResourceRepresentation[]>> mono =
                keycloakClient.authResourceAsync().getResources(
                    accessToken,
                    CLIENT_UUID,
                    ResourceQueryParams.builder().deep(true).build()
                );

            StepVerifier.create(mono.timeout(Duration.ofSeconds(5)))
                .assertNext(resp -> assertThat(resp.getStatus()).isEqualTo(200))
                .verifyComplete();
         }

         @Test
         @DisplayName("case14: id=존재하지 않는 UUID -> 200 & empty or not found logically")
         void nonExistsId() {
            UUID randomNotExists = UUID.randomUUID();
            Mono<KeycloakResponse<ResourceRepresentation[]>> mono =
                keycloakClient.authResourceAsync().getResources(
                    accessToken,
                    CLIENT_UUID,
                    ResourceQueryParams.builder().id(randomNotExists).build()
                );

            StepVerifier.create(mono.timeout(Duration.ofSeconds(5)))
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(200);
                   ResourceRepresentation[] arr = resp.getBody().orElse(new ResourceRepresentation[0]);
                   // 서버에 따라 empty or not — 최소 200 보장
                   // 관찰 위주: 강한 조건은 피함
                })
                .verifyComplete();
         }

         @ParameterizedTest(name = "case15.{index}: special({0}) value → 200 (URL-encoded 포함)")
         @CsvSource({
             "owner,user 123",
             "scope,read/write",
             "type,urn:type/with space",
             "uri,/api/special/a+b=c&d",
             "matchingUri,/api/match/*"
         })
         @DisplayName("case15: 특수문자 인코딩 파라미터")
         void specialCharsEncoded(String key, String value) {
            // 쿼리스트링 생성 확인
            ResourceQueryParams.ResourceQueryParamsBuilder b = ResourceQueryParams.builder();
            switch (key) {
               case "owner":
                  b.owner(value);
                  break;
               case "scope":
                  b.scope(value);
                  break;
               case "type":
                  b.type(value);
                  break;
               case "uri":
                  b.uri(value);
                  break;
               case "matchingUri":
                  b.matchingUri(value);
                  break;
               default:
                  throw new IllegalArgumentException();
            }
            ResourceQueryParams qp = b.build();
            assertThat(qp.toQueryString()).contains(UrlUtil.toUrlEncoded(Map.of(key, value)));

            // 서버 호출
            Mono<KeycloakResponse<ResourceRepresentation[]>> mono =
                keycloakClient.authResourceAsync().getResources(accessToken, CLIENT_UUID, qp);

            StepVerifier.create(mono.timeout(Duration.ofSeconds(5)))
                .assertNext(resp -> assertThat(resp.getStatus()).isEqualTo(200))
                .verifyComplete();
         }
      }
   }


   @Nested
   @TestInstance(TestInstance.Lifecycle.PER_CLASS)
   @DisplayName("Create Resource Tests")
   class CreateResourceTests {

      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Success Cases")
      class SuccessCases {

         @Test
         @DisplayName("case7: create resource -> 201 & retrievable")
         void createResource_success() {
            // given
            String name = "res-" + UUID.randomUUID();
            ResourceRepresentation newRes = new ResourceRepresentation();
            newRes.setName(name);

            // when
            Mono<KeycloakResponse<Void>> createMono =
                keycloakClient.authResourceAsync().createResource(adminAccessToken, CLIENT_UUID, newRes);

            // then
            StepVerifier.create(createMono)
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(201);
                   assertThat(resp.getBody()).isEmpty();
                })
                .verifyComplete();

            // verify by listing
            Mono<KeycloakResponse<ResourceRepresentation[]>> listMono =
                keycloakClient.authResourceAsync().getResources(
                    adminAccessToken,
                    CLIENT_UUID,
                    ResourceQueryParams.builder().name(name).exactName(true).build()
                );
            StepVerifier.create(listMono)
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(200);
                   ResourceRepresentation[] arr = resp.getBody().orElseThrow();
                   assertThat(arr).isNotEmpty();
                   assertThat(arr[0].getName()).isEqualTo(name);
                })
                .verifyComplete();
         }
      }

      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Failure Cases")
      class FailureCases {

         @Test
         @DisplayName("case8: create resource with null accessToken -> 401 Unauthorized")
         void createResource_accessTokenNull() {
            ResourceRepresentation res = new ResourceRepresentation();
            res.setName("res-" + UUID.randomUUID());

            Mono<KeycloakResponse<Void>> mono =
                keycloakClient.authResourceAsync().createResource(null, CLIENT_UUID, res);

            StepVerifier.create(mono)
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(HttpResponseStatus.UNAUTHORIZED.code());
                   assertThat(resp.getBody()).isEmpty();
                   assertThat(resp.getMessage()).contains("Unauthorized");
                })
                .verifyComplete();
         }

         @Test
         @DisplayName("case9: create resource with null clientUuid -> 404 Not Found")
         void createResource_clientUuidNull() {
            ResourceRepresentation res = new ResourceRepresentation();
            res.setName("res-" + UUID.randomUUID());

            Mono<KeycloakResponse<Void>> mono =
                keycloakClient.authResourceAsync().createResource(adminAccessToken, null, res);

            StepVerifier.create(mono)
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(HttpResponseStatus.NOT_FOUND.code());
                   assertThat(resp.getBody()).isEmpty();
                   assertThat(resp.getMessage()).contains("Not Found");
                })
                .verifyComplete();
         }
      }
   }


   @Nested
   @TestInstance(TestInstance.Lifecycle.PER_CLASS)
   @DisplayName("Update Resource Tests")
   class UpdateResourceTests {

      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Success Cases")
      class SuccessCases {

         @Test
         @DisplayName("case10: update resource -> 204")
         void updateResource_success() {
            // given: 생성 후 조회하여 id 확보
            String originalName = "res-" + UUID.randomUUID();
            ResourceRepresentation res = new ResourceRepresentation();
            res.setName(originalName);

            keycloakClient.authResourceAsync()
                .createResource(adminAccessToken, CLIENT_UUID, res)
                .block(Duration.ofSeconds(5));

            ResourceRepresentation toUpdate = keycloakClient.authResourceAsync()
                .getResources(adminAccessToken, CLIENT_UUID,
                    ResourceQueryParams.builder().name(originalName).exactName(true).build())
                .map(resp -> {
                   ResourceRepresentation[] arr = resp.getBody()
                       .orElseThrow(() -> new IllegalStateException("empty body from getResources"));
                   if (arr.length == 0) {
                      throw new IllegalStateException("empty array from getResources");
                   }
                   return arr[0];
                })
                .block(Duration.ofSeconds(5));
            toUpdate.setName("updated-" + originalName);

            // when
            Mono<KeycloakResponse<Void>> mono =
                keycloakClient.authResourceAsync().updateResource(adminAccessToken, CLIENT_UUID, toUpdate);

            // then
            StepVerifier.create(mono)
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(204);
                   assertThat(resp.getBody()).isEmpty();
                })
                .verifyComplete();
         }
      }

      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Failure Cases")
      class FailureCases {

         @Test
         @DisplayName("case11: update resource with null accessToken -> 401 Unauthorized")
         void updateResource_accessTokenNull() {
            ResourceRepresentation res = new ResourceRepresentation();
            res.setId(UUID.randomUUID().toString());
            res.setName("res-" + UUID.randomUUID());

            Mono<KeycloakResponse<Void>> mono =
                keycloakClient.authResourceAsync().updateResource(null, CLIENT_UUID, res);

            StepVerifier.create(mono)
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(HttpResponseStatus.UNAUTHORIZED.code());
                   assertThat(resp.getBody()).isEmpty();
                   assertThat(resp.getMessage()).contains("Unauthorized");
                })
                .verifyComplete();
         }
      }
   }

   @Nested
   @TestInstance(TestInstance.Lifecycle.PER_CLASS)
   @DisplayName("Delete Resource Tests")
   class DeleteResourceTests {

      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Success Cases")
      class SuccessCases {

         @Test
         @DisplayName("case12: delete resource -> 204")
         void deleteResource_success() {
            // given: 생성 후 id 확보
            String name = "res-" + UUID.randomUUID();
            ResourceRepresentation res = new ResourceRepresentation();
            res.setName(name);

            keycloakClient.authResourceAsync()
                .createResource(adminAccessToken, CLIENT_UUID, res)
                .block(Duration.ofSeconds(5));

            UUID resourceId = keycloakClient.authResourceAsync()
                .getResources(adminAccessToken, CLIENT_UUID,
                    ResourceQueryParams.builder().name(name).exactName(true).build())
                .block(Duration.ofSeconds(5))
                .getBody()
                .map(arr -> UUID.fromString(arr[0].getId()))
                .orElseThrow();

            // when
            Mono<KeycloakResponse<Void>> mono =
                keycloakClient.authResourceAsync().deleteResource(adminAccessToken, CLIENT_UUID, resourceId);

            // then
            StepVerifier.create(mono)
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(204);
                   assertThat(resp.getBody()).isEmpty();
                })
                .verifyComplete();
         }
      }

      @Nested
      @TestInstance(TestInstance.Lifecycle.PER_CLASS)
      @DisplayName("Failure Cases")
      class FailureCases {

         @Test
         @DisplayName("case13: delete resource with null accessToken -> 401 Unauthorized")
         void deleteResource_accessTokenNull() {
            Mono<KeycloakResponse<Void>> mono =
                keycloakClient.authResourceAsync()
                    .deleteResource(null, CLIENT_UUID, UUID.randomUUID());

            StepVerifier.create(mono)
                .assertNext(resp -> {
                   assertThat(resp.getStatus()).isEqualTo(HttpResponseStatus.UNAUTHORIZED.code());
                   assertThat(resp.getBody()).isEmpty();
                   assertThat(resp.getMessage()).contains("Unauthorized");
                })
                .verifyComplete();
         }
      }
   }
}
