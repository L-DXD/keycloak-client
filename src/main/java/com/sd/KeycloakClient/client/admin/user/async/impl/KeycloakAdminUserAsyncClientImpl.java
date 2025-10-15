package com.sd.KeycloakClient.client.admin.user.async.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sd.KeycloakClient.client.admin.user.async.KeycloakAdminUserAsyncClient;
import com.sd.KeycloakClient.config.ClientConfiguration;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.user.UserQueryParams;
import com.sd.KeycloakClient.http.Http;
import java.util.UUID;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import reactor.core.publisher.Mono;

public class KeycloakAdminUserAsyncClientImpl implements KeycloakAdminUserAsyncClient {

   private final ClientConfiguration configuration;
   private final Http http;
   private final ObjectMapper objectMapper = new ObjectMapper();

   public KeycloakAdminUserAsyncClientImpl(ClientConfiguration configuration) {
      this.configuration = configuration;
      this.http = new Http(configuration);
   }

   @Override
   public Mono<KeycloakResponse<UserRepresentation[]>> searchUsers(
       String accessToken,
       UserQueryParams userQueryParams
   ) {
      String url = configuration.getUserSearchUrl(userQueryParams.toQueryString());

      return http.<UserRepresentation[]>get(url)
          .authorizationBearer(accessToken)
          .applicationJson()
          .responseType(UserRepresentation[].class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<Integer>> getUsersCount(String accessToken, UserQueryParams userQueryParams) {
      String url = configuration.getUsersCountUrl(userQueryParams.toQueryString());
      return http.<Integer>get(url)
          .authorizationBearer(accessToken)
          .applicationJson()
          .responseType(Integer.class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<Void>> updateUserInfo(String accessToken, UserRepresentation userRepresentation) {
      String url = configuration.getUserUrl(UUID.fromString(userRepresentation.getId()));
      return http.<Void>put(url)
          .authorizationBearer(accessToken)
          .entities(userRepresentation)
          .applicationJson()
          .responseType(Void.class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<UserRepresentation>> findByUserId(String accessToken, UUID userId) {
      String url = configuration.getUserUrl(userId);
      return http.<UserRepresentation>get(url)
          .authorizationBearer(accessToken)
          .applicationJson()
          .responseType(UserRepresentation.class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<Void>> createUser(String accessToken, UserRepresentation userRepresentation) {
      String url = configuration.getBaseUserPath();
      return http.<Void>post(url)
          .authorizationBearer(accessToken)
          .entities(userRepresentation)
          .applicationJson()
          .responseType(Void.class)
          .send();
   }

   @Override
   public Mono<KeycloakResponse<Void>> resetPassword(String accessToken, UUID userId, CredentialRepresentation credentialRepresentation) {
      String resetPasswordUrl = configuration.getResetPasswordUrl(userId);
      return http.<Void>put(resetPasswordUrl)
          .authorizationBearer(accessToken)
          .entities(credentialRepresentation)
          .applicationJson()
          .responseType(Void.class)
          .send();
   }

}
