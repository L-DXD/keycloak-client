package com.sd.KeycloakClient.util;

import static com.sd.KeycloakClient.util.JWTUtil.getAccessToken;
import static com.sd.KeycloakClient.util.JWTUtil.getMatchedJwks;
import static com.sd.KeycloakClient.util.JWTUtil.getTokenKid;
import static com.sd.KeycloakClient.util.JWTUtil.makePublicKey;
import static com.sd.KeycloakClient.util.JWTUtil.verify;
import static org.assertj.core.api.Assertions.assertThat;

import com.sd.KeycloakClient.config.ClientConfiguration;
import com.sd.KeycloakClient.dto.auth.KeycloakJwksResponse;
import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.common.VerificationException;
import org.keycloak.exceptions.TokenNotActiveException;
import org.keycloak.exceptions.TokenSignatureInvalidException;
import reactor.test.StepVerifier;

/**
 * @author SangWonYu
 * @date 2025-04
 */
class JWTUtilTest {

   private static final String EXPIRED_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJXZGlDdEZMdi11ZXcxblR1SW5RU0JjVUMyVVRfM3ppSzRlTU83YkNJRTRZIn0.eyJleHAiOjE3NDUyMjE0MTgsImlhdCI6MTc0NTIxOTYxOCwiYXV0aF90aW1lIjoxNzQ1MjE5NjE4LCJqdGkiOiI0OTAzMzFmNy03N2E3LTQzMDItOTRlZC1kZGYzMjY1ZTBhYWQiLCJpc3MiOiJodHRwczovL2F1dGhkZXYuZGFld29vbmcuY28ua3Iva2V5Y2xvYWsvcmVhbG1zL2JhY2tvZmZpY2Utc3NvIiwiYXVkIjoicHJtLWNsaWVudCIsInN1YiI6ImE0M2VhMjFlLTUzMzctNGZlNS1hNmExLWYzNzllMTc0OWI5ZSIsInR5cCI6IklEIiwiYXpwIjoicHJtLWNsaWVudCIsIm5vbmNlIjoiTmJ4a2tiTUdGMkI1Z1VmRmIyLWQ1WVlNakhMUTk4TUhudC1pUUFKWjgwUSIsInNpZCI6IjQ4NTA2YzE4LTc4NmQtNGIwYy1iZTQxLTMwNmZiZGZjMGM2ZSIsImF0X2hhc2giOiJvclZvMlhacUg0RlVTdFZnX2Y5aTRBIiwiYWNyIjoiMCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicHJlZmVycmVkX3VzZXJuYW1lIjoiMDIxMTAzOTQiLCJ1c2VySWQiOiIwMjExMDM5NCIsImVtYWlsIjoid2ViYWRtaW5AaWRzdHJ1c3QuY29tIn0.l_X4rDifnBL8dvjZaZkWoeYFKmlfBXWoEsJnKbNtjnyYB7RlmoZKZ94gsVHTTOSzboifJ1_us8CbxBUtc1lrWuLiZBQRrOt6gRYJ6kiCDgm1KGRWwtsSCQ7fFhFMc4uFeGmw9lIWqdjPCfOVkzCLCq4H6MdTyRQhLQp_IM7B0a9gebadd65O4BW-nb9VvtkLs2ScJQqw1uNvOQVpDq9D8lFCXRjN9cVDbLCKADv5mY4oOyTo79l3FeodkS22Drlwz25xcE-UqoxTwwbyFEXaEEli9y9Tsm1wTsNPqHqyPItgrbuSrSecY3lhv0sNQj9vIBtLO6PCvG8WZaIiexyyFw";
   private static final String INVALID_TOKEN = "invalid token";
   private static final String E = "AQAB";
   private static final String KID = "WdiCtFLv-uew1nTuInQSBcUC2UT_3ziK4eMO7bCIE4Y";
   private static final String N = "11S3i-raeMymm81jUVL115qMS7VnrJ_5ABUlRmaTzgG-1VjdIXS6vF1kxpT1ONbiZ94Gm7a-hEtvLWQsqweaR9q05K8k7XaytnX2fUKk2BPmLULI30cdnE0voDbaOPBUakx8Qwr7HLQdTCOlMv06Zc_v9GLaWqj6zVvLZzfybHZ1AgFIBQXFKlDT4RqltVn3KXpqh2RpspW9GoVB4fnbzCbJvy3jxPvd7adWY-EX5C-gapyD7JM5Cgppsz_Qu-3GtJjMlUivK5OnhE01ww_TawLCLqHEe1j9TeH4FsDW_Zob4dMIYep9zg3d0MiEFverOVh7HOyurqc55SLIfvAR8Q";
   private static final String INVALID_N = "31S6i-raeMymm81jUVL315qMS7VnrJ_5ABUlRmaTzgG-1VjdIXS6vF1kxpT1ONbiZ94Gm7a-hEtvLWQsqweaR9q05K8k7XaytnX2fUKk2BPmLULI30cdnE0voDbaOPBUakx8Qwr7HLQdTCOlMv06Zc_v9GLaWqj6zVvLZzfybHZ1AgFIBQXFKlDT4RqltVn3KXpqh2RpspW9GoVB4fnbzCbJvy3jxPvd7adWY-EX5C-gapyD7JM5Cgppsz_Qu-3GtJjMlUivK5OnhE01ww_TawLCLqHEe1j9TeH4FsDW_Zob4dMIYep9zg3d0MiEFverOVh7HOyurqc55SLIfvAR1Q";

