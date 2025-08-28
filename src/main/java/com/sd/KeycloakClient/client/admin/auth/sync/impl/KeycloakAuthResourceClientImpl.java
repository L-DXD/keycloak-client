package com.sd.KeycloakClient.client.admin.auth.sync.impl;

import com.sd.KeycloakClient.client.admin.auth.async.KeycloakAuthResourceAsyncClient;
import com.sd.KeycloakClient.client.admin.auth.sync.KeycloakAuthResourceClient;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.ResourceQueryParams;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;

@RequiredArgsConstructor
public class KeycloakAuthResourceClientImpl implements KeycloakAuthResourceClient {

   private final KeycloakAuthResourceAsyncClient keycloakAuthResourceAsyncClient;

   @Override
   public KeycloakResponse<ResourceRepresentation> getResource(String accessToken, UUID clientUuid, UUID resourceId) {
      return keycloakAuthResourceAsyncClient.getResource(accessToken, clientUuid, resourceId).block();
   }

   @Override
   public KeycloakResponse<ResourceRepresentation[]> getResources(String accessToken, UUID clientUuid, ResourceQueryParams queryParams) {
      return keycloakAuthResourceAsyncClient.getResources(accessToken, clientUuid, queryParams).block();
   }

   @Override
   public KeycloakResponse<Void> createResource(String accessToken, UUID clientUuid, ResourceRepresentation resourceRepresentation) {
      return keycloakAuthResourceAsyncClient.createResource(accessToken, clientUuid, resourceRepresentation).block();
   }

   @Override
   public KeycloakResponse<Void> updateResource(String accessToken, UUID clientUuid, ResourceRepresentation resourceRepresentation) {
      return keycloakAuthResourceAsyncClient.updateResource(accessToken, clientUuid, resourceRepresentation).block();
   }

   @Override
   public KeycloakResponse<Void> deleteResource(String accessToken, UUID clientUuid, UUID resourceId) {
      return keycloakAuthResourceAsyncClient.deleteResource(accessToken, clientUuid, resourceId).block();
   }
}
