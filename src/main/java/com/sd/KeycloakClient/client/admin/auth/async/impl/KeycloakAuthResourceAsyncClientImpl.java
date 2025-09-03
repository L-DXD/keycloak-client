package com.sd.KeycloakClient.client.admin.auth.async.impl;

import com.sd.KeycloakClient.client.admin.auth.async.KeycloakAuthResourceAsyncClient;
import com.sd.KeycloakClient.config.ClientConfiguration;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.ResourceQueryParams;
import com.sd.KeycloakClient.http.Http;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.Objects;
import java.util.UUID;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import reactor.core.publisher.Mono;

public class KeycloakAuthResourceAsyncClientImpl implements KeycloakAuthResourceAsyncClient {

   private final ClientConfiguration configuration;
   private final Http http;
   private final String ACCESS_TOKEN_MISSING_OR_BLANK = "Unauthorized";
   private final String CLIENT_UUID_IS_REQUIRED = "clientUuid is required";
   private final String RESOURCE_ID_IS_REQUIRED = "resourceId is required";

   public KeycloakAuthResourceAsyncClientImpl(ClientConfiguration configuration) {
      this.configuration = configuration;
      this.http = new Http(configuration);
   }

   @Override
   public Mono<KeycloakResponse<ResourceRepresentation>> getResource(
       String accessToken,
       UUID clientUuid,
       UUID resourceId
   ) {
      if (accessToken == null || accessToken.isBlank()) {
         return Mono.just(KeycloakResponse.fail(HttpResponseStatus.UNAUTHORIZED.code(), ACCESS_TOKEN_MISSING_OR_BLANK));
      }
      if (clientUuid == null) {
         return Mono.just(KeycloakResponse.fail(HttpResponseStatus.BAD_REQUEST.code(), CLIENT_UUID_IS_REQUIRED));
      }
      if (resourceId == null) {
         return Mono.just(KeycloakResponse.fail(HttpResponseStatus.BAD_REQUEST.code(), RESOURCE_ID_IS_REQUIRED));
      }

      return http.<ResourceRepresentation>get(
              configuration.getAuthResourceUrl(Objects.toString(clientUuid, ""), Objects.toString(resourceId, "")))
          .authorizationBearer(accessToken)
          .applicationJson()
          .responseType(ResourceRepresentation.class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<ResourceRepresentation[]>> getResources(
       String accessToken,
       UUID clientUuid,
       ResourceQueryParams queryParams
   ) {
      if (accessToken == null || accessToken.isBlank()) {
         return Mono.just(KeycloakResponse.fail(HttpResponseStatus.UNAUTHORIZED.code(), ACCESS_TOKEN_MISSING_OR_BLANK));
      }
      if (clientUuid == null) {
         return Mono.just(KeycloakResponse.fail(HttpResponseStatus.BAD_REQUEST.code(), CLIENT_UUID_IS_REQUIRED));
      }

      String queryString = (queryParams == null) ? "" : queryParams.toQueryString();
      return http.<ResourceRepresentation[]>get(
              configuration.getAuthResourceSearchUrl(Objects.toString(clientUuid, ""), queryString))
          .authorizationBearer(accessToken)
          .applicationJson()
          .responseType(ResourceRepresentation[].class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<Void>> createResource(
       String accessToken,
       UUID clientUuid,
       ResourceRepresentation resourceRepresentation
   ) {
      if (accessToken == null || accessToken.isBlank()) {
         return Mono.just(KeycloakResponse.fail(HttpResponseStatus.UNAUTHORIZED.code(), ACCESS_TOKEN_MISSING_OR_BLANK));
      }
      if (clientUuid == null) {
         return Mono.just(KeycloakResponse.fail(HttpResponseStatus.BAD_REQUEST.code(), CLIENT_UUID_IS_REQUIRED));
      }
      return http.<Void>post(configuration.getAuthResourceUrl(Objects.toString(clientUuid, "")))
          .authorizationBearer(accessToken)
          .entities(resourceRepresentation)
          .applicationJson()
          .responseType(Void.class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<Void>> updateResource(
       String accessToken,
       UUID clientUuid,
       ResourceRepresentation resourceRepresentation
   ) {
      if (accessToken == null || accessToken.isBlank()) {
         return Mono.just(KeycloakResponse.fail(HttpResponseStatus.UNAUTHORIZED.code(), ACCESS_TOKEN_MISSING_OR_BLANK));
      }
      if (clientUuid == null) {
         return Mono.just(KeycloakResponse.fail(HttpResponseStatus.BAD_REQUEST.code(), CLIENT_UUID_IS_REQUIRED));
      }
      return http.<Void>put(configuration.getAuthResourceUrl(Objects.toString(clientUuid, ""), resourceRepresentation.getId()))
          .authorizationBearer(accessToken)
          .entities(resourceRepresentation)
          .applicationJson()
          .responseType(Void.class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<Void>> deleteResource(
       String accessToken,
       UUID clientUuid,
       UUID resourceId
   ) {
      if (accessToken == null || accessToken.isBlank()) {
         return Mono.just(KeycloakResponse.fail(HttpResponseStatus.UNAUTHORIZED.code(), ACCESS_TOKEN_MISSING_OR_BLANK));
      }
      if (clientUuid == null) {
         return Mono.just(KeycloakResponse.fail(HttpResponseStatus.BAD_REQUEST.code(), CLIENT_UUID_IS_REQUIRED));
      }
      if (resourceId == null) {
         return Mono.just(KeycloakResponse.fail(HttpResponseStatus.BAD_REQUEST.code(), RESOURCE_ID_IS_REQUIRED));
      }
      return http.<Void>delete(
              configuration.getAuthResourceUrl(Objects.toString(clientUuid, ""), Objects.toString(resourceId, "")))
          .authorizationBearer(accessToken)
          .applicationJson()
          .responseType(Void.class)
          .send();
   }
}