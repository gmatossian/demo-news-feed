# News feed — purpose and working agreements

## Agreed purpose

Build a small, understandable news-feed application as a reusable starting point
for learning exercises and ExecDesk dogfooding. Its overall purpose should be
intuitive: people publish posts and people read them. Its supported capabilities
and actual behaviour must still be explicit.

The application is built with basic features first. Learning then happens through
new requirements, investigations, bug fixes, or design questions against that
working baseline. Building the initial baseline is not itself the learning plan.

The same starting point supports two distinct activities:

- Practice: the learner investigates, designs, implements, or compares approaches.
- ExecDesk dogfooding: a believable fictional issue is worked through ExecDesk to
  evaluate delegation, context, human checkpoints, evidence, and results.

An exercise need not produce a commit or any lasting baseline change. Its work can
be reviewed and discarded. Do not gradually incorporate exercise solutions into
the baseline just because they were completed successfully.

## Repository and audience

This repository contains this system only, with its Angular frontend and Java/Spring
Boot backend. Separate repositories exist for the booking and media-library
systems; do not modify them for this assignment.

The repository is currently private. Potential future public visibility is an
option, not an instruction to publish. Documentation and the eventual application
must clearly identify their demo/learning purpose. Use fictional authors and
original synthetic posts, without copying real user information or news articles.

The intended users are Gabriel, other learners, and people evaluating development
agents. The baseline should be understandable without reading a long product
brief, while the capability record provides precise expectations for exercises.

## Quality expectations

- Deliver a working application rather than static screens or a simulated backend.
- Keep the code readable and maintainable. A demo is no excuse for a giant file.
- Use focused components and services with clear responsibility boundaries.
- Prefer a straightforward local setup and a reproducible starting dataset.
- Make meaningful behaviour, limitations, and failure states visible.
- Use proportionate verification. No blanket coverage target, exhaustive matrix,
  or production-hardening programme is requested.
- Do not implement concurrency frameworks, distributed infrastructure, or advanced
  features solely because they might be useful for a future exercise.

## Authority and status

These purpose/working agreements come from the discussion. The accepted baseline
and stack, plus remaining proposals, are tracked in [decisions.md](decisions.md).
Suggestions are not accepted requirements until Gabriel accepts them. Later user
instructions take precedence over these documents; record meaningful changes
rather than silently rewriting the historical decision status.
