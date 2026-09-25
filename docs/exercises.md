# Optional exercises after the baseline

This is an idea menu, not assigned work or a requirement to build these features.
Keep the initial application small so each exercise starts with a useful gap.
Do not implement solutions or create GitHub issues from this list automatically.

| Scenario | Example bounded requirement | Useful practice |
| --- | --- | --- |
| Finding a post | Add case-insensitive title search with clear empty results. | Angular forms/state, Java collection filtering, input rules. |
| Organizing posts | Add tags and filter the feed by a selected tag. | Sets/maps, data modelling, accessible UI controls. |
| Continuing unfinished writing | Save a draft and resume it later without publishing it. | Angular state and persistence, distinguishing draft from published data. |
| Reader discussion | Add flat comments to a post, with defined ordering and deletion behaviour. | Relationships, API contracts, validation, UI composition. |
| Two editors | Detect competing updates instead of silently overwriting another edit. | Concurrency, optimistic locking, conflict UX. |
| Scheduled news | Publish a saved post at its scheduled time, including after a restart. | Java scheduling/concurrency, time semantics, idempotency. |
| A larger feed | Design pagination, then optionally implement one agreed approach. | API/system design, query behaviour, stable ordering. |
| Different feed strategies | Compare chronological and ranked feeds without building the ranking system. | Requirements, collection ordering, system-design tradeoffs. |

These examples are intentionally separate; none requires implementing the entire
menu. A concurrency exercise should include a reproducible competing-action case,
not just adding threads to code that does not benefit from them. A system-design
exercise can finish with a design and tradeoff discussion without code changes.

## Turn one idea into a task

Record only what the exercise needs:

- Starting baseline revision and current capabilities it assumes.
- User scenario, observable outcome, and explicit exclusions.
- Decisions already made and choices the learner must explore.
- Who does the work: learner, agent, or collaboration. Do not assume an agent
  should implement a learning exercise on the learner's behalf.
- Expected evidence: demonstration, focused checks, analysis, or design artifact.
- Stop conditions and human checkpoints, including consequential choices.
- End state: inspect the result, keep it separately, or discard it deliberately.

For ExecDesk, turn that brief into a fictional issue and select its workflow and
working modes. This repository does not need a direct ExecDesk integration.

Use a separate local copy or exercise branch when useful. Neither commits nor
merges are required to complete an exercise. Never reset/discard unrelated work
automatically, and never promote an exercise solution into the baseline without
an explicit decision. Record a baseline revision after it has been accepted and
committed through an authorized delivery step.
