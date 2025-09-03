package com.sd.KeycloakClient.factory;

import com.sd.KeycloakClient.client.admin.auth.async.KeycloakAuthResourceAsyncClient;
import com.sd.KeycloakClient.client.admin.auth.async.KeycloakAuthScopeAsyncClient;
import com.sd.KeycloakClient.client.admin.auth.async.impl.KeycloakAuthResourceAsyncClientImpl;
import com.sd.KeycloakClient.client.admin.auth.async.impl.KeycloakAuthScopeAsyncClientImpl;
import com.sd.KeycloakClient.client.admin.auth.sync.KeycloakAuthResourceClient;
import com.sd.KeycloakClient.client.admin.auth.sync.KeycloakAuthScopeClient;
import com.sd.KeycloakClient.client.admin.auth.sync.impl.KeycloakAuthResourceClientImpl;
import com.sd.KeycloakClient.client.admin.auth.sync.impl.KeycloakAuthScopeClientImpl;
import com.sd.KeycloakClient.client.admin.client.async.KeycloakClientsAsyncClient;
import com.sd.KeycloakClient.client.admin.client.async.impl.KeycloakClientsAsyncClientImpl;
import com.sd.KeycloakClient.client.admin.client.sync.KeycloakClientsClient;
import com.sd.KeycloakClient.client.admin.client.sync.impl.KeycloakClientsClientImpl;
import com.sd.KeycloakClient.client.admin.role.async.KeycloakRoleAsyncClient;
import com.sd.KeycloakClient.client.admin.role.async.impl.KeycloakRoleAsyncClientImpl;
import com.sd.KeycloakClient.client.admin.role.sync.KeycloakRoleClient;
import com.sd.KeycloakClient.client.admin.role.sync.impl.KeycloakRoleClientImpl;
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
   private final KeycloakClientsAsyncClientImpl clientsAsyncClient;
   private final KeycloakClientsClientImpl clientsClient;
   private final KeycloakRoleAsyncClientImpl roleAsyncClient;
   private final KeycloakRoleClientImpl roleClient;
   private final KeycloakAuthScopeAsyncClientImpl authScopeAsyncClient;
   private final KeycloakAuthScopeClientImpl authScopeClient;
   private final KeycloakAuthResourceAsyncClientImpl authResourceAsyncClient;
   private final KeycloakAuthResourceClientImpl authResourceClient;

   public KeycloakClient(final ClientConfiguration config) {
      this.authAsyncClient = new KeycloakAuthAsyncClientImpl(config);
      this.authClient = new KeycloakAuthClientImpl(this.authAsyncClient);
      this.userAsyncClient = new KeycloakUserAsyncClientImpl(config);
      this.userClient = new KeycloakUserClientImpl(this.userAsyncClient);
      this.adminUserAsyncClient = new KeycloakAdminUserAsyncClientImpl(config);
      this.adminUserClient = new KeycloakAdminUserClientImpl(this.adminUserAsyncClient);
      this.clientsAsyncClient = new KeycloakClientsAsyncClientImpl(config);
      this.clientsClient = new KeycloakClientsClientImpl(this.clientsAsyncClient);
      this.roleAsyncClient = new KeycloakRoleAsyncClientImpl(config);
      this.roleClient = new KeycloakRoleClientImpl(this.roleAsyncClient);
      this.authScopeAsyncClient = new KeycloakAuthScopeAsyncClientImpl(config);
      this.authScopeClient = new KeycloakAuthScopeClientImpl(this.authScopeAsyncClient);
      this.authResourceAsyncClient = new KeycloakAuthResourceAsyncClientImpl(config);
      this.authResourceClient = new KeycloakAuthResourceClientImpl(this.authResourceAsyncClient);
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

   public KeycloakClientsAsyncClient clientsAsync() {
      return this.clientsAsyncClient;
   }

   public KeycloakClientsClient clients() {
      return this.clientsClient;
   }

   public KeycloakAdminUserAsyncClient adminUserAsync() {
      return this.adminUserAsyncClient;
   }

   public KeycloakAdminUserClient adminUser() {
      return this.adminUserClient;
   }

   public KeycloakRoleAsyncClient roleAsync() {
      return this.roleAsyncClient;
   }

   public KeycloakRoleClient role() {
      return this.roleClient;
   }

   public KeycloakAuthScopeAsyncClient authScopeAsync() {
      return this.authScopeAsyncClient;
   }

   public KeycloakAuthScopeClient authScope() {
      return this.authScopeClient;
   }

   public KeycloakAuthResourceAsyncClient authResourceAsync() {
      return this.authResourceAsyncClient;
   }

   public KeycloakAuthResourceClient authResource() {
      return this.authResourceClient;
   }
}
