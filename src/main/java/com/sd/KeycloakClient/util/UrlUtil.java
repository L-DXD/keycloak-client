package com.sd.KeycloakClient.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public class UrlUtil {

   public static String toUrlEncoded(Map<String, Object> entities) {
      return entities.entrySet().stream()
          .map(entry -> {
             try {
                return URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" +
                    URLEncoder.encode(String.valueOf(entry.getValue()), StandardCharsets.UTF_8);
             } catch (Exception e) {
                throw new RuntimeException("Failed to encode parameter", e);
             }
          })
          .collect(Collectors.joining("&"));
   }

   /**
    * # : keycloak scope
    *
    * @return
    */
   public static String toPermissionFormat(String endpoint, String scope) {
      return endpoint + "#" + scope;
   }
}
