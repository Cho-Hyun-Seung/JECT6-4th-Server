# JECT Main API Server (ject_api)

체험단 매칭 플랫폼의 **Spring Boot 메인 API 서버**.
인증(OAuth)·사용자·체험단 공고·피드·마이페이지·블로그 AI 분석 요청·진단 쿼터·
온보딩 추천 등 비즈니스 로직 전체를 담당하는 시스템의 중심 서버입니다.

AI 변환 작업(청킹·임베딩·LLM 분석·진단·채팅)은 Analyzer(`ject`)에,
웹 수집은 Crawler(`ject_crawl`)에 위임하고, 이 서버는 요청 접수·캐시/락/쿼터 관리·
결과 조회·이벤트 수신·pgvector 기반 추천 조회를 맡습니다.

---

## 시스템 컨텍스트

```
Frontend (Next.js)
   │  Nginx 게이트웨이 경유: /api/* /campaigns /feed/* /my/* /blog/* /onboarding/* ...
   ▼
Spring Boot API (이 서버)
   ├─ HTTP 동기 프록시 ────▶ Analyzer  (진단 /v1/diagnosis, 채팅 /v1/conversations,
   │                                    프로필 임베딩 /v1/profile/embed, 분석 결과 조회)
   ├─ RabbitMQ blog.analysis 발행 ──▶ Analyzer analysis-worker (비동기 분석)
   ├─ RabbitMQ blog.analysis.completed 수신 ◀── Analyzer (완료 이벤트)
   ├─ HTTP 트리거 ────▶ Crawler  (POST /crawl/blog-posts — fire-and-forget)
   │       ◀──── Crawler가 /internal/campaigns/bulk 로 공고 upsert
   ├─ PostgreSQL(+pgvector) — 비즈니스 데이터 + 벡터 추천 직접 조회
   └─ Redis — 분석 캐시/분산 락/멱등 처리/쿼터 보조
```

## 기술 스택·아키텍처

Spring Boot / Java 17. 패키지 루트 `com.ject6.boost`, 도메인별 레이어드 구조.

```
com.ject6.boost/
├── presentation/     컨트롤러 (auth, user, campaign, my, blog, onboarding, common)
├── application/      서비스 — 비즈니스 로직
├── domain/           엔티티·도메인 모델
└── infrastructure/   외부 연동 — RabbitMQConfig, AnalysisQueuePublisher,
                      AnalysisCompletedListener, PythonAiClient, CrawlerClient,
                      AnalysisCacheService(Redis)
```

주요 의존성: Spring Data JPA + QueryDSL, Spring Security + OAuth2 Client(Kakao),
Spring AMQP(RabbitMQ), Spring Data Redis, Spring AI pgvector vector store.

```bash
./gradlew build        # 빌드
./gradlew bootRun      # 실행
./gradlew test         # 테스트
```

---

## API 목록

### Auth / User (`/api` 프리픽스)

| Method | Path | 설명 |
|---|---|---|
| GET/POST | `/api/auth/login/{provider}` | OAuth 로그인 진입 / code 기반 로그인 처리 |
| POST | `/api/auth/logout` | 로그아웃 |
| POST | `/api/auth/refresh` | 리프레시 토큰으로 액세스 토큰 재발급 |
| POST | `/api/auth/demo-login` | 데모 로그인 |
| GET/PATCH | `/api/users/me` | 내 프로필 조회 / 수정 |
| DELETE | `/api/users/me` | 회원 탈퇴 |
| POST | `/api/users/me/blog` | 블로그 URL 연동 (초기 색인은 크롤러에 비동기 위임) |
| GET | `/api/users/nickname/check`, `/nickname/random` | 닉네임 중복 확인 / 랜덤 생성 |

### Campaign / Feed

| Method | Path | 설명 |
|---|---|---|
| GET | `/campaigns` | 공고 목록 (필터·정렬·페이지네이션) |
| GET | `/campaigns/{id}` | 공고 상세 |
| GET | `/campaigns/{id}/viewers`, `/{id}/related`, `/{id}/likes/analysis` | 상세 부가 정보 |
| GET | `/campaigns/search` | 벡터/키워드 검색 (임베딩 provider 설정 필요) |
| GET | `/campaigns/popular`, `/guaranteed`, `/closing-soon` | 인기 / 100% 당첨 / 마감임박 |
| POST | `/campaigns/{id}/like`, `/{id}/apply` | 좋아요 토글 / 지원 |
| GET | `/feed/hero`, `/feed/body`, `/feed/blogger-stories` | 홈 피드 섹션 |
| POST | `/internal/campaigns/bulk` | Crawler 전용 공고 bulk upsert (내부 API) |

### My Page (`/my`)

| Method | Path | 설명 |
|---|---|---|
| GET | `/my`, `/my/account` | 마이페이지 계정 요약 |
| GET | `/my/campaigns` (+`/likes`, `/applies`, `/recent-views`, `/recent-applies`, `/{id}`) | 내 캠페인 활동 |
| GET | `/my/ai-history` | 지난 AI 분석·진단 이력 |
| GET/POST | `/my/points`, `/my/points/withdraw` | 포인트 조회 / 출금 |

### Blog AI / Onboarding

