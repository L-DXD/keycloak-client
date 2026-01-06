package com.sd.KeycloakClient.client.auth.async.impl;


import static com.sd.KeycloakClient.http.KeycloakEntities.authenticationEntities;
import static com.sd.KeycloakClient.http.KeycloakEntities.authorizationEntities;
import static com.sd.KeycloakClient.http.KeycloakEntities.clientCredentialEntities;
import static com.sd.KeycloakClient.http.KeycloakEntities.exchangeTokenEntities;
import static com.sd.KeycloakClient.http.KeycloakEntities.logoutEntities;
import static com.sd.KeycloakClient.http.KeycloakEntities.passwordEntities;
import static com.sd.KeycloakClient.http.KeycloakEntities.refreshTokenEntities;
import static com.sd.KeycloakClient.http.KeycloakEntities.tokenEntities;
import static com.sd.KeycloakClient.util.JWTUtil.getMatchedJwks;
import static com.sd.KeycloakClient.util.JWTUtil.getTokenKid;
import static com.sd.KeycloakClient.util.JWTUtil.verify;
import static com.sd.KeycloakClient.util.UrlUtil.toPermissionFormat;

import com.sd.KeycloakClient.client.auth.async.KeycloakAuthAsyncClient;
import com.sd.KeycloakClient.config.ClientConfiguration;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.auth.KeycloakAuthorizationResponse;
import com.sd.KeycloakClient.dto.auth.KeycloakAuthorizationResult;
import com.sd.KeycloakClient.dto.auth.KeycloakClientTokenInfo;
import com.sd.KeycloakClient.dto.auth.KeycloakExchangeTokenInfo;
import com.sd.KeycloakClient.dto.auth.KeycloakIntrospectResponse;
import com.sd.KeycloakClient.dto.auth.KeycloakJwksKeys;
import com.sd.KeycloakClient.dto.auth.KeycloakTokenInfo;
import com.sd.KeycloakClient.dto.auth.VerifyTokenResult;
import com.sd.KeycloakClient.http.Http;
import com.sd.KeycloakClient.util.JWTUtil;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.Objects;
import reactor.core.publisher.Mono;

/**
 * @author SangWonYu
 * @date 2025-04
 * @description The KeycloakAuthAsyncClientImpl provides various authentication features. basic auth, logout, reissue etc... For reference,
 * the authorization() method offers authorization capabilities based on resources. Accordingly, it returns the result  of the authorization
 * process using Keycloak's authorization policies based on the URI and scope.
 */

public class KeycloakAuthAsyncClientImpl implements KeycloakAuthAsyncClient {

   private final ClientConfiguration configuration;
   private final Http http;

   public KeycloakAuthAsyncClientImpl(ClientConfiguration configuration) {
      this.configuration = configuration;
      this.http = new Http(configuration);
   }

