package com.sd.KeycloakClient.client.admin.auth.sync;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.ScopeQueryParams;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;

public interface KeycloakAuthScopeClient {

   KeycloakResponse<ScopeRepresentation> getScope(String accessToken, String clientUuid, String scopeId);

   KeycloakResponse<ScopeRepresentation[]> getScopes(String accessToken, String clientUuid, ScopeQueryParams scopeQueryParams);

   KeycloakResponse<Void> createScope(String accessToken, String clientUuid, ScopeRepresentation scopeRepresentation);

   KeycloakResponse<Void> updateScope(String accessToken, String clientUuid, ScopeRepresentation scopeRepresentation);

   KeycloakResponse<Void> deleteScope(String accessToken, String clientUuid, String scopeId);
}
