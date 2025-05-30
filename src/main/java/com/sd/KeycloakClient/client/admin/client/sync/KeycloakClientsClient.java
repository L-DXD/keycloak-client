package com.sd.KeycloakClient.client.admin.client.sync;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.ClientQueryParams;
import org.keycloak.representations.idm.ClientRepresentation;

public interface KeycloakClientsClient {

   KeycloakResponse<ClientRepresentation[]> getClientsInfo(String accessToken, ClientQueryParams clientQueryParams);

}
