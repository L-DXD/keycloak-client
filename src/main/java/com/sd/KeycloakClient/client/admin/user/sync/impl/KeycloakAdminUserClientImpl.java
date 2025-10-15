package com.sd.KeycloakClient.client.admin.user.sync.impl;

import com.sd.KeycloakClient.client.admin.user.async.KeycloakAdminUserAsyncClient;
import com.sd.KeycloakClient.client.admin.user.sync.KeycloakAdminUserClient;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.user.UserQueryParams;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

@RequiredArgsConstructor
public class KeycloakAdminUserClientImpl implements KeycloakAdminUserClient {

   private final KeycloakAdminUserAsyncClient adminUserClient;

   @Override
   public KeycloakResponse<UserRepresentation[]> searchUsers(String accessToken, UserQueryParams params) {
      return adminUserClient.searchUsers(accessToken, params).block();
   }

   @Override
   public KeycloakResponse<Integer> getUsersCount(String accessToken, UserQueryParams params) {
      return adminUserClient.getUsersCount(accessToken, params).block();
   }

   @Override
   public KeycloakResponse<Void> updateUserInfo(String accessToken, UserRepresentation userRepresentation) {
      return adminUserClient.updateUserInfo(accessToken, userRepresentation).block();
   }

   @Override
   public KeycloakResponse<UserRepresentation> findByUserId(String accessToken, UUID userId) {
      return adminUserClient.findByUserId(accessToken, userId).block();
   }

   @Override
   public KeycloakResponse<Void> createUser(String accessToken, UserRepresentation userRepresentation) {
      return adminUserClient.createUser(accessToken, userRepresentation).block();
   }

   @Override
   public KeycloakResponse<Void> resetPassword(String accessToken, UUID userId, CredentialRepresentation credentialRepresentation) {
      return adminUserClient.resetPassword(accessToken, userId, credentialRepresentation).block();
   }

}
