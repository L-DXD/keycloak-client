package com.sd.KeycloakClient.common;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * @author SangWonYu
 * @date 2025-04
 * @description A base class for sharing a container across a single test.
 */
@Testcontainers
public abstract class KeycloakShareTestContainer {

   private static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:26.0.6";
   private static final String KEYCLOAK_ADMIN_USER = "admin";
   private static final String KEYCLOAK_ADMIN_PW = "admin";

   @Container
   private static final KeycloakContainer keycloak = new KeycloakContainer(KEYCLOAK_IMAGE)
       .withRealmImportFile("/realm.json")
       .withAdminUsername(KEYCLOAK_ADMIN_USER)
       .withAdminPassword(KEYCLOAK_ADMIN_PW);

   public static KeycloakContainer container() {
      return keycloak;
   }

   public static String authServerUrl() {
      return keycloak.getAuthServerUrl();
   }

}
