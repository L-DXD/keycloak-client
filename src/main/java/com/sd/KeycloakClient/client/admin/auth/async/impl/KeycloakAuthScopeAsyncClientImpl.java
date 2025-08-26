package com.sd.KeycloakClient.client.admin.auth.async.impl;

import com.sd.KeycloakClient.client.admin.auth.async.KeycloakAuthScopeAsyncClient;
import com.sd.KeycloakClient.config.ClientConfiguration;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.ScopeQueryParams;
import com.sd.KeycloakClient.http.Http;
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
       String clientUuid,
       String scopeId
   ) {
      return http.<ScopeRepresentation>get(configuration.getAuthScopeUrl(clientUuid, scopeId))
          .authorizationBearer(accessToken)
          .applicationJson()
          .responseType(ScopeRepresentation.class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<ScopeRepresentation[]>> getScopes(
       String accessToken,
       String clientUuid,
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
       String clientUuid,
       ScopeRepresentation scopeRepresentation
   ) {
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
       String clientUuid,
       ScopeRepresentation scopeRepresentation
   ) {
      return http.<Void>put(configuration.getAuthScopeUrl(clientUuid, scopeRepresentation.getId()))
          .authorizationBearer(accessToken)
          .entities(scopeRepresentation)
          .applicationJson()
          .responseType(Void.class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<Void>> deleteScope(
       String accessToken,
       String clientUuid,
       String scopeId
   ) {
      return http.<Void>delete(configuration.getAuthScopeUrl(clientUuid, scopeId))
          .authorizationBearer(accessToken)
          .applicationJson()
          .responseType(Void.class)
          .send();
   }
}
