# Verification record — baseline implementation

Recorded 2026-09-25 by Claude during implementation, on macOS (Apple Silicon). This
is evidence for Gabriel's review, not acceptance. The acceptance checklist in
[acceptance.md](acceptance.md) is intentionally left unchecked for Gabriel.

## Environment

| Tool | Version | Source |
| --- | --- | --- |
| Java | OpenJDK 25.0.4.1 (Homebrew) | Already installed; `java` on PATH |
| Maven | 3.9.16 | Project-local wrapper `backend/mvnw` |
| Spring Boot | 4.1.1 (Spring Framework 7.0.9, Hibernate 7.4.5, Jackson 3.1.5, H2 2.4.240) | Resolved from `pom.xml` |
| Node.js / npm | 24.20.0 / 11.19.0 | Already installed (nvm) |
| Angular | 22.2.0 (TypeScript 6.0.3, RxJS 7.8.2, Vitest 5.0.2) | Project-local `frontend/node_modules` |

Compatibility was checked against the official documentation on 2026-09-25:

- Spring Boot 4.1.1 system requirements: "requires at least Java 17 and is compatible
  with versions up to and including Java 26"; Maven 3.6.3 or later.
- Angular version reference: Angular 22 supports Node `^22.22.3 || ^24.15.0 || ^26.0.0`
  and TypeScript `>=6.0.0 <6.1.0`.

Nothing was installed globally. The Angular CLI ran through `npx @angular/cli@22.2.0`
(with `--commit=false`); the backend skeleton came from start.spring.io.

## Automated checks

| Command | Result |
| --- | --- |
| `cd backend && ./mvnw clean test` | 21 tests passed: `PostTextTest` 6, `PostApiTest` 10, `DemoDataTest` 5 |
| `cd frontend && npm test` | 13 tests passed across 3 files (10 originally; 3 added with the uncertain-save follow-up) |
| `cd frontend && npx ng build` | Production build succeeded with no warnings |
| `cd frontend && npm ci` | Succeeded. npm 11 warned that 5 packages' install scripts are not in `allowScripts`; build and tests pass without them |

What the automated tests cover:

- **Text rules:** trimming, CRLF normalization, and whitespace-only rejection;
  code-point counting at the 120 / 5,000 limits; single-line titles. The same cases
  run on both frontend and backend.
- **API:**
  - feed order, including the equal-timestamp tie-break;
  - creating a post, with normalized storage and IDs of 1000 or more;
  - field errors, with nothing stored when validation fails;
  - editing keeps the author and publication time and doesn't move the post;
  - `authorId` or `publishedAt` sent with an edit is rejected;
  - saving unchanged text doesn't set `updatedAt`;
  - deleting, then 404 on the old link;
  - 404 for get, update and delete of a missing post.
- **Seed and reset:**
  - startup seeding doesn't overwrite changed or deleted data;
  - an emptied feed stays empty across startups;
  - reset restores the exact dataset with stable IDs;
  - posts created after a reset get new, non-colliding IDs;
  - a reset that fails part-way rolls back and keeps the previous data.
- **Frontend form:** whitespace-only input is rejected without being emitted; emitted
  values are normalized; the author is read-only when editing; server field messages
  appear next to their field; a failed save keeps the draft and shows the error.

Check on the rollback test: with `@Transactional` temporarily removed from
`DemoDataService.reset()`, `failedResetRollsBackAndKeepsTheCurrentData` failed. With it
restored, the test passed.

## Manual walkthrough

Performed in the Claude desktop app's browser pane (Chromium). The backend used a
disposable scratch data directory (`NEWSFEED_DATA_DIR`). The frontend ran on port 4300
because port 4200 was occupied by an unrelated local project.

| Check | Observed |
| --- | --- |
| Fresh start | Log said "Fresh database: loaded the demo dataset."; the feed showed 8 posts, newest first, with post 6 above post 5 (equal times). |
| Detail view | Full body shown with line breaks and indentation; literal `<b>bold</b>` shown as text; the browser tab title shows the post title. |
| Compose validation | A whitespace-only title and empty fields showed "Choose an author.", "Enter a title.", "Enter the post text."; the counter showed 0/120; focus moved to the first invalid field. |
| Failed save | With the backend stopped, Publish kept the title, text and author and showed an error. (The wording at the time was "Not saved. Cannot reach the backend…"; it was superseded by the follow-up below.) |
| Publish | After restarting the backend, Publish created post 1000, opened it with the notice "Post published.", and kept the emoji and indentation. |
| Restart persistence | The post created before a backend restart was still there afterwards (same data directory). |
| Direct link / refresh | Reloading `/posts/1000` and `/posts/1000/edit` worked. |
| Edit | The author was shown read-only; an emoji counted as 1 character (18/120); Cancel discarded the change; Save showed "Changes saved." and "Edited …"; the post stayed first in the feed with its original publication time. |
| Delete (keyboard only) | Enter opened the dialog with focus on Cancel. Escape closed it, returned focus to Delete, and kept the post. Enter, Tab, Enter deleted it, returned to the feed with "Post deleted.", and the old link showed "Post not found". |
| Invalid URLs | `/posts/abc/edit` showed "Post not found"; `/nowhere` showed "Page not found". |
| Empty feed | After deleting every post through the API, the feed showed "No posts yet." with links to write a post or reset. |
| Reset | The banner dialog explained the scope. Confirming restored 8 posts, with the notice "Demo data restored…". This worked both on the feed page and from a detail page (which returns to the feed). The notice cleared on the next navigation. |
| Backend down | The feed and detail pages showed "…could not be loaded. Cannot reach the backend…" with Try again, which recovered after the backend restarted. |
| Narrow viewport | At 375×812 the feed and detail pages had no horizontal scrolling, and the header and banner wrapped. |
| Console | Only the expected failed-request entries (502 while the backend was down, 404 for the deleted post); no application errors. |

