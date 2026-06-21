# CLAUDE.md

이 파일은 Claude Code (claude.ai/code)가 이 저장소에서 작업할 때 참고하는 가이드입니다.

## 서비스 개요

**체험단 매칭 플랫폼** — 블로거·인플루언서를 위한 체험단 공고 통합 조회 & AI 기반 맞춤 추천 서비스.
이 저장소는 Spring Boot API Server이며, 전체 시스템의 비즈니스 로직 중심 서버다.

## 명령어

```bash
# 인프라 시작 (PostgreSQL + Redis + RabbitMQ)
docker-compose up -d

# 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun

# 전체 테스트 실행
./gradlew test

# 단일 테스트 클래스 실행
./gradlew test --tests "com.ject6.boost.domain.user.application.service.UserServiceTest"
```

## 아키텍처

Spring Boot 3.5 / Java 17 REST API. 패키지 루트는 `com.ject6.boost`.

### 패키지 구조

```
common/
  config/          # QuerydslConfig, RabbitMQConfig, SecurityConfig, SwaggerConfig
  dto/             # ApiResponse<T>, PagedResponse<T>
  exception/       # ErrorCode 인터페이스, BusinessException, GlobalExceptionHandler
  queue/           # AnalysisMessage, AnalysisQueuePublisher (RabbitMQ 발행)
  redis/           # ViewerCountService (실시간 조회자 수)
  security/        # JWT 필터, JwtTokenProvider, AuthenticatedUser

domain/<name>/
  application/
    service/       # 비즈니스 로직
    exception/     # 도메인별 ErrorCode 열거형
  domain/
    entity/        # JPA 엔티티
    constant/      # 도메인 열거형
    repository/    # 도메인 레포지토리 인터페이스 (JPA 의존 없음)
  infrastructure/
    repository/    # Spring Data JPA 인터페이스 (*JpaRepository)
    impl/          # 도메인 레포지토리 인터페이스를 구현하는 어댑터
    client/        # 외부 HTTP 클라이언트 (blog, user 도메인에 존재)
  presentation/
    controller/    # REST 컨트롤러
    controller/docs/ # Swagger *Api 인터페이스 (컨트롤러와 분리)
    dto/           # 요청/응답 레코드
```

### 주요 패턴

**레포지토리 이중 레이어**: 도메인 코드는 `domain/repository/` 인터페이스(예: `UserRepository`)에만 의존한다. `infrastructure/repository/`에는 JPA 인터페이스(`UserJpaRepository`)가, `infrastructure/impl/`에는 어댑터(`UserRepositoryImpl`)가 위치한다. 서비스는 JPA가 아닌 도메인 인터페이스를 주입받는다.

**Swagger 분리**: 각 컨트롤러는 `docs/` 하위 패키지의 `*Api` 인터페이스를 구현한다. `@Operation`, `@ApiResponse` 등 Swagger 어노테이션은 모두 인터페이스에 작성하여 컨트롤러를 깔끔하게 유지한다.

**에러 처리**: `ErrorCode` 인터페이스를 구현하는 열거형으로 에러를 정의한다(`AuthErrorCode`, `UserErrorCode`, `GlobalErrorCode` 참고). `BusinessException(errorCode)`를 던지면 `GlobalExceptionHandler`가 자동으로 `ApiResponse` 실패 응답으로 변환한다.

**API 응답 봉투**: 모든 엔드포인트는 `ApiResponse<T>`를 반환한다. 성공 시 `ApiResponse.success(data)`, 실패 시 예외 핸들러가 `ApiResponse.failure(...)`를 생성한다.

**소프트 삭제**: 엔티티에 `deletedAt` 컬럼이 있다. 활성 레코드 조회는 `deletedAt IS NULL` 조건을 사용한다(예: `findByIdAndDeletedAtIsNull`).

### 인증 흐름

1. 브라우저가 `/oauth2/authorization/kakao`로 리다이렉트 → Spring OAuth2가 코드 교환 처리.
2. `OAuth2LoginSuccessHandler`가 `AuthService.login()`을 호출하여 `User` + `UserOAuthAccount`를 upsert.
3. 액세스 토큰(JWT)은 응답 바디에, 리프레시 토큰은 Redis에 `refresh:<tokenId>` 키로 저장.
4. `JwtAuthenticationFilter`가 이후 요청의 `Authorization: Bearer <token>`을 검증하고 `SecurityContextHolder`에 `AuthenticatedUser`를 저장.
5. `/auth/refresh`는 리프레시 토큰을 받아 Redis에서 검증 후 새 액세스 토큰 발급.

## 도메인 현황

Spring 서버가 담당하는 도메인과 현재 구현 상태.

| ID | 도메인 | 핵심 책임 | 상태 |
|---|---|---|---|
| D01 | Auth | 소셜 로그인(카카오) · JWT 발급/검증 | ✅ 완료 |
| D02 | User | 회원 정보 · 블로그 연동 · 온보딩 프로필 | ✅ 완료 |
| D03 | Subscription | 구독 플랜 · AI 크레딧 차감/충전 | ⏳ 미구현 |
| D04 | Campaign | 공고 조회·필터·검색·좋아요 (`campaigns` 테이블은 읽기 전용, 크롤러가 write) | ✅ 완료 |
| D05 | Feed | 유저 상태별 홈피드 (비로그인 / 로그인-미연동 / 로그인-연동 3분기) | ✅ 완료 (`campaign` 패키지 내 `FeedController`) |
| D06 | My | 마이페이지 · 포인트 지갑(최소 출금 5,000P) · 내 체험단 현황 | ✅ 완료 |
| D07 | Onboarding | 비로그인 4단계 채팅 추천 · 로그인 전환 시 `session_id` → `user_id` 병합 | ✅ 완료 |
| D08 | Blog AI Bridge | 블로그 분석 트리거 · Python AI 서버 연동 · pgvector 추천 | ✅ 완료 (`blog` 패키지) |

