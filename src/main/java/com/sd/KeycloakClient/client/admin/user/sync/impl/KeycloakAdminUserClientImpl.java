package com.sd.KeycloakClient.client.admin.user.sync.impl;

import com.sd.KeycloakClient.client.admin.user.async.KeycloakAdminUserAsyncClient;
import com.sd.KeycloakClient.client.admin.user.sync.KeycloakAdminUserClient;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.user.UserQueryParams;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;

@RequiredArgsConstructor
public class KeycloakAdminUserClientImpl implements KeycloakAdminUserClient {

   private final KeycloakAdminUserAsyncClient adminUserClient;

   @Override
   public KeycloakResponse<UserRepresentation[]> searchUsers(String accessToken, UserQueryParams params) {
      return adminUserClient.searchUsers(accessToken, params).block();
   }

   @Override
   public KeycloakResponse<Void> updateUserInfo(String accessToken, UserRepresentation userRepresentation) {
      return adminUserClient.updateUserInfo(accessToken, userRepresentation).block();
   }

   @Override
   public KeycloakResponse<UserRepresentation> findByUserId(String accessToken, String userId) {
      return adminUserClient.findByUserId(accessToken, userId).block();
   }

}
