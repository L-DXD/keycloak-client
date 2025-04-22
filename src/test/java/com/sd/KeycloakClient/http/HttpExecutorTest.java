package com.sd.KeycloakClient.http;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED;
import static org.assertj.core.api.Assertions.assertThat;

import com.sd.KeycloakClient.annotation.MockWebServer;
import com.sd.KeycloakClient.config.ClientConfiguration;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.auth.KeycloakAuthorizationResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.HashMap;
import java.util.Map;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * @author SangWonYu
 * @date 2025-04
 */
@MockWebServer
class HttpExecutorTest {

   private okhttp3.mockwebserver.MockWebServer server;
   private ClientConfiguration config;

   @BeforeEach
   void setUp() {
      config = ClientConfiguration.builder()
          .realmName("test realm")
          .clientId("client id")
          .redirectUri("redirect url")
          .baseUrl("base-url")
          .logoutRedirectUri("logout-redirect")
          .responseType("code")
          .clientSecret("secret code")
          .build();
      ;
   }

   @Test
   @DisplayName("case1. Successful POST request")
   void postRequestTest() {
      // Given
      Map<String, Object> body = Map.of("username", "test", "password", "secret");
      String responseJson = "{\"accessToken\":\"abc123\"}";
      server.enqueue(new MockResponse()
          .setBody(responseJson)
          .addHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString())
          .setResponseCode(HttpResponseStatus.OK.code()));
      String url = server.url("/token").toString();

      // When
      HttpExecutor<Object> executor = new HttpExecutor<>(config, HttpMethod.POST, url);
      Mono<KeycloakResponse<Object>> responseMono = executor
          .applicationJson()
          .entities(body)
          .responseType(Object.class)
          .send();

      // Then
      StepVerifier.create(responseMono)
          .assertNext(res -> {
             assertThat(HttpResponseStatus.OK.code()).isEqualTo(res.getStatus());
             assertThat(res.getBody()).isNotNull();
             assertThat(((Map<String, String>) res.getBody()).get("accessToken")).isEqualTo("abc123");
          })
          .verifyComplete();
   }

   @Test
   @DisplayName("case2. Json parsing error")
   void jsonParsingErrorTest() {
      // Given
      server.enqueue(new MockResponse()
          .setResponseCode(HttpResponseStatus.OK.code())
          .setBody("{{ not-json }}"));
      String url = server.url("/bad-json").toString();

      Mono<KeycloakResponse<KeycloakAuthorizationResponse>> send = new HttpExecutor<KeycloakAuthorizationResponse>(config, HttpMethod.GET,
          url)
          .applicationJson()
          .responseType(KeycloakAuthorizationResponse.class)
          .send();

      // When & Then
      StepVerifier.create(send)
          .expectErrorMatches(throwable -> throwable instanceof RuntimeException
              && throwable.getMessage().contains("Error parsing JSON"))
          .verify();
   }

   @Test
   @DisplayName("case3. Status 204, 403, 404, 500 - return no Body")
   void status204Test() {
      // Given
      server.enqueue(new MockResponse()
          .setResponseCode(HttpResponseStatus.NO_CONTENT.code()));
      server.enqueue(new MockResponse()
          .setResponseCode(HttpResponseStatus.NOT_FOUND.code()));
      server.enqueue(new MockResponse()
          .setResponseCode(HttpResponseStatus.FORBIDDEN.code()));
      server.enqueue(new MockResponse()
          .setResponseCode(HttpResponseStatus.METHOD_NOT_ALLOWED.code()));
      server.enqueue(new MockResponse()
          .setResponseCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()));
      String url = server.url("/change-password").toString();
      Flux<KeycloakResponse<Object>> responseFlux = Flux.concat(
          new HttpExecutor<>(config, HttpMethod.POST, url).applicationJson().send(),
          new HttpExecutor<>(config, HttpMethod.POST, url).applicationJson().send(),
          new HttpExecutor<>(config, HttpMethod.POST, url).applicationJson().send(),
          new HttpExecutor<>(config, HttpMethod.POST, url).applicationJson().send(),
          new HttpExecutor<>(config, HttpMethod.POST, url).applicationJson().send()
      );

      // When & Then
      StepVerifier.create(responseFlux)
          .assertNext((res) -> {
             assertThat(res.getStatus()).isEqualTo(HttpResponseStatus.NO_CONTENT.code());
             assertThat(res.getBody()).isNull();
          })
          .assertNext((res) -> {
             assertThat(res.getStatus()).isEqualTo(HttpResponseStatus.NOT_FOUND.code());
             assertThat(res.getBody()).isNull();
          })
          .assertNext((res) -> {
             assertThat(res.getStatus()).isEqualTo(HttpResponseStatus.FORBIDDEN.code());
             assertThat(res.getBody()).isNull();
          })
          .assertNext((res) -> {
             assertThat(res.getStatus()).isEqualTo(HttpResponseStatus.METHOD_NOT_ALLOWED.code());
             assertThat(res.getBody()).isNull();
          })
          .assertNext((res) -> {
             assertThat(res.getStatus()).isEqualTo(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
             assertThat(res.getBody()).isNull();
          })
          .verifyComplete();
   }

   @Test
   @DisplayName("case4. application-form-urlencoded is not json but url encoded form")
   void applicationFormUrlencodedTest() throws InterruptedException {
      // given
      server.enqueue(new MockResponse()
          .setBody("{\"result\": \"ok\"}")
          .setHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString())
          .setResponseCode(200));
      String url = server.url("/token").toString();
      Map<String, Object> entities = new HashMap<>();
      entities.put("accessToken", "abc123");
      entities.put("idToken", "def456");

      // when
      Mono<KeycloakResponse<Object>> send = new HttpExecutor<>(config, HttpMethod.POST, url)
          .applicationFormUrlencoded()
          .entities(entities)
          .send();

      // then
      StepVerifier.create(send)
          .assertNext(res -> {
             assertThat(res.getStatus()).isEqualTo(HttpResponseStatus.OK.code());
          }).verifyComplete();

      RecordedRequest request = server.takeRequest();
      assertThat(request.getMethod()).isEqualTo(HttpMethod.POST.name());
      assertThat(request.getHeader(CONTENT_TYPE.toString())).isEqualTo(APPLICATION_X_WWW_FORM_URLENCODED.toString());
      assertThat(request.getBody().readUtf8()).isEqualTo("idToken=def456&accessToken=abc123");
      assertThat(request.getBody()).isNotEqualTo("{");
   }

}