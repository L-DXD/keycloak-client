package com.sd.KeycloakClient.dto.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ScopeQueryParamsTest {


   @Test
   @DisplayName("1. no parameters provided -> empty string")
   void noParams_returnsEmpty() {
      ScopeQueryParams q = new ScopeQueryParams();
      String s = q.toQueryString();
      assertEquals(s, "?first=0&max=100");
   }

   @ParameterizedTest(name = "2.{index}) first={0}, max={1} => \"{2}\"")
   @CsvSource({
       "0,100,?first=0&max=100",
       "10,50,?first=10&max=50",
       "1,999,?first=1&max=999",
       "-1,100,?first=-1&max=100"
   })
   @DisplayName("2. numeric parameters -> exact string match")
   void numericParams_toString(Integer first, Integer max, String expected) {
      ScopeQueryParams q = ScopeQueryParams.builder()
          .first(first)
          .max(max)
          .build();
      assertEquals(expected, q.toQueryString());
   }

   @ParameterizedTest(name = "3.{index}) name=\"{0}\" -> contains name= fragment")
   @ValueSource(strings = {"read", "read write", "읽기", "read/write", "a+b=c&d"})
   @DisplayName("3. name provided -> included in query")
   void name_included(String name) {
      ScopeQueryParams q = ScopeQueryParams.builder()
          .name(name)
          .build();
      String s = q.toQueryString();
      assertThat(s).contains("name=");
   }

   @Test
   @DisplayName("4. scopeId provided -> included in query")
   void scopeId_included() {
      UUID scopeId = UUID.fromString("00000000-0000-0000-0000-000000000123");
      ScopeQueryParams q = ScopeQueryParams.builder()
          .scopeId(scopeId)
          .build();
      assertEquals("?first=0&max=100&scopeId=" + scopeId, q.toQueryString());
   }


   @ParameterizedTest(name = "5.{index}) name=\"{0}\" -> exact string")
   @CsvSource({
       "'',?first=0&max=100&name=",
       "'scopeX',?first=0&max=100&name=scopeX"
   })
   @DisplayName("5. name empty/non-empty -> exact string")
   void name_edgeCases_exact(String name, String expected) {
      ScopeQueryParams q = ScopeQueryParams.builder()
          .name(name)
          .build();
      assertEquals(expected, q.toQueryString());
   }

}
