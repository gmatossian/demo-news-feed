# demo-news-feed

**Demo and learning purposes only. Not intended for production use.**

A familiar news-feed application to use as a starting point for independent
practice exercises and ExecDesk dogfooding. Build the basic system first; later
exercise work does not have to become a permanent change to this repository.

## Current status

The initial baseline is implemented for learning exercises and ExecDesk dogfooding.
Gabriel authorized committing and pushing it on 2026-09-25. Hands-on acceptance
remains separate; this is not a production release.

What it does:

- **Feed:** a chronological feed (newest first) showing each post's title, fictional
  author, date, "edited" marker, and excerpt.
- **Post pages:** each post has its own page with the full plain text. Direct links
  and refreshes work, and missing posts show "Post not found".
- **Writing:** publish plain-text posts under a fictional author, and edit the title
  and text afterwards. The author and publication time are fixed by the backend.
- **Deleting:** delete a post after confirming in a dialog.
- **Demo data:** eight original fictional posts load into a fresh database. A
  confirmed "Reset demo data" action restores them.
- **Saved data:** posts are stored in a local H2 database file and survive restarts.
- **Visible states:** loading, empty, validation, save-failure and backend-down states
  are all shown.

There is no sign-in, comments, likes, media, search, tags, or ranking. The author
selector is a fictional label, **not authentication**. This is a demo intended for public sharing, not a hosted or production-ready
service.

## Prerequisites

| Tool | Version used | Notes |
| --- | --- | --- |
| Java JDK | 25 (Spring Boot 4.1.1 supports 17–26; the build targets 25) | `java -version` |
| Node.js | 24 LTS (≥ 24.15; see `frontend/.nvmrc`) | `node --version` |
| npm | 11 (ships with Node 24) | |

Maven and the Angular CLI are **not** needed globally. The backend uses the Maven
wrapper (`backend/mvnw`), and the frontend uses its locally installed Angular CLI.

## Run it

Use two terminals from the repository root.

1. Backend, on http://localhost:8081:

   ```bash
   cd backend && ./mvnw spring-boot:run
   ```

   The first start creates and seeds `backend/data/newsfeed.mv.db`. Later starts keep
   whatever data is there.

2. Frontend, on http://localhost:4301. `npm ci` is only needed the first time.

   ```bash
   cd frontend && npm ci && npm start
   ```

   If port 4301 is taken, use `npm start -- --port 4302` (or another free port) and
   open that port instead. The dev server forwards `/api` requests to the backend on
   port 8081.

Open http://localhost:4301.

These ports avoid the framework defaults (4200 and 8080), which other local projects
may already be using. The backend port is `server.port` in
`backend/src/main/resources/application.properties`, and the proxy target is in
`frontend/proxy.conf.json`. Change them together.

**Stop:** press `Ctrl+C` in each terminal.

**Tests:** `cd backend && ./mvnw test` and `cd frontend && npm test`.

## Reset demo data

Choose **Reset demo data** in the banner at the top of the app, then confirm. This
removes all current posts and restores the original fictional posts and authors. It
changes only the app's database, never source code or exercise work.

Command for agents and tools (the backend must be running):

```bash
curl -X POST http://localhost:8081/api/demo/reset
```

For a completely fresh database, stop the backend, delete `backend/data/`, and start
it again. That directory only ever holds this app's local data, and git ignores it.

## Data location

- **Runtime database:** `backend/data/newsfeed.mv.db`, relative to where the backend is
  started, and gitignored. Set `NEWSFEED_DATA_DIR` to put it somewhere else.
- **Committed seed data:** `backend/src/main/resources/seed/demo-data.json`.
- **Tests:** automated tests use a separate in-memory database.

## Known limitations

- Runs locally in development mode only. The Angular dev server and Spring Boot run
  as two separate processes, and there is no packaged or production build set-up.
- There are no accounts: anyone using the local app can edit, delete, or reset
  anything.
- If two tabs edit the same post, the last save wins. No conflict is detected.
- There is no pagination. The feed returns every post.
- Times display in the browser's time zone. Seed times are fixed UTC instants.
- Verified on macOS with a Chromium-based browser. Other platforms and browsers were
  not tested. See [docs/verification.md](docs/verification.md).

## Documents

| Read | Purpose |
| --- | --- |
| [CLAUDE.md](CLAUDE.md) | Working instructions, authority, and delivery boundaries. |
| [Product brief](docs/product-brief.md) | Why the system exists and how the baseline differs from later exercises. |
| [Decision register](docs/decisions.md) | Accepted choices, dated refinement answers, and implementation details. |
| [Baseline specification](docs/baseline-spec.md) | Capabilities and scope boundaries. |
| [Data and API contract](docs/api.md) | Fields, text rules, ordering, endpoints, errors, seed/reset, and storage. |
| [Verification record](docs/verification.md) | Versions, checks actually run, the manual walkthrough, and what was not checked. |
| [Implementation brief](docs/implementation-brief.md) | The original delegation brief. |
| [Acceptance and verification](docs/acceptance.md) | Gabriel's acceptance checklist (not yet checked). |
| [Optional exercises](docs/exercises.md) | Future task ideas. They are not part of the baseline. |
| [frontend/README.md](frontend/README.md) | Frontend commands and code layout. |

Backend code is organized by feature under `backend/src/main/java/demo/newsfeed/`:

- `post/`: the post rules and API.
- `author/`: the fixed fictional authors.
- `demodata/`: seeding and reset.
- `web/`: error responses.

## Handing this to Claude

Start Claude Code in this repository so it can read CLAUDE.md and the linked
documents. If using a chat client without local file access, supply the documents
themselves; local paths alone do not provide access.

The documents provide context and boundaries. Your delegation message determines
what work Claude is authorized to perform. Scope approval is not Git-delivery or
publication permission.
