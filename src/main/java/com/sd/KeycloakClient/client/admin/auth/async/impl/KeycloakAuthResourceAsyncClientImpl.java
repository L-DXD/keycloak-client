package com.sd.KeycloakClient.client.admin.auth.async.impl;

import com.sd.KeycloakClient.client.admin.auth.async.KeycloakAuthResourceAsyncClient;
import com.sd.KeycloakClient.config.ClientConfiguration;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.ResourceQueryParams;
import com.sd.KeycloakClient.http.Http;
import java.util.Objects;
import java.util.UUID;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import reactor.core.publisher.Mono;

public class KeycloakAuthResourceAsyncClientImpl implements KeycloakAuthResourceAsyncClient {

   private final ClientConfiguration configuration;
   private final Http http;

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
      return http.<Void>delete(
              configuration.getAuthResourceUrl(Objects.toString(clientUuid, ""), Objects.toString(resourceId, "")))
          .authorizationBearer(accessToken)
          .applicationJson()
          .responseType(Void.class)
          .send();
   }
}