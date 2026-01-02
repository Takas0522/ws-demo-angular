# Shared Library

共有ライブラリ - 3つのマイクロサービス（Auth, User, Permission）で使用される共通コンポーネント

## 概要

このライブラリには以下のコンポーネントが含まれています：

- JWT トークンの生成と検証
- 共通 DTO (Data Transfer Objects)
- 内部 API 認証フィルター
- RestTemplate 設定（タイムアウトとリトライ）
- 共通例外クラス

## 依存関係

このライブラリを使用するには、`pom.xml` に以下を追加してください：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>shared-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```

## コンポーネント

### JWT コンポーネント

#### JwtTokenProvider
JWT トークンを生成します。

```java
@Autowired
private JwtTokenProvider tokenProvider;

// トークンを生成
String token = tokenProvider.generateToken(userId);

// クレーム付きでトークンを生成
Map<String, Object> claims = new HashMap<>();
claims.put("roles", List.of("USER", "ADMIN"));
String token = tokenProvider.generateToken(userId, claims);
```

#### JwtValidator
JWT トークンを検証します。

```java
@Autowired
private JwtValidator tokenValidator;

// トークンを検証してクレームを取得
Claims claims = tokenValidator.validateToken(token);

// サブジェクト（ユーザーID）を取得
String userId = tokenValidator.getSubject(token);

// 特定のクレームを取得
Object value = tokenValidator.getClaim(token, "claimKey");

// トークンの有効期限をチェック
boolean isExpired = tokenValidator.isTokenExpired(token);
```

### DTO クラス

#### UserInfoDto
ユーザー情報を表現します。

```java
UserInfoDto user = UserInfoDto.builder()
    .id(1L)
    .username("user123")
    .email("user@example.com")
    .roles(List.of("USER"))
    .permissions(List.of())
    .build();
```

#### PermissionDto
権限情報を表現します。

```java
PermissionDto permission = PermissionDto.builder()
    .id(1L)
    .name("READ_USER")
    .resource("user")
    .action("read")
    .description("Read user data")
    .build();
```

#### AuthResponseDto
認証レスポンスを表現します。

```java
AuthResponseDto response = AuthResponseDto.builder()
    .token("jwt-token")
    .tokenType("Bearer")
    .expiresIn(3600L)
    .user(userInfo)
    .build();
```

#### ErrorResponseDto
エラーレスポンスを表現します。

```java
ErrorResponseDto error = ErrorResponseDto.builder()
    .status(404)
    .error("Not Found")
    .message("User not found")
    .path("/api/users/123")
    .timestamp(LocalDateTime.now())
    .build();
```

### フィルター

#### InternalApiAuthFilter
内部 API 呼び出しに対して JWT 認証を行います。

```java
@Configuration
public class SecurityConfig {
    
    @Autowired
    private JwtValidator jwtValidator;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(
            new InternalApiAuthFilter(jwtValidator),
            UsernamePasswordAuthenticationFilter.class
        );
        return http.build();
    }
}
```

### 設定

#### RestTemplateConfig
30秒のタイムアウトと3回のリトライを持つ RestTemplate を提供します。

```java
@Autowired
private RestTemplate restTemplate;

@Autowired
private RetryTemplate retryTemplate;

// RestTemplate を使用
ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

// リトライ付きで実行
String result = retryTemplate.execute(context -> {
    return restTemplate.getForObject(url, String.class);
});
```

### 例外クラス

- `UnauthorizedException` - 認証されていないアクセス
- `ResourceNotFoundException` - リソースが見つからない
- `BadRequestException` - 不正なリクエスト

```java
if (user == null) {
    throw new ResourceNotFoundException("User not found with id: " + userId);
}

if (!isAuthorized) {
    throw new UnauthorizedException("User is not authorized to access this resource");
}

if (invalidInput) {
    throw new BadRequestException("Invalid input data");
}
```

## 設定

### application.properties / application.yml

```properties
# JWT 設定
jwt.private-key-path=/keys/private_key.pem
jwt.public-key-path=/keys/public_key.pem
jwt.expiration=3600000
```

## ビルド

```bash
mvn clean install
```

## テスト

```bash
mvn test
```

## ライセンス

このプロジェクトは社内利用のみです。
