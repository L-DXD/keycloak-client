package com.sd.KeycloakClient.dto.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sd.KeycloakClient.util.UrlUtil;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ResourceQueryParamsTest {

   @Test
   @DisplayName("1. no parameters provided -> default first/max only")
   void noParams_returnsFirstMaxOnly() {
      ResourceQueryParams q = new ResourceQueryParams();
      String s = q.toQueryString();
      assertEquals("?first=0&max=100", s);
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
      ResourceQueryParams q = ResourceQueryParams.builder()
          .first(first)
          .max(max)
          .build();
      assertEquals(expected, q.toQueryString());
   }

   @ParameterizedTest(name = "3.{index}) name=\"{0}\" -> contains encoded name fragment")
   @ValueSource(strings = {"doc", "read write", "읽기", "read/write", "a+b=c&d"})
   @DisplayName("3. name provided -> URL-encoded and included")
   void name_included_andEncoded(String name) {
      ResourceQueryParams q = ResourceQueryParams.builder()
          .name(name)
          .build();
      String encoded = UrlUtil.toUrlEncoded(Map.of("name", name));
      String s = q.toQueryString();
      System.out.println("s = " + s);
      assertThat(s).contains(encoded);
   }

   @Test
   @DisplayName("4. id provided -> included as UUID")
   void id_included() {
      UUID id = UUID.fromString("00000000-0000-0000-0000-000000000123");
      ResourceQueryParams q = ResourceQueryParams.builder()
          .id(id)
          .build();
      assertEquals("?first=0&max=100&id=" + id, q.toQueryString());
   }

   @ParameterizedTest(name = "5.{index}) deep={0}, exactName={1} -> contains boolean fragments")
   @CsvSource({
       "true,true",
       "true,false",
       "false,true",
       "false,false"
   })
   @DisplayName("5. boolean flags (deep, exactName) -> included when set")
   void booleans_included(Boolean deep, Boolean exactName) {
      ResourceQueryParams q = ResourceQueryParams.builder()
          .deep(deep)
          .exactName(exactName)
          .build();
      String s = q.toQueryString();
      assertThat(s).contains("deep=" + deep).contains("exactName=" + exactName);
   }

   @ParameterizedTest(name = "6.{index}) key={0}, value={1} -> contains encoded fragment")
   @CsvSource({
       "owner, user-123",
       "scope, read:all",
       "type, urn:res:type",
       "uri, /api/resources/1",
       "matchingUri, /api/resources/*"
   })
   @DisplayName("6. string filters (owner, scope, type, uri, matchingUri) -> URL-encoded and included")
   void stringFilters_included_andEncoded(String key, String value) {
      // 빌더에 동적으로 주입
      ResourceQueryParams.ResourceQueryParamsBuilder b = ResourceQueryParams.builder();
      switch (key) {
         case "owner":
            b.owner(value);
            break;
         case "scope":
            b.scope(value);
            break;
         case "type":
            b.type(value);
            break;
         case "uri":
            b.uri(value);
            break;
         case "matchingUri":
            b.matchingUri(value);
            break;
         default:
            throw new IllegalArgumentException("unknown key: " + key);
      }
      ResourceQueryParams q = b.build();

      String expected = UrlUtil.toUrlEncoded(Map.of(key, value));
      String s = q.toQueryString();
      assertThat(s).contains(expected);
   }

   @ParameterizedTest(name = "7.{index}) name=\"{0}\" -> exact string (empty/non-empty)")
   @CsvSource({
       "'',?first=0&max=100&name=",
       "'file',?first=0&max=100&name=file"
   })
   @DisplayName("7. name empty/non-empty -> exact string")
   void name_edgeCases_exact(String name, String expected) {
      ResourceQueryParams q = ResourceQueryParams.builder()
          .name(name)
          .build();
      assertEquals(expected, q.toQueryString());
   }

   @Test
   @DisplayName("8. multiple filters compose -> contains all pairs (order not strictly asserted)")
   void multipleFilters_compose() {
      UUID id = UUID.fromString("00000000-0000-0000-0000-000000000abc");
      ResourceQueryParams q = ResourceQueryParams.builder()
          .first(5)
          .max(20)
          .id(id)
          .deep(true)
          .exactName(false)
          .name("my res")
          .owner("user-1")
          .scope("read")
          .type("urn:type")
          .uri("/x/y")
          .matchingUri("/x/*")
          .build();

      String s = q.toQueryString();

      // 고정 전체 문자열 비교 대신, 핵심 쌍들이 포함되는지를 확인 (인코딩 포함)
      assertThat(s).startsWith("?first=5&max=20");
      assertThat(s).contains("id=" + id);
      assertThat(s).contains("deep=true");
      assertThat(s).contains("exactName=false");

      // 인코딩 되는 항목들
      assertThat(s).contains(UrlUtil.toUrlEncoded(Map.of("name", "my res")));
      assertThat(s).contains(UrlUtil.toUrlEncoded(Map.of("owner", "user-1")));
      assertThat(s).contains(UrlUtil.toUrlEncoded(Map.of("scope", "read")));
      assertThat(s).contains(UrlUtil.toUrlEncoded(Map.of("type", "urn:type")));
      assertThat(s).contains(UrlUtil.toUrlEncoded(Map.of("uri", "/x/y")));
      assertThat(s).contains(UrlUtil.toUrlEncoded(Map.of("matchingUri", "/x/*")));
   }
}
