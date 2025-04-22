package com.sd.KeycloakClient.properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import lombok.Getter;
import org.keycloak.representations.idm.RealmRepresentation;

/**
 * @author SangWonYu
 * @date 2025-04
 */

@Getter
public class KeycloakProperties {

   private final String clientId;
   private final String clientSecret;
   private final String realmName;

   public KeycloakProperties() throws Exception {
      RealmRepresentation realm = readRealmJson();
      clientId = realm.getClients().get(0).getClientId();
      clientSecret = realm.getClients().get(0).getSecret();
      realmName = realm.getRealm();
   }

   private RealmRepresentation readRealmJson() throws Exception {
      ObjectMapper mapper = new ObjectMapper();
      ClassLoader classLoader = KeycloakProperties.class.getClassLoader();
      try (InputStream inputStream = classLoader.getResourceAsStream("realm.json")) {
         if (inputStream == null) {
            throw new IllegalArgumentException("Not found File: realm.json");
         }
         return mapper.readValue(inputStream, RealmRepresentation.class);
      }
   }
}