> D05 Feed는 별도 패키지 없이 `domain/campaign/presentation/controller/FeedController`에 구현됨.
> D08 BlogAnalysis 핵심 로직은 Python 서버가 담당. Spring의 `blog` 도메인은 트리거 발행, 결과 조회, AI 챗봇 프록시, pgvector 추천 검색을 처리.

### 개발 순서 (의존성 기반)

```
STEP 1: 공통 인프라 (DB, Redis, RabbitMQ, 예외 처리, Security 기본 설정)  ✅
STEP 2: D01 Auth        ← 전체 도메인의 보안 기반                          ✅
STEP 3: D02 User        ← 선행: D01                                        ✅
STEP 4: D03 Subscription ← 선행: D02. 크레딧 차감 시 동시성 처리(낙관적 락) ⏳
STEP 5: D04 Campaign    ← 동적 쿼리(Querydsl) 필요                         ✅
STEP 6: D07 Onboarding  ← 선행: D04                                        ✅
STEP 7: D05 Feed        ← 선행: D04 + Analyze Server 가동                  ✅
STEP 8: D06 My          ← 선행: D04 + D03                                  ✅
```

## 외부 연동

### RabbitMQ — 블로그 분석 트리거

블로그 분석 요청 시 Queue에 발행 → Python Analyze Server가 Consume하여 LLM 분석 처리.

```
Exchange:     blog.analysis
Routing Key:  analysis.request
메시지:       { user_id, document_id }
DLQ:          blog.analysis.dlx → blog.analysis.dlq (최대 3회 재시도 후 이동)
```

### Python Analyze Server — HTTP Client (`blog` 도메인)

`/v1/*` 경로는 Python 내부 API로 **클라이언트가 직접 호출 불가**, Spring이 `PythonAiClient`로만 호출한다.
클라이언트 코드 위치: `domain/blog/infrastructure/client/PythonAiClient.java`

| 목적 | 엔드포인트 | 사용 도메인 |
|---|---|---|
| 분석 결과 조회 | `GET /v1/analysis/documents/{document_id}` | D06, D08 |
| 챗봇 메시지 전송 | `POST /v1/conversations/messages` | D07, D08 |
| 세션 초기화 | `DELETE /v1/conversations/{session_id}` | D06 |

### pgvector — AI 추천 공고

`domain/blog/infrastructure/impl/PgvectorBlogRecommendationRepository.java`가 Spring AI(`spring-ai-starter-vector-store-pgvector`)를 사용해 pgvector를 직접 조회한다.

## 주요 엔티티

| 도메인 | 엔티티 |
|---|---|
| user | `User`, `UserOAuthAccount`, `UserBlog`, `UserRegion`, `UserActivityType`, `UserCategory`, `Region`, `BlogAnalysisResult` |
| campaign | `Campaign`, `UserCampaign` |
| my | `PointWallet`, `PointTransaction` |
| onboarding | `OnboardingResponse` |

## 공통 Enum

새 도메인 추가 시 아래 Enum 값을 참고해 상수를 정의한다.

```
CATEGORY: FOOD, BEAUTY, FASHION, LIFE, PET, TECH, TRAVEL, CULTURE, ETC
TYPE:     VISIT, DELIVERY, REPORTER, REVIEW, PAYBACK
CHANNEL:  BLOG, INSTA, YOUTUBE, SHORT_FORM, ETC
REGION:   ALL, SEOUL, GYEONGGI, INCHEON, BUSAN, DAEGU, GWANGJU, DAEJEON, ULSAN,
          SEJONG, GANGWON, CHUNGBUK, CHUNGNAM, JEONBUK, JEONNAM, GYEONGBUK,
          GYEONGNAM, JEJU, FOREIGN, ETC
```

## 인프라

- **PostgreSQL** (pgvector 이미지) — pgvector 확장: AI 추천용 임베딩 유사도 검색을 Spring이 직접 조회.
- **Redis** — 리프레시 토큰 세션, 공고 리스트 캐시(5분), 히어로 배너 캐시(10분), 실시간 조회자 수, 온보딩 세션(24시간).
- **RabbitMQ** — 블로그 분석 비동기 처리 (LLM 응답 지연 대응).
- **Flyway** — 마이그레이션 파일 위치: `src/main/resources/db/migration`. 개발 환경에서는 `ddl-auto: update`로 스키마 관리.

## 환경 변수

모든 설정은 `application.yaml`에 기본값이 있다. 정상 동작을 위해 **반드시** 설정해야 하는 변수:

| 변수 | 용도 |
|---|---|
| `KAKAO_OAUTH_CLIENT_ID` / `KAKAO_OAUTH_CLIENT_SECRET` | OAuth 로그인 (현재 카카오만 구현) |
| `JWT_SECRET` | 토큰 서명 (로컬 외 환경에서는 반드시 변경) |
| `SPRING_DATASOURCE_URL/USERNAME/PASSWORD` | DB 연결 |
| `SPRING_DATA_REDIS_HOST/PORT` | Redis 연결 |
| `ANALYZE_SERVER_URL` | Python Analyze Server URL (blog/feed 연동 시 필요) |
