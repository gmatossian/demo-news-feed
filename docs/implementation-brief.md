# Delegation brief — build the initial baseline

## Desired result

An understandable local news-feed application meeting the accepted baseline,
with readable code, synthetic data, repeatable setup/reset, and enough verification
to support subsequent exercises. No application code exists at this handoff.

## First engagement

Read the pack and inspect the actual checkout. Confirm what is already approved
in [decisions.md](decisions.md). For open material choices, present a short proposed
solution with tradeoffs and ask Gabriel; do not silently decide them. A compact
screen sketch and technical outline are sufficient. No elaborate roadmap, formal
architecture programme, or exhaustive design package is requested.

Once the required choices are accepted and implementation is authorized, carry the
work through implementation, verification, and review preparation. Do not repeatedly
ask to continue between routine edits or successful checks. New consequential
choices still require a decision. This document does not authorize commits/pushes.

## Accepted stack and proposed technical outline

Gabriel accepted an Angular frontend and Java/Spring Boot backend, providing both
Angular and Java practice opportunities. Do not reopen that choice without a
concrete blocker. A simple HTTP/JSON boundary is recommended; API route naming
and ordinary component/class organization may be handled as implementation details
within the accepted design.

Suggested layout, not an architecture mandate:

    frontend/        Angular application and its local build configuration
    backend/         Java application and its build wrapper
    docs/            Actual capability, design, setup, and verification records
    exercises/       Optional exercise briefs, when separately requested

These application directories do not exist yet. Do not create a separate repo for
frontend/backend or add a shared platform across the three demo systems.

Within the accepted stack:

- Choose compatible stable Angular, Node, Java, and Spring Boot versions using
  current official documentation. Record the versions and prerequisites; do not
  treat versions from the separate ExecDesk project as automatically required.
- Use feature-focused Angular components and explicit API access. Keep substantial
  templates/styles separate from orchestration logic; avoid a giant root component.
- Keep Java HTTP handling, application rules, and persistence responsibilities
  understandable. Avoid both a giant controller and unnecessary abstraction layers.
- Select persistence with Gabriel before establishing the data contract. Prefer
  low setup overhead; evaluate an embedded local database if persistence across
  restarts is wanted. No storage technology has been accepted yet.
- Document post fields, validation, timestamps, ordering, API responses, missing
  records, and error behaviour once agreed. Keep UI and backend contracts aligned.
- Document data location and reset scope. Keep generated/runtime data and local
  configuration separate from committed seed data; do not include credentials.
- Use synthetic content and plain-text display. External services and framework
  features should only be added when they serve the accepted baseline.

Leave multithreaded processing, caches, distributed services, queues, and deployment
infrastructure for exercises unless a concrete baseline requirement needs them.

## Verification and handoff

Follow [acceptance.md](acceptance.md). Maintain a short verification record with
commands, observed outcomes, and limits. Distinguish manual checks from automated
checks and intended behaviour from observed behaviour.

Before handing back, update README with exact verified setup/start/stop/reset
commands and prerequisites, plus links to the final capability and contract docs.
Do not publish plausible-looking commands before they exist and have been checked.
Summarize implemented behaviour, material decisions made during work, checks,
known limitations, and exactly what remains for Gabriel to accept.

## Reference guidance

These are technical references, not authority to expand scope:

- [Angular style guide](https://angular.dev/style-guide) — feature organization and focused files.
- [Spring REST guide](https://spring.io/guides/gs/rest-service/) — introductory Java HTTP service guidance.
- [Claude Code project instructions](https://code.claude.com/docs/en/memory) — use of CLAUDE.md; other Claude clients may require supplying the documents manually.
