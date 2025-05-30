package com.sd.KeycloakClient.client.admin.client.async.impl;

import com.sd.KeycloakClient.client.admin.client.async.KeycloakClientsAsyncClient;
import com.sd.KeycloakClient.config.ClientConfiguration;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.ClientQueryParams;
import com.sd.KeycloakClient.http.Http;
import org.keycloak.representations.idm.ClientRepresentation;
import reactor.core.publisher.Mono;

public class KeycloakClientsAsyncClientImpl implements KeycloakClientsAsyncClient {

   private final ClientConfiguration configuration;
   private final Http http;

   public KeycloakClientsAsyncClientImpl(ClientConfiguration configuration) {
      this.configuration = configuration;
      this.http = new Http(configuration);
   }

   @Override
   public Mono<KeycloakResponse<ClientRepresentation[]>> getClientsInfo(String accessToken, ClientQueryParams clientQueryParams) {
      String url = configuration.getClientSearchUrl(clientQueryParams.toQueryString());
      return http.<ClientRepresentation[]>get(url)
          .responseType(ClientRepresentation[].class)
          .authorizationBearer(accessToken)
          .applicationJson()
          .send();
   }

}
