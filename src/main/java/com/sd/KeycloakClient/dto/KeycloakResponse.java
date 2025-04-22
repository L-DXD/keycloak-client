package com.sd.KeycloakClient.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KeycloakResponse<T> {

   private int status;
   private String message;
   private T body;

   public static <T> KeycloakResponse<T> of(final int status, final String message, final T body) {
      return KeycloakResponse.<T>builder()
          .status(status)
          .message(message)
          .body(body)
          .build();
   }
}