   @Override
   public Mono<KeycloakResponse<KeycloakTokenInfo>> basicAuth(String username, String password) {
      Map<String, Object> entities = passwordEntities(username, password);

      return http.<KeycloakTokenInfo>post(configuration.getTokenUrl())
          .applicationFormUrlencoded()
          .authorizationBasic(configuration.getClientId(), configuration.getClientSecret())
          .entities(entities)
          .responseType(KeycloakTokenInfo.class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<KeycloakTokenInfo>> issueToken(String code) {
      Map<String, Object> entities = tokenEntities(configuration.getClientId(), configuration.getClientSecret(),
          configuration.getRedirectUri(), code);

      return http.<KeycloakTokenInfo>post(configuration.getTokenUrl())
          .applicationFormUrlencoded()
          .entities(entities)
          .responseType(KeycloakTokenInfo.class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<Object>> logout(String refreshToken) {
      if (Objects.isNull(refreshToken)) {
         return Mono.error(new Exception("NO REFRESH TOKEN"));
      }

      Map<String, Object> entities = logoutEntities(configuration.getClientId(), configuration.getClientSecret(), refreshToken);
      return http.post(configuration.getLogoutUrl())
          .applicationFormUrlencoded()
          .entities(entities)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<KeycloakTokenInfo>> reissueToken(String refreshToken) {
      if (Objects.isNull(refreshToken)) {
         return Mono.error(new Exception("NO REFRESH TOKEN"));
      }

      Map<String, Object> entities = refreshTokenEntities(configuration.getClientId(), configuration.getClientSecret(), refreshToken);
      return http.<KeycloakTokenInfo>post(configuration.getTokenUrl())
          .applicationFormUrlencoded()
          .entities(entities)
          .responseType(KeycloakTokenInfo.class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<KeycloakIntrospectResponse>> authenticationByIntrospect(String token) {
      Map<String, Object> entities = authenticationEntities(configuration.getClientId(), configuration.getClientSecret(), token);
      return http.<KeycloakIntrospectResponse>post(configuration.getIntrospectionUrl())
          .applicationFormUrlencoded()
          .entities(entities)
          .responseType(KeycloakIntrospectResponse.class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<KeycloakClientTokenInfo>> openIdConnectClientToken() {
      Map<String, Object> entities = clientCredentialEntities(configuration.getClientId(), configuration.getClientSecret());
      return http.<KeycloakClientTokenInfo>post(configuration.getOpenIdConnectTokenUrl())
          .applicationFormUrlencoded()
          .entities(entities)
          .responseType(KeycloakClientTokenInfo.class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<KeycloakExchangeTokenInfo>> openIdConnectExchangeToken(String requestedSubject, String subjectToken) {
      Map<String, Object> entities = exchangeTokenEntities(configuration.getClientId(), configuration.getClientSecret(), requestedSubject,
          subjectToken);
      return http.<KeycloakExchangeTokenInfo>post(configuration.getOpenIdConnectTokenUrl())
          .applicationFormUrlencoded()
          .entities(entities)
          .responseType(KeycloakExchangeTokenInfo.class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<VerifyTokenResult>> authenticationByOffline(String token, RSAPublicKey publicKey) {
      return verify(token, publicKey, configuration)
          .map(jwt -> KeycloakResponse.of(HttpResponseStatus.OK.code(), "SUCCESS", VerifyTokenResult.of(true, jwt), null))
          .onErrorResume(throwable -> Mono.just(
              KeycloakResponse.of(
                  HttpResponseStatus.UNAUTHORIZED.code(),
                  throwable.getMessage(),
                  VerifyTokenResult.of(false, null),
                  null
              )));
   }

   @Override
   public Mono<KeycloakResponse<RSAPublicKey>> getPublicKey(String token) {
      return http.<KeycloakJwksKeys>get(configuration.getJwksUrl())
          .responseType(KeycloakJwksKeys.class)
          .send()
          .flatMap((response) -> Mono.justOrEmpty(response.getBody()))
          .flatMap((body) -> getTokenKid(token)
              .flatMap((kid) -> getMatchedJwks(kid, body.getKeys()))
              .flatMap(JWTUtil::makePublicKey)
              .flatMap((key) -> Mono.just(KeycloakResponse.of(HttpResponseStatus.OK.code(), "SUCCESS", key, null))))
          .onErrorResume(
              (throwable -> Mono.just(KeycloakResponse.of(HttpResponseStatus.BAD_REQUEST.code(), throwable.getMessage(), null, null))));
   }

   @Override
   public Mono<KeycloakResponse<KeycloakAuthorizationResult>> authorization(String accessToken, String endpoint, String scope) {
      Map<String, Object> entities = authorizationEntities(configuration.getClientId(), toPermissionFormat(endpoint, scope));

      return http.<KeycloakAuthorizationResponse>post(configuration.getTokenUrl())
          .applicationFormUrlencoded()
          .authorizationBearer(accessToken)
          .entities(entities)
          .responseType(KeycloakAuthorizationResponse.class)
          .send()
          .flatMap((response) -> Mono.just(
              KeycloakResponse.of(response.getStatus(), response.getMessage(), KeycloakAuthorizationResult.builder()
                      .granted(HttpResponseStatus.OK.code() == response.getStatus())
                      .authorizationResponse(response.getBody().orElse(null))
                      .build(),
                  null)));
   }


}
