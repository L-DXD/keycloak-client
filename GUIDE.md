# Project application guide

1. Add dependancy
2. Set keycloak information using the 'KeycloakConfig' class
3. Manage authentication/authorization/username via 'KeycloakClient' interface

## Add dependancy

- maven
```

```

- gradle
```

```

## Set KeycloakConfig
```java
@Configuration
@Getter
public class KeycloakConfig extends AbstractKeycloakConfig {

    @Value("${}")
    public String realmName;

    @Value("${}")
    public String baseUrl;

    @Value("${}")
    public String relativePath;

    @Value("${}")
    public String clientId;

    @Value("${}")
    public String redirectUri;

    @Value("${}")
    public String logoutRedirectUri;

    @Value("${}")
    public String responseType;

    @Value("${}")
    public String clientSecret;

    @Override
    @Bean
    public KeycloakClient keycloakClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .baseUrl(baseUrl)
                .realmName(realmName)
                .relativePath(relativePath)
                .clientId(clientId)
                .redirectUri(redirectUri)
                .logoutRedirectUri(logoutRedirectUri)
                .responseType(responseType)
                .clientSecret(clientSecret)
                .build();

        return new KeycloakClient(clientConfiguration);
    }
}
```
    
```java

// test using
@RequireArgsConstructor
public class Test {
    private final KeycloakClient keycloakClient;
    
    private void test() {
        keycloakClient.authAsync().logout("refreshToken");
        keycloakClient.auth().logout("refreshToken");
        keycloakClient.userAsync().getUserInfo("accessToken");
        keycloakClient.user().getUserInfo("accessToken");
        ...
    }
}
```

## KeycloakAuthClient
 Sync Functions provided by [this class](src/main/java/com/sd/KeycloakClient/client/auth/sync/KeycloakAuthClient.java)  
 Async Functions provided by [this class](src/main/java/com/sd/KeycloakClient/client/auth/async/KeycloakAuthAsyncClient.java)

Invocation Type| Function                | Return      |Description  
---|-------------------------|-------------|---  
Synchronous| KeycloakAuthClient      | Value       | Synchronous features for auth
Asynchronous| KeyclaokAuthAsyncClient | Mono<Value> |Asynchronous features for auth
...

## KeycloakUserClient
Sync Functions provided by [this class](src/main/java/com/sd/KeycloakClient/client/user/sync/KeycloakUserClient.java)  
Async Functions provided by [this class](src/main/java/com/sd/KeycloakClient/client/user/async/KeycloakUserAsyncClient.java)

Invocation Type| Function                | Return      |Description  
---|-------------------------|-------------|---  
Synchronous| KeycloakUserClient      | Value       | Synchronous features for user
Asynchronous| KeyclaokUserAsyncClient | Mono<Value> |Asynchronous features for user

...