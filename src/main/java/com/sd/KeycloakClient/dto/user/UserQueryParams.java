package com.sd.KeycloakClient.dto.user;

import static com.sd.KeycloakClient.util.UrlUtil.toUrlEncoded;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserQueryParams {

   /**
    * If true, returns only brief representations of users (e.g., id, username), not full details
    */
   private Boolean briefRepresentation;
   /**
    * email filter
    */
   private String email;
   /**
    * Filters users based on whether their email is verified (true = verified, false = not verified)
    */
   private Boolean emailVerified;
   /**
    * Boolean representing if user is enabled or not
    */
   private Boolean enabled;
   /**
    * If true, performs exact matching for fields like username, email, etc.
    */
   private Boolean exact;
   /**
    * Filters users by the alias of the identity provider they are linked to
    */
   private String idpAlias;
   /**
    * Filters users by the user ID provided by the identity provider
    */
   private String idpUserId;
   /**
    * Filters users by their first name (e.g., "John")
    */
   private String firstName;

   /**
    * Filters users by their last name (e.g., "Doe")
    */
   private String lastName;
   /**
    * Index of the first user to return (used for pagination). Defaults to 0 if not specified.
    */
   private Integer first;
   /**
    * Maximum number of users to return (used for pagination).
    */
   private Integer max;
   /**
    * Advanced query string to filter users using Keycloak's query language (e.g., "email:test@example.com")
    */
   private String q;
   /**
    * arbitrary search string for all the fields below. Default search behavior is prefix-based (e.g., foo or foo*). Use foo for infix
    * search and &quot;foo&quot; for exact search.
    */
   private String search;
   /**
    * username filter
    */
   private String username;


   public String toQueryString() {
      String queryParam = toUrlEncoded(Stream.of(
              new SimpleEntry<>("briefRepresentation", briefRepresentation),
              new SimpleEntry<>("email", email),
              new SimpleEntry<>("emailVerified", emailVerified),
              new SimpleEntry<>("enabled", enabled),
              new SimpleEntry<>("exact", exact),
              new SimpleEntry<>("first", first),
              new SimpleEntry<>("firstName", firstName),
              new SimpleEntry<>("idpAlias", idpAlias),
              new SimpleEntry<>("idpUserId", idpUserId),
              new SimpleEntry<>("lastName", lastName),
              new SimpleEntry<>("max", max),
              new SimpleEntry<>("q", q),
              new SimpleEntry<>("search", search),
              new SimpleEntry<>("username", username)
          )
          .filter(entry -> Objects.nonNull(entry.getValue()))
          .collect(Collectors.toMap(Entry::getKey, Entry::getValue)));

      return queryParam.isEmpty() ? "" : "?" + queryParam;
   }

}
