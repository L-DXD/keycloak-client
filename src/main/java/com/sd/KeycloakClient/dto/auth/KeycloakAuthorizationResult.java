package com.sd.KeycloakClient.dto.auth;

import lombok.Builder;
import lombok.Getter;

/**
 * @author SangWonYu
 * @date 2025-04-03
 * @description authorization result object
 */

@Builder
@Getter
public class KeycloakAuthorizationResult {

   private boolean granted;
   private KeycloakAuthorizationResponse authorizationResponse;
}
