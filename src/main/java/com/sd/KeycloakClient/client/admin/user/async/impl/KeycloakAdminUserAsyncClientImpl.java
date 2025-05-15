package com.sd.KeycloakClient.client.admin.user.async.impl;

import com.sd.KeycloakClient.client.admin.user.async.KeycloakAdminUserAsyncClient;
import com.sd.KeycloakClient.config.ClientConfiguration;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.user.UserQueryParams;
import com.sd.KeycloakClient.http.Http;
import org.keycloak.representations.idm.UserRepresentation;
import reactor.core.publisher.Mono;

public class KeycloakAdminUserAsyncClientImpl implements KeycloakAdminUserAsyncClient {

   private final ClientConfiguration configuration;
   private final Http http;

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
          .responseType(UserRepresentation[].class)
          .send();
   }

}
