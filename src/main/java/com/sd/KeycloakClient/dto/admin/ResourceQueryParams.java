package com.sd.KeycloakClient.dto.admin;

import static com.sd.KeycloakClient.util.UrlUtil.toUrlEncoded;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author: LeeBongSeung
 * @date: 2025-08-28
 * @description: Keycloak client resource query parameters
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceQueryParams {

   /**
    * Index of the first resource to return (used for pagination). Defaults to 0 if not specified.
    */
   @Builder.Default
   private Integer first = 0;

   /**
    * Maximum number of resources to return (used for pagination). Defaults to 100 if not specified.
    */
   @Builder.Default
   private Integer max = 100;

   /**
    * ID of the resource.
    */
   private UUID id;

   /**
    * Whether to perform deep search (true/false).
    */
   private Boolean deep;

   /**
    * Whether to match name exactly.
    */
   private Boolean exactName;

   /**
    * Matching URI string to filter resources.
    */
   private String matchingUri;

   /**
    * Name of the resource to filter by.
    */
   private String name;

   /**
    * Owner of the resource (user/client identifier).
    */
   private String owner;

   /**
    * Scope of the resource.
    */
   private String scope;

   /**
    * Type of the resource.
    */
   private String type;

   /**
    * URI of the resource.
    */
   private String uri;

   public String toQueryString() {
      String queryParam = toUrlEncoded(Stream.of(
              new SimpleEntry<>("first", first),
              new SimpleEntry<>("max", max),
              new SimpleEntry<>("id", id),
              new SimpleEntry<>("deep", deep),
              new SimpleEntry<>("exactName", exactName),
              new SimpleEntry<>("matchingUri", matchingUri),
              new SimpleEntry<>("name", name),
              new SimpleEntry<>("owner", owner),
              new SimpleEntry<>("scope", scope),
              new SimpleEntry<>("type", type),
              new SimpleEntry<>("uri", uri)
          )
          .filter(entry -> entry.getValue() != null)
          .collect(Collectors.toMap(
              Entry::getKey,
              Entry::getValue,
              (a, b) -> b,
              LinkedHashMap::new
          )));

      return queryParam.isEmpty() ? "" : "?" + queryParam;
   }
}