   @Test
   @DisplayName("case1. Get token in expired token")
   void tokenVerify() {
      // when & then
      StepVerifier.create(getAccessToken(EXPIRED_TOKEN))
          .assertNext(token -> {
             assertThat(token.isExpired()).isTrue();
             assertThat(token.getPreferredUsername()).isEqualTo("02110394");
             assertThat(token.getType()).isEqualTo("ID");
             assertThat(token.getEmail()).isNotNull();
          }).verifyComplete();
   }

   @Test
   @DisplayName("case2. Invalid token")
   void invalidTokenVerify() {
      // when & then
      StepVerifier.create(getAccessToken(INVALID_TOKEN))
          .expectErrorMatches(
              throwable -> throwable instanceof VerificationException && throwable.getMessage().equals("Failed to parse JWT"))
          .verify();
   }

   @Test
   @DisplayName("case3. Get Kid")
   void tokenKid() {
      // when & then
      StepVerifier.create(getTokenKid(EXPIRED_TOKEN))
          .assertNext(kid -> {
             assertThat(kid).isNotNull();
             assertThat(kid).isEqualTo(KID);
          }).verifyComplete();
   }

   @Test
   @DisplayName("case4. Get kid through invalid token")
   void invalidTokenKid() {
      // when & then
      StepVerifier.create(getTokenKid(INVALID_TOKEN))
          .expectErrorMatches(
              throwable -> throwable instanceof VerificationException && throwable.getMessage().equals("Failed to parse JWT"))
          .verify();
   }

   @Test
   @DisplayName("case5. Get matched Kid")
   void matchedJwks() {

      // given
      List<KeycloakJwksResponse> keycloakJwksResponses = List.of(KeycloakJwksResponse.builder()
          .kid(KID)
          .alg("RS256")
          .build());

      // when & then
      StepVerifier.create(getMatchedJwks(KID, keycloakJwksResponses))
          .assertNext(jwks -> {
             assertThat(jwks.getAlg()).isEqualTo("RS256");
             assertThat(jwks.getKid()).isEqualTo(KID);
          }).verifyComplete();
   }

