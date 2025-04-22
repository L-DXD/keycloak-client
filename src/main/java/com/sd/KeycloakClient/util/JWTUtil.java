package com.sd.KeycloakClient.util;

import static org.keycloak.TokenVerifier.IS_ACTIVE;

import com.sd.KeycloakClient.config.ClientConfiguration;
import com.sd.KeycloakClient.dto.auth.KeycloakJwksResponse;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.jose.jws.JWSHeader;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.JsonWebToken;
import org.keycloak.representations.RefreshToken;
import reactor.core.publisher.Mono;

public class JWTUtil {

   public static Mono<JsonWebToken> verify(String token, RSAPublicKey publicKey, ClientConfiguration config) {
      try {
         TokenVerifier<JsonWebToken> verifier = TokenVerifier.create(token, JsonWebToken.class)
             .withChecks(IS_ACTIVE, new TokenVerifier.AudienceCheck(config.getClientId()),
                 new TokenVerifier.IssuedForCheck(config.getClientId()))
             .publicKey(publicKey);

         return Mono.just(verifier.verify()
             .getToken());
      } catch (RuntimeException | VerificationException e) {
         return Mono.error(e);
      }
   }

   public static Mono<String> getTokenKid(String token) {
      try {
         TokenVerifier<JsonWebToken> verifier = TokenVerifier.create(token, JsonWebToken.class);
         JWSHeader header = verifier.getHeader();
         return Mono.just(header.getKeyId());
      } catch (Exception e) {
         return Mono.error(e);
      }
   }

   public static Mono<AccessToken> getAccessToken(String accessToken) {
      try {
         TokenVerifier<AccessToken> verifier = TokenVerifier.create(accessToken, AccessToken.class);
         return Mono.just(verifier.getToken());
      } catch (Exception e) {
         return Mono.error(e);
      }
   }

   public static Mono<IDToken> getIdToken(String idToken) {
      try {
         TokenVerifier<IDToken> verifier = TokenVerifier.create(idToken, IDToken.class);
         return Mono.just(verifier.getToken());
      } catch (Exception e) {
         return Mono.error(e);
      }
   }

   public static Mono<RefreshToken> getRefreshToken(String idToken) {
      try {
         TokenVerifier<RefreshToken> verifier = TokenVerifier.create(idToken, RefreshToken.class);
         return Mono.just(verifier.getToken());
      } catch (Exception e) {
         return Mono.error(e);
      }
   }

   public static Mono<KeycloakJwksResponse> getMatchedJwks(String kid, List<KeycloakJwksResponse> jwks) {
      Optional<KeycloakJwksResponse> responseJwks = jwks.stream()
          .filter((key) -> key.getKid().equals(kid))
          .findFirst();
      return responseJwks.map(Mono::just)
          .orElseGet(() -> Mono.error(new NoSuchElementException("There is no KID that matches.")));

   }

   public static Mono<RSAPublicKey> makePublicKey(KeycloakJwksResponse jwks) {
      try {
         byte[] nBytes = Base64.getUrlDecoder().decode(jwks.getN());
         byte[] eBytes = Base64.getUrlDecoder().decode(jwks.getE());

         BigInteger modulus = new BigInteger(1, nBytes);
         BigInteger exponent = new BigInteger(1, eBytes);

         KeyFactory keyFactory = KeyFactory.getInstance("RSA");
         RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);
         return Mono.just((RSAPublicKey) keyFactory.generatePublic(keySpec));
      } catch (Exception e) {
         return Mono.error(e);
      }

   }
}
