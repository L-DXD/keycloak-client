package com.sd.KeycloakClient.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeycloakClientTokenInfo {

   @JsonProperty("access_token")
   private String accessToken;

   @JsonProperty("expires_in")
   private Integer expireTime;

   @JsonProperty("refresh_expires_in")
   private Integer refreshExpireTime;

   @JsonProperty("token_type")
   private String tokenType;

   @JsonProperty("scope")
   private String scope;

}
