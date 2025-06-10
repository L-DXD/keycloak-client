package com.sd.KeycloakClient.dto;

import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KeycloakResponse<T> {

   private int status;
   private String message;
   private String responseLocation;
   private T body;

   public static <T> KeycloakResponse<T> of(final int status, final String message, final T body, final String location) {
      return KeycloakResponse.<T>builder()
          .status(status)
          .message(message)
          .responseLocation(location)
          .body(body)
          .build();
   }

   public Optional<T> getBody() {
      return Optional.ofNullable(body);
   }
}
