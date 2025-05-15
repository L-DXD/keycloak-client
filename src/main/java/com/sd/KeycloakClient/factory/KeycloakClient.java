package com.sd.KeycloakClient.factory;

import com.sd.KeycloakClient.client.admin.user.async.KeycloakAdminUserAsyncClient;
import com.sd.KeycloakClient.client.admin.user.async.impl.KeycloakAdminUserAsyncClientImpl;
import com.sd.KeycloakClient.client.admin.user.sync.KeycloakAdminUserClient;
import com.sd.KeycloakClient.client.admin.user.sync.impl.KeycloakAdminUserClientImpl;
import com.sd.KeycloakClient.client.auth.async.KeycloakAuthAsyncClient;
import com.sd.KeycloakClient.client.auth.async.impl.KeycloakAuthAsyncClientImpl;
import com.sd.KeycloakClient.client.auth.sync.KeycloakAuthClient;
import com.sd.KeycloakClient.client.auth.sync.impl.KeycloakAuthClientImpl;
import com.sd.KeycloakClient.client.user.async.KeycloakUserAsyncClient;
import com.sd.KeycloakClient.client.user.async.impl.KeycloakUserAsyncClientImpl;
import com.sd.KeycloakClient.client.user.sync.KeycloakUserClient;
import com.sd.KeycloakClient.client.user.sync.impl.KeycloakUserClientImpl;
import com.sd.KeycloakClient.config.ClientConfiguration;

/**
 * @author SangWonYu
 * @date 2025-04-03
 * @description Keycloak Client class by pacade pattern Call the necessary client methods to use them.
 */

public class KeycloakClient {

   private final KeycloakAuthClientImpl authClient;
   private final KeycloakAuthAsyncClientImpl authAsyncClient;
   private final KeycloakUserClientImpl userClient;
   private final KeycloakUserAsyncClientImpl userAsyncClient;
   private final KeycloakAdminUserAsyncClientImpl adminUserAsyncClient;
   private final KeycloakAdminUserClientImpl adminUserClient;

   public KeycloakClient(final ClientConfiguration config) {
      this.authAsyncClient = new KeycloakAuthAsyncClientImpl(config);
      this.authClient = new KeycloakAuthClientImpl(this.authAsyncClient);
      this.userAsyncClient = new KeycloakUserAsyncClientImpl(config);
      this.userClient = new KeycloakUserClientImpl(this.userAsyncClient);
      this.adminUserAsyncClient = new KeycloakAdminUserAsyncClientImpl(config);
      this.adminUserClient = new KeycloakAdminUserClientImpl(this.adminUserAsyncClient);
   }

   public KeycloakAuthClient auth() {
      return this.authClient;
   }

   public KeycloakAuthAsyncClient authAsync() {
      return this.authAsyncClient;
   }

   public KeycloakUserClient user() {
      return this.userClient;
   }

   public KeycloakUserAsyncClient userAsync() {
      return this.userAsyncClient;
   }

   public KeycloakAdminUserAsyncClient adminUserAsync() {
      return this.adminUserAsyncClient;
   }

   public KeycloakAdminUserClient adminUser() {
      return this.adminUserClient;
   }
}
