# Car Matchmaker

A full-stack web app that helps car buyers in India cut through choice paralysis — quiz-based recommendations, filtering, comparison, and shortlisting for 25 real-world models.

---

## Run It

```bash
docker-compose up --build -d
```

App available at **http://localhost:8080** once healthy (~30s). Requires Docker Desktop.

```bash
docker-compose down      # stop
docker-compose down -v   # stop + wipe data
docker-compose logs -f   # watch logs
```

**Without Docker** (local dev with H2 file DB):
```bash
./mvnw spring-boot:run
```

---

## What I Built and Why

**Problem**: The Indian car market has 25+ models across overlapping segments. Buyers spend weeks on forums and YouTube without a structured way to narrow down options.

**Solution**: A recommendation engine that scores cars on 8 weighted factors (budget fit, use-case match, mileage, safety, ratings, body type, fuel/transmission, must-haves) and produces a transparent 0-100 score with explanations — no black-box AI.

**Core features**:
- Quiz → Top 5 ranked results with "why it matches" and "tradeoffs"
- Browse/filter all 25 cars by specs
- Shortlist (persisted, HTMX-powered add/remove)
- Side-by-side comparison with winner badges

---

## What I Deliberately Cut

| Cut | Reason |
|-----|--------|
| Auth / user accounts | Adds sessions, security config, migration complexity — overkill for a demo |
| External APIs (pricing, reviews) | Cost, latency, rate limits; seeded data is sufficient to prove the UX |
| AI-powered scoring | Unpredictable, expensive, hard to explain — deterministic algo is testable and transparent |
| Mobile app | Responsive web covers it; no time for native |
| Payment / booking | Out of scope — this is discovery, not transaction |
| Caching (Redis) | Premature at 25 cars; everything responds in <200ms |

---

## Tech Stack and Why

| Layer | Choice | Why |
|-------|--------|-----|
| Backend | Java 17, Spring Boot 3.2 | LTS, convention-over-config, great JPA/validation support |
| Frontend | Thymeleaf + HTMX + Tailwind CDN | Server-rendered with dynamic updates — no JS build step |
| Database | H2 (dev) / PostgreSQL 16 (Docker) | Zero-config locally, production-grade in containers |
| Build | Maven (wrapper included) | No install required, reproducible builds |
| Containerization | Docker + docker-compose | Single-command setup, health checks, volume persistence |

**Key tradeoff**: Thymeleaf+HTMX over React/Vue. Faster to build, no separate frontend build pipeline, and the interactivity (shortlist toggle, dynamic filters) doesn't justify a SPA.

---

## What I Delegated to AI vs. Did Manually

### AI did (GitHub Copilot / agent):
- **Data seeding** — Generated 25 cars with realistic Indian market specs, pricing, reviews (tedious lookup work)
- **Boilerplate** — Entity classes, repository interfaces, controller scaffolding, DTOs
- **Unit tests** — Generated comprehensive test suites for services and controllers (89 tests)
- **Docker setup** — Dockerfile, docker-compose.yml, application-docker.yml profile, .dockerignore
- **Thymeleaf templates** — HTML structure, Tailwind classes, HTMX attributes
- **Bug fixes in tests** — When integration tests hit template parsing issues, AI diagnosed and fixed them

### I did manually:
- **Architecture decisions** — Layered design, scoring algorithm weights, which features to include/cut
- **Scoring algorithm logic** — The 8-factor weighted system, match reasons, tradeoff detection
- **UX flow** — Quiz → results → browse → shortlist → compare pipeline
- **Data model design** — Entity relationships, what fields each car needs
- **Code review** — Validated AI output for correctness, removed over-engineered suggestions

### Where AI helped most:
- **Data entry** — Seeding 25 cars with 3-4 variants each would've taken hours manually
- **Test generation** — Writing 89 tests by hand is grunt work; AI nailed the happy paths and edge cases
- **Docker config** — Multi-stage builds, health checks, depends_on — easy to get wrong, AI got it right (after one ARM64 fix)

### Where AI got in the way:
- **Over-engineering** — Suggested adding Spring Security, caching layers, and event-driven patterns when a simple MVC was all that was needed
- **Template issues** — Generated Thymeleaf fragments that referenced variables not in scope, causing runtime errors in integration tests
- **Docker on Apple Silicon** — Initially used `alpine` base images that don't support ARM64; had to override to standard images
- **Verbose output** — Generated overly detailed README sections and test classes with redundant assertions; needed pruning
- **False confidence** — Sometimes generated plausible-looking code that compiled but failed at runtime (especially around Thymeleaf fragment selectors)

---

## If I Had Another 4 Hours

1. **User accounts + saved sessions** — Spring Security with simple form login, so each user gets their own shortlist and quiz history
2. **EMI / TCO calculator** — Total cost of ownership on the compare page (insurance, fuel cost per year, service intervals)
3. **Elasticsearch-powered search** — Fuzzy matching, "cars like Creta but cheaper", natural-language filters
4. **CI/CD pipeline** — GitHub Actions: test → build → push Docker image → deploy to a free-tier cloud (Render/Railway)

---

## Project Structure

```
src/main/java/com/example/carmatchmaker/
├── config/DataSeeder.java            # Seeds 25 cars on startup
├── controller/                       # 5 controllers (Home, Quiz, Car, Shortlist, Compare)
├── service/                          # CarService, RecommendationService, ShortlistService
├── repository/                       # Spring Data JPA interfaces
├── model/                            # JPA entities (Car, Variant, Review, ShortlistItem, BuyerPreference)
├── dto/                              # BuyerPreferenceForm, RecommendationResult, CarFilterRequest
└── enums/                            # BodyType, FuelType, Transmission, UseCase, Priority, MustHave

src/main/resources/
├── application.yml                   # H2 config (dev)
├── application-docker.yml            # PostgreSQL config (Docker)
└── templates/                        # Thymeleaf views (7 pages + fragments)

Docker:
├── Dockerfile                        # Multi-stage build (JDK 17 → JRE 17)
├── docker-compose.yml                # App + PostgreSQL 16
└── .dockerignore
```

---

## Tests

```bash
./mvnw test    # 89 tests — all pass
```

Covers: scoring algorithm, service layer (CarService, ShortlistService, RecommendationService), controller endpoints, DTO validation, and application context loading.
