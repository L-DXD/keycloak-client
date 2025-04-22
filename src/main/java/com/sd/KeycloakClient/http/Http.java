package com.sd.KeycloakClient.http;

import com.sd.KeycloakClient.config.ClientConfiguration;
import io.netty.handler.codec.http.HttpMethod;
import lombok.RequiredArgsConstructor;

/**
 * @author SangWonYu
 * @date 2024-04
 * @description This provides http method.
 */


@RequiredArgsConstructor
public class Http {

   private final ClientConfiguration configuration;

   public <T> HttpExecutor<T> get(String uri) {
      return create(HttpMethod.GET, uri);
   }

   public <T> HttpExecutor<T> post(String uri) {
      return create(HttpMethod.POST, uri);
   }

   public <T> HttpExecutor<T> patch(String uri) {
      return create(HttpMethod.PATCH, uri);
   }

   public <T> HttpExecutor<T> put(String uri) {
      return create(HttpMethod.PUT, uri);
   }

   public <T> HttpExecutor<T> delete(String uri) {
      return create(HttpMethod.DELETE, uri);
   }

   private <T> HttpExecutor<T> create(HttpMethod method, String uri) {
      return new HttpExecutor<>(configuration, method, uri);
   }
}

