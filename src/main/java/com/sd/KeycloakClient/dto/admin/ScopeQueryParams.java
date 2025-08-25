package com.sd.KeycloakClient.dto.admin;

import static com.sd.KeycloakClient.util.UrlUtil.toUrlEncoded;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScopeQueryParams {

   /**
    * Index of the first scope to return (used for pagination). Defaults to 0 if not specified.
    */
   private String first;
   /**
    * Maximum number of scopes to return (used for pagination).
    */
   private String max;
   /**
    * Name of the scope to filter by.
    */
   private String name;
   /**
    * Scope ID to filter by.
    */
   private String scopeId;

   public String toQueryString() {
      String queryParam = toUrlEncoded(Stream.of(
              new SimpleEntry<>("first", first),
              new SimpleEntry<>("max", max),
              new SimpleEntry<>("name", name),
              new SimpleEntry<>("scopeId", scopeId)
          )
          .filter(entry -> entry.getValue() != null)
          .collect(Collectors.toMap(Entry::getKey, Entry::getValue)));

      return queryParam.isEmpty() ? "" : "?" + queryParam;
   }
}
