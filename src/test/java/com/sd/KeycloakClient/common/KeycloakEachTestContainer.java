package com.sd.KeycloakClient.common;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * @author SangWonYu
 * @date 2025- 04
 */
public abstract class KeycloakEachTestContainer {

   private static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:26.0.6";
   private static final String KEYCLOAK_ADMIN_USER = "admin";
   private static final String KEYCLOAK_ADMIN_PW = "admin";

   private static final KeycloakContainer keycloak = new KeycloakContainer(KEYCLOAK_IMAGE)
       .withRealmImportFile("/realm.json")
       .withAdminUsername(KEYCLOAK_ADMIN_USER)
       .withAdminPassword(KEYCLOAK_ADMIN_PW);

   @BeforeEach
   void startKeycloak() {
      keycloak.start();
   }

   @AfterEach
   void stopKeycloak() {
      keycloak.stop();
   }

   public static KeycloakContainer container() {
      return keycloak;
   }

   public static String authServerUrl() {
      return keycloak.getAuthServerUrl();
   }

}
