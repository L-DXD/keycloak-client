package com.sd.KeycloakClient.dto.user;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

public class KeycloakUserInfo {

   public static final String SUBJECT = "sub";
   public static final String PREFERRED_USERNAME = "preferred_username";

   @JsonProperty(SUBJECT)
   private String subject;

   @JsonProperty(PREFERRED_USERNAME)
   private String preferredUsername;

   private String email;

   private String name;

   protected Map<String, Object> otherInfo = new HashMap<>();


   /**
    * This is a map of any other user info. Could be custom user attribute set up by the auth server
    *
    * @return
    */
   @JsonAnyGetter
   public Map<String, Object> getOtherInfo() {
      return otherInfo;
   }

   @JsonAnySetter
   public void setOtherInfo(String name, Object value) {
      otherInfo.put(name, value);
   }
}
