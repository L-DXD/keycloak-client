package com.sd.KeycloakClient.http;

import static com.sd.KeycloakClient.enumType.Headers.BASIC;
import static com.sd.KeycloakClient.enumType.Headers.BEARER;
import static com.sd.KeycloakClient.util.UrlUtil.toUrlEncoded;
import static io.netty.handler.codec.http.HttpHeaderNames.AUTHORIZATION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sd.KeycloakClient.config.ClientConfiguration;
import com.sd.KeycloakClient.dto.KeycloakResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.AsciiString;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

/**
 * @author SangWonYu
 * @date 2025-04
 * @description HttpExecutor is an object used to make HTTP requests and is invoked through the Http object.
 */


public class HttpExecutor<T> {

   private final HttpMethod method;
   private final String uri;
   private final HttpClient httpClient;

   public HttpExecutor(final ClientConfiguration config, final HttpMethod method, final String uri) {
      this.method = method;
      this.uri = uri;

      ConnectionProvider provider = ConnectionProvider.builder("limited")
          .maxConnections(config.getMAX_CONNECTIONS())
          .pendingAcquireMaxCount(config.getPENDING_ACQUIRE_MAX_COUNT())
          .pendingAcquireTimeout(config.getPENDING_ACQUIRE_TIMEOUT())
          .build();
      this.httpClient = HttpClient.create(provider)
          .baseUrl(config.getBaseUrl())
          .wiretap(true);
   }

   private Map<AsciiString, Object> headers = new HashMap<>(Map.of(CONTENT_TYPE, APPLICATION_JSON));
   private Map<String, Object> entities = new HashMap<>();
   private Class<T> responseType;

   private final ObjectMapper objectMapper = new ObjectMapper();

   public HttpExecutor<T> headers(final Map<AsciiString, Object> headers) {
      this.headers = headers;
      return this;
   }

   public HttpExecutor<T> responseType(final Class<T> responseType) {
      this.responseType = responseType;
      return this;
   }

   public HttpExecutor<T> authorizationBearer(String token) {
      headers.put(AUTHORIZATION, BEARER + token);
      return this;
   }

   public HttpExecutor<T> authorizationBasic(String clientId, String clientSecret) {
      headers.put(AUTHORIZATION,
          BASIC + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8)));
      return this;
   }

   public HttpExecutor<T> applicationFormUrlencoded() {
      headers.put(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
      return this;
   }

   public HttpExecutor<T> applicationJson() {
      headers.put(CONTENT_TYPE, APPLICATION_JSON);
      return this;
   }

   public HttpExecutor<T> entities(final Map<String, Object> entities) {
      this.entities = entities;
      return this;
   }

   public Mono<KeycloakResponse<T>> send() {
      try {
         if (method == HttpMethod.GET) {
            return responseReceiver(httpClient.headers(this::setHeaders).get().uri(uri));
         }
         return responseReceiver(createSender().send(getSendDataByHeader()));
      } catch (Exception e) {
         return Mono.error(e);
      }

   }

   private Mono<KeycloakResponse<T>> responseReceiver(HttpClient.ResponseReceiver<?> receiver) {
      return receiver.responseSingle(((response, byteBufMono) -> byteBufMono.asString()
          .defaultIfEmpty("")
          .flatMap((responseBody) -> {
             int statusCode = response.status().code();
             String reason = response.status().reasonPhrase();

             // 204 No Content
             if (statusCode == NO_CONTENT.code()) {
                return Mono.just(KeycloakResponse.of(statusCode, response.status().reasonPhrase(), null));
             }
             // 3XX Redirection
             if (statusCode >= 300 && statusCode < 400) {
                return Mono.just(KeycloakResponse.of(statusCode, reason, null));
             }

             // 2XX
             if (statusCode >= 200 && statusCode < 300) {
                try {
                   T parsed = null;
                   if (responseType != null && !responseBody.isBlank() && !Void.class.equals(responseType)) {
                      parsed = objectMapper.readValue(responseBody, responseType);
                   }

                   return Mono.just(KeycloakResponse.of(statusCode, reason, parsed));
                } catch (JsonProcessingException e) {
                   return Mono.error(new RuntimeException("Error parsing JSON response.", e));
                }
             }

             return Mono.just(KeycloakResponse.of(statusCode, reason + " : " + responseBody, null));
          })));
   }

   private ByteBufFlux getSendDataByHeader() throws JsonProcessingException {
      if (this.headers.get(CONTENT_TYPE).equals(APPLICATION_X_WWW_FORM_URLENCODED)) {
         return ByteBufFlux.fromString(Mono.just(toUrlEncoded(entities)));
      }

      String jsonBody = objectMapper.writeValueAsString(entities);
      return ByteBufFlux.fromString(Mono.just(jsonBody));
   }


   private HttpClient.RequestSender createSender() {
      if (HttpMethod.POST.equals(method)) {
         return httpClient.headers(this::setHeaders).post().uri(uri);
      } else if (HttpMethod.PUT.equals(method)) {
         return httpClient.headers(this::setHeaders).put().uri(uri);
      } else if (HttpMethod.DELETE.equals(method)) {
         return httpClient.headers(this::setHeaders).delete().uri(uri);
      }
      throw new IllegalArgumentException("Unsupported http method: " + method);
   }

   private void setHeaders(HttpHeaders clientHeaders) {
      for (Map.Entry<AsciiString, Object> header : headers.entrySet()) {
         clientHeaders.add(header.getKey(), header.getValue());
      }
   }
}