## Documented commands verified

- `cd backend && ./mvnw spring-boot:run`: started on port 8080, created
  `backend/data/newsfeed.mv.db`, and seeded it. `git check-ignore` confirmed
  `backend/data/` is ignored. The directory was then deleted, so the first real run
  starts fresh.
- `curl -X POST http://localhost:8080/api/demo/reset` returned `{"authors":5,"posts":8}`.
- `cd frontend && npm start`: with port 4200 already in use it exits with "Port 4200
  is already in use. Use '--port' to specify a different port." `npm start -- --port 4300`
  served the app and forwarded `/api` to the backend.
- The app on port 4200 itself was not exercised in this environment, because another
  project was using that port.

## Follow-up: uncertain save failures (2026-09-25)

Change: `isUncertainOutcome` in `frontend/src/app/core/api-error.ts` classifies a save
failure as uncertain when the status is 0, unknown, or 5xx. The post form then shows
"Couldn’t confirm whether your changes were saved—check the feed before retrying."
Definite 4xx rejections keep "Not saved." plus the backend's reason.

| Check | Result |
| --- | --- |
| `npm test` | 13 passed. New page tests: a 502 (unreachable) and a 500 problem detail show the uncertain message and keep the draft; a 400 validation error shows "Not saved. Some fields need attention." with the field message and no uncertain wording. |
| Test sensitivity | With `isUncertainOutcome` temporarily forced to `false`, both uncertain tests failed; restored afterwards. |
| `npx ng build` | Succeeded with no warnings. |
| Browser: definite | Opened `/posts/4/edit`, deleted post 4 through the API, then saved. The alert read "Not saved. Post 4 does not exist. It may have been deleted. Your text is still here." The edited title was kept. |
| Browser: uncertain | Opened `/posts/2/edit`, stopped the backend, then saved. The alert read "Couldn’t confirm whether your changes were saved—check the feed before retrying. Your text is still here." The edited title and body were kept and Save was enabled again. |

Environment note: during this check, an unrelated local project (`demo-media-library`)
started a backend on port 8080. For the uncertain browser check, this app's backend ran
on port 8090 with a scratch proxy file (`SERVER_PORT=8090`, `--proxy-config`), so no
repository files changed. Before switching, a save sent through the default proxy
reached the other app and showed "Not saved. No static resource api/posts/2."

## Follow-up: default ports 4301 / 8081 (2026-09-25)

Change: `server.port=8081` in `application.properties`; `port: 4301` in the
`angular.json` serve options; `proxy.conf.json` targets `http://localhost:8081`; the
unreachable-backend message, both READMEs, and `docs/api.md` were updated. The port
references in earlier sections of this file are historical.

| Check | Result |
| --- | --- |
| Port availability | Checked listeners without stopping anything. 4301 and 8081 were free. 4200 was held by an unrelated development server; 8080 was free by then, the other project having stopped on its own. |
| `./mvnw clean test` / `npm test` / `npx ng build` | 21 and 13 tests passed; the build succeeded with no warnings. |
| README commands, unmodified | `cd backend && ./mvnw spring-boot:run` logged "Tomcat started on port 8081" and seeded a fresh `backend/data`. `cd frontend && npm ci && npm start` served on http://localhost:4301/. |
| Frontend reaches its own backend | A `POST /api/posts` sent through `localhost:4301` created post 1000, which `GET localhost:8081/api/posts/1000` returned directly. The feed through the proxy listed it first. `curl -X POST http://localhost:8081/api/demo/reset` returned `{"authors":5,"posts":8}`, and the proxy then showed IDs 8…1. In the browser, `http://localhost:4301/posts/6` rendered the post from `GET localhost:4301/api/posts/6 → 200`. |
| Cleanup | Only the two processes started for this check were stopped (by PID, after confirming their working directories). The unrelated process on 4200 was still running afterwards. The verification-created `backend/data/` was deleted. |

## Not checked

- Browsers other than the Chromium-based browser pane; Windows or Linux.
- Screen-reader output. Only labels, focus order and dialog focus were checked.
- Concurrent use from several browser tabs (no concurrency handling is in scope).
- Large datasets or performance.