   @Test
   @DisplayName("case6. Error not matched Kid")
   void notMatchedJwks() {

      // given
      List<KeycloakJwksResponse> keycloakJwksResponses = List.of(KeycloakJwksResponse.builder()
          .kid("test kid")
          .alg("RS256")
          .build());

      // when & then
      StepVerifier.create(getMatchedJwks(KID, keycloakJwksResponses))
          .expectErrorMatches(
              throwable -> throwable instanceof NoSuchElementException && throwable.getMessage().equals("There is no KID that matches."))
          .verify();
   }


   @Test
   @DisplayName("case7. Make public key")
   void makePublicKeyTest() {
      // given
      KeycloakJwksResponse jwks = KeycloakJwksResponse.builder()
          .kid(KID)
          .kty("RSA")
          .alg("RS256")
          .use("sig")
          .n(N)
          .e(E)
          .build();

      // when & then
      StepVerifier.create(makePublicKey(jwks))
          .assertNext(publicKey -> {
             assertThat(publicKey).isNotNull();
             assertThat(publicKey.getAlgorithm()).isEqualTo("RSA");
             assertThat(publicKey).isInstanceOf(RSAPublicKey.class);
             assertThat(publicKey.getPublicExponent()).isEqualTo(new BigInteger(1, Base64.getUrlDecoder().decode(jwks.getE())));
             assertThat(publicKey.getModulus()).isEqualTo(new BigInteger(1, Base64.getUrlDecoder().decode(jwks.getN())));
          }).verifyComplete();
   }

   @Test
   @DisplayName("case8. Invalid element test")
   void invalidN() {
      // given
      KeycloakJwksResponse jwks = KeycloakJwksResponse.builder()
          .kid(KID)
          .kty("TT")
          .alg("TT")
          .use("TT")
          .n("Test")
          .e("TT")
          .build();

      // when & then
      StepVerifier.create(makePublicKey(jwks))
          .expectError(InvalidKeySpecException.class)
          .verify();
   }

   @Test
   @DisplayName("case9. Invalid Signature test")
   void invalidSignature() {
      // given
      KeycloakJwksResponse jwks = KeycloakJwksResponse.builder()
          .kid(KID)
          .kty("RSA")
          .alg("RS256")
          .use("sig")
          .n(INVALID_N)
          .e(E)
          .build();

      ClientConfiguration config = ClientConfiguration.builder()
          .clientId("client-id")
          .build();

      // when & then
      StepVerifier.create(
              makePublicKey(jwks)
                  .flatMap(publicKey -> verify(EXPIRED_TOKEN, publicKey, config))
          )
          .expectErrorMatches(
              throwable -> throwable instanceof TokenSignatureInvalidException && throwable.getMessage().equals("Invalid token signature"))
          .verify();
   }

   @Test
   @DisplayName("case10. public key is null")
   void publicKeyNull() {
      // given
      ClientConfiguration config = ClientConfiguration.builder()
          .clientId("prm-client")
          .build();

      // when & then
      StepVerifier.create(verify(EXPIRED_TOKEN, null, config)
          )
          .expectErrorMatches(
              throwable -> throwable instanceof VerificationException && throwable.getMessage().equals("Public key not set"))
          .verify();
   }

   @Test
   @DisplayName("case11. Inactive verify test")
   void inactiveVerify() {
      // given
      KeycloakJwksResponse jwks = KeycloakJwksResponse.builder()
          .kid(KID)
          .kty("RSA")
          .alg("RS256")
          .use("sig")
          .n(N)
          .e(E)
          .build();

      ClientConfiguration config = ClientConfiguration.builder()
          .clientId("prm-client")
          .build();

      // when & then
      StepVerifier.create(
              makePublicKey(jwks)
                  .flatMap(publicKey -> verify(EXPIRED_TOKEN, publicKey, config))
          ).expectErrorMatches(
              throwable -> throwable instanceof TokenNotActiveException && throwable.getMessage()
                  .equals("Token is not active"))
          .verify();

   }
}