| Method | Path | 설명 |
|---|---|---|
| POST | `/blog/analyze` | FULL_BLOG 또는 POST 분석 요청 (캐시·락·쿼터 적용) |
| GET | `/blog/analysis/{documentId}` | Analyzer 최근 분석 결과 조회 프록시 |
| GET | `/blog/analysis/history` | `blog_analysis_results` 기반 분석 이력 |
| GET | `/blog/analysis/{analysisId}/recommendations` | pgvector 추천 (없으면 active campaign fallback) |
| GET | `/blog/analysis/{analysisId}/bloggers` | 카테고리별 인플루언서 후보 |
| POST | `/blog/diagnosis` | Analyzer 6지표 진단 동기 프록시 |
| GET | `/blog/diagnosis/quota` | 무료 진단 쿼터 조회 |
| POST | `/blog/chat` | 분석 기반 AI 채팅 프록시 |
| DELETE | `/blog/chat/{sessionId}` | 채팅 세션 초기화 |
| POST | `/onboarding/response` | 온보딩 응답 저장 + 프로필 임베딩 동기화 |
| GET | `/onboarding/recommendations` | 온보딩 기반 추천 공고 |

---

## 동작 방식

### 1) 블로그 분석 요청 (`POST /blog/analyze` → `BlogAiService`)

**FULL_BLOG 모드** (블로그 전체 분석):
1. `analysis-cache:FULL_BLOG:{userId}:{blogId}:v1:default` 캐시 조회 — 적중 시 기존 결과 반환
2. `analysis-lock:FULL_BLOG:{userId}:{blogId}`를 `SET NX`(TTL 35분)로 획득 — 중복 요청 차단
3. 진단 쿼터 예약(reserve)
4. `CrawlerClient`로 `POST /crawl/blog-posts` **fire-and-forget** 트리거
   → 이후 파이프라인은 Crawler → Redis Stream → Analyzer가 비동기로 진행

**POST 모드** (단일 포스트 분석):
- `documentId`가 있으면 RabbitMQ `blog.analysis`에 직접 발행
- 없으면 크롤러 트리거로 수집부터 시작

### 2) 분석 완료 이벤트 수신 (`AnalysisCompletedListener`)

Analyzer가 발행한 `blog.analysis.completed`를 수신하여:
- `completed-event:processed:{correlationId}` `SET NX`로 **멱등 처리** (중복 이벤트 무시)
- `analysis-ctx:{correlationId}`로 userId/blogId 역산
- `blog_analysis_results` 저장, 캐시 갱신, 락 해제, 쿼터 확정
- 처리 실패 메시지는 `blog.analysis.completed.dlx` → `.dlq`로 격리

### 3) 진단·채팅 동기 프록시 (`PythonAiClient`)

`POST /blog/diagnosis`, `POST /blog/chat`은 인증·쿼터 문맥을 이 서버가 검증한 뒤
Analyzer로 동기 프록시한다. LLM 처리 시간을 고려해 **connect timeout 10초,
read timeout 120초**로 설정되어 있다 (게이트웨이의 `proxy_read_timeout 120s`와 정합).

### 4) 온보딩 추천

1. `POST /onboarding/response` → 응답 저장 → `OnboardingProfileTextBuilder`가
   선호 지역/카테고리/활동 유형을 텍스트화
2. `PythonAiClient.embedOnboardingProfile` → Analyzer `POST /v1/profile/embed`
3. `GET /onboarding/recommendations` → 최신 `profile_embeddings` row를 쿼리 벡터로
   pgvector 유사도 조회, 결과 없으면 active campaign fallback

### 5) 공고 데이터 유입

Crawler가 수집한 공고를 `POST /internal/campaigns/bulk`로 upsert 받는다.
공고 본문의 벡터화는 Crawler → Redis Stream → Analyzer 경로로 별도 진행되며,
추천 조회 시 이 서버가 pgvector 테이블을 직접 읽는다.

---

## 인프라 사용 (이 서버 소유 리소스)

**DB 테이블**: `users`, `user_blogs`, `regions`, `campaigns`, `user_campaigns`,
`user_campaign_likes`, `user_campaign_applies`, `diagnosis_quotas`, `blog_analysis_results` 등.
Analyzer 소유 테이블(`documents`, `document_chunks`, `analysis_jobs`, `blog_diagnoses`,
`profile_embeddings`)은 조회 위주로 접근한다. `influencer`는 이 서버가 primary로 조회한다.

**Redis 키** (`AnalysisCacheService`):

| 키 패턴 | 용도 |
|---|---|
| `analysis-cache:FULL_BLOG:{userId}:{blogId}:v1:default` | FULL_BLOG 결과 포인터 캐시 (값: analysisJobId) |
| `analysis-cache:POST:{userId}:{documentId}:v1:default` | POST 결과 포인터 캐시 |
| `analysis-lock:FULL_BLOG:{userId}:{blogId}` | 중복 분석 방지 락 (SET NX, TTL 35분) |
| `analysis-ctx:{correlationId}` | 완료 이벤트에서 userId/blogId 역산 (TTL 35분) |
| `completed-event:processed:{correlationId}` | 완료 이벤트 멱등 처리 (SET NX) |

**RabbitMQ** (`RabbitMQConfig`): `blog.analysis` 발행,
`blog.analysis.completed` 소비, 각각 DLX/DLQ (`.dlx`/`.dlq`) 구성.
