package com.sd.KeycloakClient.client.auth.async.impl;

import static com.sd.KeycloakClient.constants.TestConstants.E;
import static com.sd.KeycloakClient.constants.TestConstants.EXPIRED_TOKEN;
import static com.sd.KeycloakClient.constants.TestConstants.KID;
import static com.sd.KeycloakClient.constants.TestConstants.N;
import static com.sd.KeycloakClient.util.JWTUtil.makePublicKey;
import static org.assertj.core.api.Assertions.assertThat;

import com.sd.KeycloakClient.annotation.MockKeycloakClient;
import com.sd.KeycloakClient.common.KeycloakShareTestContainer;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.auth.KeycloakAuthorizationResult;
import com.sd.KeycloakClient.dto.auth.KeycloakIntrospectResponse;
import com.sd.KeycloakClient.dto.auth.KeycloakJwksResponse;
import com.sd.KeycloakClient.dto.auth.KeycloakTokenInfo;
import com.sd.KeycloakClient.dto.auth.VerifyTokenResult;
import com.sd.KeycloakClient.factory.KeycloakClient;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.security.interfaces.RSAPublicKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * @author SangWonYu
 * @date 2025-04
 */

@MockKeycloakClient
class KeycloakAuthAsyncClientImplTest extends KeycloakShareTestContainer {

   private KeycloakClient keycloakClient;

   @Test
   @DisplayName("case1. Incorrect or Correct username or password")
   void basicAuthTest() {
      // given
      Flux<KeycloakResponse<KeycloakTokenInfo>> basicAuth = Flux.concat(
          keycloakClient.authAsync().basicAuth("test", "asd4521321@"),
          keycloakClient.authAsync().basicAuth("test-user-keycloak", "1234")
      );

      // when & then
      StepVerifier.create(basicAuth)
          .assertNext(response -> {
             assertThat(HttpResponseStatus.UNAUTHORIZED.code()).isEqualTo(response.getStatus());
             assertThat(response.getMessage()).contains("Invalid user credentials");
          })
          .assertNext(response -> {
             assertThat(HttpResponseStatus.OK.code()).isEqualTo(response.getStatus());
             assertThat(response.getBody()).isPresent();
             KeycloakTokenInfo body = response.getBody().get();

             assertThat(body.getTokenType()).isEqualTo("Bearer");
             assertThat(body.getAccessToken()).isNotNull();
             assertThat(body.getRefreshToken()).isNotNull();
          })
          .verifyComplete();
   }

   @Test
   @DisplayName("case2. token issue test based on invalid code")
   void issueTokenTest() {
      // given
      Mono<KeycloakResponse<KeycloakTokenInfo>> keycloakResponseMono = keycloakClient.authAsync().issueToken("invalid code");

      // when & then
      StepVerifier.create(keycloakResponseMono)
          .assertNext(response -> {
             assertThat(HttpResponseStatus.BAD_REQUEST.code()).isEqualTo(response.getStatus());
             assertThat(response.getMessage()).contains("Code not valid");
          })
          .verifyComplete();
   }

   @Test
   @DisplayName("case3. logout : invalid refresh token")
   void logoutInvalidTokenTest() {
      // given
      Mono<KeycloakResponse<Object>> logout = keycloakClient.authAsync().logout("invalidToken");

      // when && then
      StepVerifier.create(logout)
          .assertNext((response) -> {
             assertThat(response.getStatus()).isEqualTo(HttpResponseStatus.BAD_REQUEST.code());
             assertThat(response.getMessage()).contains("Invalid refresh token");
          })
          .verifyComplete();
   }

   @Test
   @DisplayName("case4. logout : null refresh token")
   void logoutNullTokenTest() {
      // given
      Mono<KeycloakResponse<Object>> logout = keycloakClient.authAsync().logout(null);

      // when && then
      StepVerifier.create(logout)
          .expectErrorMatches(throwable ->
              throwable instanceof Exception && throwable.getMessage().equals("NO REFRESH TOKEN"))
          .verify();
   }

