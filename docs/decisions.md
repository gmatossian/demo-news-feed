# Decision register

Recorded 2026-09-25. This separates Gabriel's agreements from the documentation
author's proposals. No application has been implemented yet.

Update 2026-09-25: the refinement answers below were given and the baseline was then
implemented in the working tree (uncommitted, awaiting Gabriel's review). Earlier
sections are kept as the historical record.

## Accepted

| Decision | Agreement |
| --- | --- |
| Purpose | Build a familiar working demo baseline for later learning exercises and ExecDesk dogfooding. Baseline construction is not itself the learning exercise. |
| Repository | News feed has its own repository, separate from booking and media library. |
| Baseline scope | Chronological feed, post details, creating/editing/deleting plain-text posts using fictional authors, seeded data, and reset. |
| Deferred features | Sign-in, comments, likes, media uploads, and ranking belong to possible later exercises. |
| Stack | Angular frontend and Java/Spring Boot backend. |
| Exercise independence | Exercise work need not be committed or incorporated into the baseline. |
| Working quality | Readable, maintainable code in focused files; proportionate verification rather than production-hardening ceremony. |
| Human control | Consequential decisions and final acceptance remain with Gabriel unless explicitly delegated. |

## Refinement answers — 2026-09-25

Accepted by Gabriel during the initial Claude engagement.

| Decision | Agreement |
| --- | --- |
| Persistence | File-backed H2 under `backend/data/`, gitignored. Data survives restarts. A fresh database is seeded; normal startup never overwrites existing data. |
| Reset | "Reset demo data" control in the demo banner, with a confirmation explaining that it removes current posts and restores the original fictional dataset. Document the equivalent command for agents/tools. Reset affects application data only — never source code or exercise changes. |
| Visual direction | Plain CSS; clean, responsive layout, readable typography, clear controls. Styles organized per component with shared spacing/colour values. Demo label visible but unobtrusive. Aim for a simple, finished-looking app rather than a wireframe. |
| Authors and attribution | Five fixed fictional authors in seed data; no author management. Author chosen when composing and fixed after publication; edits record no editor. Anyone using the local demo may edit/delete any post. The author selector is a fictional label, not authentication. |
| Editing and ordering | Publication time is set once and never changes. A saved edit records `updatedAt`, shown as "Edited <time>". Feed order: `publishedAt` descending, then `id` descending. Numeric IDs; seed posts keep the same IDs after reset. Seed data includes an equal-timestamp pair. Times shown as absolute local times. |
| Input contract | Title required, ≤120 characters, single line. Body required, ≤5,000 characters, internal formatting preserved. Author must exist. Validation failures return 400 with per-field messages (`ProblemDetail`). Content always rendered as plain text. |
| Screens and flows | Header with app name, "New post", and demo banner containing reset. Feed `/`, detail `/posts/:id`, compose `/posts/new`, edit `/posts/:id/edit` (shared form, author read-only when editing), "Post not found" and "Page not found" views. Cancel discards without prompting. Failed saves keep the draft. Delete confirmed via native `<dialog>`, then return to feed with a notice. Loading and error states everywhere. |
| Versions | Angular 22.2 / Node 24 LTS; Spring Boot 4.1.1 / Java 25 LTS; Maven wrapper. |
| Technical outline | `frontend/` Angular standalone components, signals, per-area data services, dev proxy for `/api`. `backend/` feature packages (`post/`, `author/`, `demo/`) with controller/service/Spring Data JPA repository; `schema.sql`, no migration tool; committed seed resource; H2 console off. REST API under `/api`. Two dev processes. |
| Planned checks | Backend integration tests (validation, ordering/tie-break, CRUD, missing post, reset, seeding without overwrite); a few focused frontend tests; manual browser walkthrough; short verification record in `docs/`. |

Clarifications Gabriel attached to the approval (same date):

- Fixed author and publication time are enforced by the backend, not only by
  disabled form controls.
- Whitespace trimming and character counting are defined identically in frontend
  and backend. Whitespace-only titles/bodies fail validation; internal body
  formatting is preserved.
- Schema initialization and seeding preserve existing data on ordinary startup.
  Reset is transactional, and creating posts after reset must not cause ID collisions.
- Exact framework versions and compatibility are verified during setup rather than
  assumed. Tooling is project-local; global installations are left unchanged.

## Current facts and delivery boundaries

- The repository is private and initially contained only its README.
- This delivery adds documentation only. It does not claim working features.
- Possible future public use does not authorize publication or visibility changes.
- No commit, push, merge, release, deployment, or external issue creation is
  authorized by this documentation handoff. Ask for project-specific authority.
- Update 2026-09-25: the application now exists in the working tree, uncommitted.
  See [verification.md](verification.md). Implementation complete, accepted by
  Gabriel, and committed remain separate states; only the first applies so far.

## Open choices for the initial Claude engagement

Status 2026-09-25: all of these topics were resolved by the refinement answers
above. The original table is kept for history.

These are recommendations to discuss, not accepted requirements. Bundle related
choices into a compact proposal instead of a long sequence of isolated questions.

| Topic | Starting recommendation | What needs deciding |
| --- | --- | --- |
| Local persistence | Keep posts across restarts using a simple local embedded database. | Durability expectation, storage technology, data location. |
| Fictional authors | Select an author while composing; anyone using the local demo can edit/delete any post. | Confirm the absence of ownership restrictions and clarify attribution on edits. This is not authentication. |
| Edit and ordering rules | Preserve publication time/feed position; record update time; use a stable ID to break timestamp ties. | Confirm observable semantics. |
| Input contract | Require nonblank title/body and a valid fictional author; choose modest explicit limits. | Fields, limits, whitespace handling, error responses. |
| Reset | An explicit local reset restores seed data; ordinary startup preserves existing work. | Command versus UI, exact replacement scope, confirmation needs. |
| UX | Feed, post detail, and compose/edit flows, with obvious demo labelling. | Compact screen/navigation proposal, deletion flow, and visual direction. |
| Technical outline | One Angular app and one Spring Boot service with a simple HTTP/JSON API. | Confirm the outline and persistence choice before scaffolding; ordinary names and compatible version selection can then be routine implementation details. |

The detailed outcomes in baseline-spec.md and acceptance.md are drafted to make
the assignment concrete. They do not imply that every UX/data detail has already
received separate approval. Further exclusions listed there are proposed scope
guardrails, not claims that Gabriel discussed each feature individually.

## Implementation details decided during the work — 2026-09-25

Claude chose these routine details within the accepted decisions while building.
Each one is small and reversible.

Update 2026-09-25: Gabriel reviewed this list and said he is comfortable with it.

| Detail | Choice and reason |
| --- | --- |
| ID scheme | Seed posts keep fixed IDs 1–8. The identity column for new posts starts at 1000, and reset re-inserts the seed IDs without restarting the identity. This meets "same IDs after reset", "transactional reset", and "no ID collisions" together. Restarting an identity is DDL in H2, which commits implicitly and so cannot be part of a transaction. |
| Enforcing the fixed author and publication time | The edit request type contains only `title` and `body`. The API rejects unknown JSON fields with 400, the entity has no setters for these fields, and the columns are excluded from SQL updates. |
| Unchanged edits | Saving an edit whose normalized text is unchanged returns 200 without setting `updatedAt`, so the post is not falsely shown as "Edited". |
| Whitespace and length rules | Trimming uses the Unicode `White_Space` property; lengths count code points; line endings become `\n`. Titles must be single-line. The same rules are implemented explicitly in `PostText.java` and `post-text-rules.ts`, with identical test cases. |
| Fresh-database detection | A database with no authors is fresh and gets seeded. Authors cannot be deleted through the app, so emptying the feed never triggers reseeding. |
| Feed excerpt | Produced by the backend: whitespace collapsed, about 200 code points, cut at a word boundary, with `…`. |
| Error format | RFC 9457 problem details, plus an `errors` object mapping each field to a message on 400. |
| Data directory override | The `NEWSFEED_DATA_DIR` environment variable, defaulting to `./data` relative to `backend/`. |
| Seed content and naming | The app is called "Demo News Feed". Seed posts describe the fictional town of Larchmere, with five invented author names. |
| Frontend state | Angular `rxResource` (stable in v22) for loading, Reactive Forms for the form, the native `<dialog>` for confirmations, and a small notice service for "Post deleted." and similar messages. |
| Removed scaffolding | The Angular CLI's `.vscode/` folder and sample `app.spec.ts`, the Spring Initializr `HELP.md` and default H2 console dependency, and Maven's empty POM metadata blocks. |

## Review follow-up — 2026-09-25

Gabriel judged the baseline sufficient and asked for these follow-up adjustments, now implemented:

| Decision | Agreement |
| --- | --- |
| Uncertain save failures | When a save gets no definite answer from the backend (unreachable, proxy failure, 5xx, or unrecognized error), the form says "Couldn’t confirm whether your changes were saved—check the feed before retrying." and keeps the draft. Definite rejections (4xx problem details such as validation errors or a deleted post) still say "Not saved." with the backend's reason and per-field messages. |
| Default ports | At Gabriel's request, the frontend dev server now defaults to 4301 and the backend to 8081, so the app no longer clashes with other local projects on 4200/8080. The proxy, the "cannot reach the backend" message, and all docs and command examples were updated. |

At that review checkpoint, final acceptance, commit, and push remained separate
and had not been authorized. The later delivery authorization is recorded below.

## Maintaining this record

Record material answers with their date and whether Gabriel accepted them or
explicitly delegated the choice. Update the affected specification/checklist at
the same time. Do not turn suggestions into decisions just because implementation
has begun. Latest explicit user instructions govern; surface any conflict.

## Baseline delivery authorization — 2026-09-25

Gabriel authorized committing and pushing the reviewed baseline, with a merge only
if the work was on a separate branch. The work was already on main. This authority
is specific to the initial baseline delivery; it does not grant standing permission
to merge exercise solutions, publish/deploy, or change repository visibility.
Hands-on acceptance remains separate from this delivery checkpoint.

## Public sharing — 2026-09-25

Gabriel authorized making this repository public after checking for secrets,
sensitive data, and machine-specific paths, superseding the earlier private-only
publication boundary. Preparation removes local checkout paths and incidental
process identifiers from documentation where present. Git history must be checked
as well as the current files before changing visibility. Public source availability
does not authorize deployment or promotion of exercise solutions into main.
