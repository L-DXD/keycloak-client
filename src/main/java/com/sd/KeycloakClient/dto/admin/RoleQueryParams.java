package com.sd.KeycloakClient.dto.admin;

import static com.sd.KeycloakClient.util.UrlUtil.toUrlEncoded;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Objects;
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
public class RoleQueryParams {

   /**
    * If true, returns only brief representations of users (e.g., id, username), not full details
    */
   private Boolean briefRepresentation;
   /**
    * Index of the first user to return (used for pagination). Defaults to 0 if not specified.
    */
   private Integer first;
   /**
    * Maximum number of users to return (used for pagination).
    */
   private Integer max;
   /**
    * arbitrary search string for all the fields below. Default search behavior is prefix-based (e.g., foo or foo*). Use foo for infix
    * search and &quot;foo&quot; for exact search.
    */
   private String search;

   public String toQueryString() {
      String queryParam = toUrlEncoded(Stream.of(
              new SimpleEntry<>("briefRepresentation", briefRepresentation),
              new SimpleEntry<>("first", first),
              new SimpleEntry<>("max", max),
              new SimpleEntry<>("search", search)
          )
          .filter(entry -> Objects.nonNull(entry.getValue()))
          .collect(Collectors.toMap(Entry::getKey, Entry::getValue)));

      return queryParam.isEmpty() ? "" : "?" + queryParam;
   }
}