   @Test
   @DisplayName("case5. reissue : invalid refresh token")
   void reissueInvalidTokenTest() {
      // given
      Mono<KeycloakResponse<KeycloakTokenInfo>> reissue = keycloakClient.authAsync().reissueToken("invalidToken");

      // when && then
      StepVerifier.create(reissue)
          .assertNext((response) -> {
             assertThat(response.getStatus()).isEqualTo(HttpResponseStatus.BAD_REQUEST.code());
             assertThat(response.getMessage()).contains("Invalid refresh token");
          })
          .verifyComplete();
   }

   @Test
   @DisplayName("case6. reissue : null refresh token")
   void reissueNullTokenTest() {
      // given
      Mono<KeycloakResponse<KeycloakTokenInfo>> reissue = keycloakClient.authAsync().reissueToken(null);

      // when && then
      StepVerifier.create(reissue)
          .expectErrorMatches(throwable ->
              throwable instanceof Exception && throwable.getMessage().equals("NO REFRESH TOKEN"))
          .verify();
   }

   @Test
   @DisplayName("case7. authenticate invalid token")
   void authenticateInvalidTokenTest() {
      // given
      Mono<KeycloakResponse<KeycloakIntrospectResponse>> invalidToken = keycloakClient.authAsync()
          .authenticationByIntrospect("invalid token");

      // when && then
      StepVerifier.create(invalidToken)
          .assertNext((response) -> {
             assertThat(response.getStatus()).isEqualTo(HttpResponseStatus.OK.code());
             assertThat(response.getBody()).isPresent();

             KeycloakIntrospectResponse body = response.getBody().get();
             assertThat(body.getActive()).isFalse();
          })
          .verifyComplete();
   }

   @Test
   @DisplayName("case8. authorize invalid token")
   void getPublic() {
      // given
      Mono<KeycloakResponse<RSAPublicKey>> publicKey = keycloakClient.authAsync()
          .getPublicKey("invalid.test.token");

      // when && then
      StepVerifier.create(publicKey)
          .assertNext((response) -> {
             assertThat(response.getStatus()).isEqualTo(HttpResponseStatus.BAD_REQUEST.code());
             assertThat(response.getBody()).isEmpty();
          })
          .verifyComplete();
   }

   @Test
   @DisplayName("case9. authorize invalid token")
   void authorizeInvalidTokenTest() {
      // given
      Mono<KeycloakResponse<KeycloakAuthorizationResult>> authorization = keycloakClient.authAsync()
          .authorization("invalid token", "/test", "GET");

      // when && then
      StepVerifier.create(authorization)
          .assertNext((response) -> {
             assertThat(response.getStatus()).isEqualTo(HttpResponseStatus.UNAUTHORIZED.code());
             assertThat(response.getMessage()).contains("Unauthorized");
             assertThat(response.getBody()).isPresent();

             KeycloakAuthorizationResult body = response.getBody().get();
             assertThat(body.isGranted()).isFalse();
          })
          .verifyComplete();
   }

   @Test
   @DisplayName("case10. offline authentication")
   void authenticationByOffline() {
      // given
      KeycloakJwksResponse jwks = KeycloakJwksResponse.builder()
          .kid(KID)
          .kty("RSA")
          .alg("RS256")
          .use("sig")
          .n(N)
          .e(E)
          .build();

      // when && then
      StepVerifier.create(
          makePublicKey(jwks)
              .flatMap(publicKey -> keycloakClient.authAsync().authenticationByOffline(EXPIRED_TOKEN, publicKey))
      ).assertNext(response -> {
         assertThat(response.getStatus()).isEqualTo(HttpResponseStatus.UNAUTHORIZED.code());
         assertThat(response.getMessage()).isEqualTo("Token is not active");
         assertThat(response.getBody()).isPresent();

         VerifyTokenResult body = response.getBody().get();
         assertThat(body.getActive()).isFalse();
         assertThat(body.getToken()).isNull();
      }).verifyComplete();
   }

}