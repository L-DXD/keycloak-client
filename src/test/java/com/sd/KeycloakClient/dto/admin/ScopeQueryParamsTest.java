package com.sd.KeycloakClient.dto.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ScopeQueryParamsTest {


   @Nested
   @DisplayName("toQueryString test cases")
   class toQueryString {

      @Test
      @DisplayName("case1. no parameters provided, should return empty query string")
      void toQueryString_noParams() {
         ScopeQueryParams scopeQueryParams = new ScopeQueryParams();
         String queryString = scopeQueryParams.toQueryString();

         assertTrue(queryString.isEmpty());
      }

      @Test
      @DisplayName("case2. with parameters provided, should return query string")
      void toQueryString_withParams() {
         ScopeQueryParams scopeQueryParams =
             ScopeQueryParams.builder()
                 .first("0")
                 .max("100")
                 .build();

         String queryString = scopeQueryParams.toQueryString();

         String[] parts = queryString.substring(1).split("&");
         Map<String, String> paramMap = Arrays.stream(parts)
             .map(s -> s.split("="))
             .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));

         assertEquals("0", paramMap.get("first"));
         assertEquals("100", paramMap.get("max"));
      }
   }
}