package com.sd.KeycloakClient.client.admin.client.sync.impl;

import com.sd.KeycloakClient.client.admin.client.async.KeycloakClientsAsyncClient;
import com.sd.KeycloakClient.client.admin.client.sync.KeycloakClientsClient;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.ClientQueryParams;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.ClientRepresentation;

@RequiredArgsConstructor
public class KeycloakClientsClientImpl implements KeycloakClientsClient {

   private final KeycloakClientsAsyncClient keycloakClientsAsyncClient;

   @Override
   public KeycloakResponse<ClientRepresentation[]> getClientsInfo(String accessToken, ClientQueryParams clientQueryParams) {
      return keycloakClientsAsyncClient.getClientsInfo(accessToken, clientQueryParams).block();
   }
}
