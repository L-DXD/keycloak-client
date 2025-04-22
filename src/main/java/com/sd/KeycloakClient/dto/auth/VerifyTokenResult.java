package com.sd.KeycloakClient.dto.auth;

import lombok.Builder;
import lombok.Getter;
import org.keycloak.representations.JsonWebToken;

/**
 * @author SangWonYu
 * @date 2025-04
 */

@Builder
@Getter
public class VerifyTokenResult {

   private Boolean active;
   private JsonWebToken token;

   public static VerifyTokenResult of(Boolean active, JsonWebToken token) {
      return VerifyTokenResult.builder()
          .active(active)
          .token(token)
          .build();
   }

}
