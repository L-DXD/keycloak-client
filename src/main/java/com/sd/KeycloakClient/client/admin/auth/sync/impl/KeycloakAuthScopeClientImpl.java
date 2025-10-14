package com.sd.KeycloakClient.client.admin.auth.sync.impl;

import com.sd.KeycloakClient.client.admin.auth.async.KeycloakAuthScopeAsyncClient;
import com.sd.KeycloakClient.client.admin.auth.sync.KeycloakAuthScopeClient;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.ScopeQueryParams;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;

@RequiredArgsConstructor
public class KeycloakAuthScopeClientImpl implements KeycloakAuthScopeClient {

   private final KeycloakAuthScopeAsyncClient keycloakAuthScopeAsyncClient;

   @Override
   public KeycloakResponse<ScopeRepresentation> getScope(String accessToken, UUID clientUuid, UUID scopeId) {
      return keycloakAuthScopeAsyncClient.getScope(accessToken, clientUuid, scopeId).block();
   }

   @Override
   public KeycloakResponse<ScopeRepresentation[]> getScopes(String accessToken, UUID clientUuid, ScopeQueryParams scopeQueryParams) {
      return keycloakAuthScopeAsyncClient.getScopes(accessToken, clientUuid, scopeQueryParams).block();
   }

   @Override
   public KeycloakResponse<Void> createScope(String accessToken, UUID clientUuid, ScopeRepresentation scopeRepresentation) {
      return keycloakAuthScopeAsyncClient.createScope(accessToken, clientUuid, scopeRepresentation).block();
   }

   @Override
   public KeycloakResponse<Void> updateScope(String accessToken, UUID clientUuid, ScopeRepresentation scopeRepresentation) {
      return keycloakAuthScopeAsyncClient.updateScope(accessToken, clientUuid, scopeRepresentation).block();
   }

   @Override
   public KeycloakResponse<Void> deleteScope(String accessToken, UUID clientUuid, UUID scopeId) {
      return keycloakAuthScopeAsyncClient.deleteScope(accessToken, clientUuid, scopeId).block();
   }
}
