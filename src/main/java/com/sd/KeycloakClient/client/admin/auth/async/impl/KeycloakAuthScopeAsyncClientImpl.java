package com.sd.KeycloakClient.client.admin.auth.async.impl;

import com.sd.KeycloakClient.client.admin.auth.async.KeycloakAuthScopeAsyncClient;
import com.sd.KeycloakClient.config.ClientConfiguration;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.ScopeQueryParams;
import com.sd.KeycloakClient.http.Http;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.UUID;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import reactor.core.publisher.Mono;

public class KeycloakAuthScopeAsyncClientImpl implements KeycloakAuthScopeAsyncClient {

   private final ClientConfiguration configuration;
   private final Http http;

   public KeycloakAuthScopeAsyncClientImpl(ClientConfiguration configuration) {
      this.configuration = configuration;
      this.http = new Http(configuration);
   }

   @Override
   public Mono<KeycloakResponse<ScopeRepresentation>> getScope(
       String accessToken,
       UUID clientUuid,
       UUID scopeId
   ) {
      if (clientUuid == null) {
         return Mono.just(KeycloakResponse.of(HttpResponseStatus.BAD_REQUEST.code(), "clientUuid is required", null, null));
      }

      return http.<ScopeRepresentation>get(configuration.getAuthScopeUrl(clientUuid, scopeId))
          .authorizationBearer(accessToken)
          .applicationJson()
          .responseType(ScopeRepresentation.class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<ScopeRepresentation[]>> getScopes(
       String accessToken,
       UUID clientUuid,
       ScopeQueryParams scopeQueryParams
   ) {
      String queryString = scopeQueryParams == null ? "" : scopeQueryParams.toQueryString();
      return http.<ScopeRepresentation[]>get(configuration.getAuthScopeSearchUrl(clientUuid, queryString))
          .authorizationBearer(accessToken)
          .applicationJson()
          .responseType(ScopeRepresentation[].class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<Void>> createScope(
       String accessToken,
       UUID clientUuid,
       ScopeRepresentation scopeRepresentation
   ) {
      if (clientUuid == null) {
         return Mono.just(KeycloakResponse.of(HttpResponseStatus.BAD_REQUEST.code(), "clientUuid is required", null, null));
      }

      return http.<Void>post(configuration.getAuthScopeUrl(clientUuid))
          .authorizationBearer(accessToken)
          .entities(scopeRepresentation)
          .applicationJson()
          .responseType(Void.class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<Void>> updateScope(
       String accessToken,
       UUID clientUuid,
       ScopeRepresentation scopeRepresentation
   ) {
      if (clientUuid == null) {
         return Mono.just(KeycloakResponse.of(HttpResponseStatus.BAD_REQUEST.code(), "clientUuid is required", null, null));
      }

      if (UUID.fromString(scopeRepresentation.getId()) == null) {
         return Mono.just(KeycloakResponse.of(HttpResponseStatus.BAD_REQUEST.code(), "scope Id is required", null, null));
      }

      return http.<Void>put(configuration.getAuthScopeUrl(clientUuid, UUID.fromString(scopeRepresentation.getId())))
          .authorizationBearer(accessToken)
          .entities(scopeRepresentation)
          .applicationJson()
          .responseType(Void.class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<Void>> deleteScope(
       String accessToken,
       UUID clientUuid,
       UUID scopeId
   ) {
      return http.<Void>delete(configuration.getAuthScopeUrl(clientUuid, scopeId))
          .authorizationBearer(accessToken)
          .applicationJson()
          .responseType(Void.class)
          .send();
   }
}
