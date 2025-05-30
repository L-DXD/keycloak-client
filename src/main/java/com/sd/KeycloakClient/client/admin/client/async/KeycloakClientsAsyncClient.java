package com.sd.KeycloakClient.client.admin.client.async;

import com.sd.KeycloakClient.dto.KeycloakResponse;
import com.sd.KeycloakClient.dto.admin.ClientQueryParams;
import org.keycloak.representations.idm.ClientRepresentation;
import reactor.core.publisher.Mono;

public interface KeycloakClientsAsyncClient {

   Mono<KeycloakResponse<ClientRepresentation[]>> getClientsInfo(String accessToken, ClientQueryParams clientQueryParams);
}
