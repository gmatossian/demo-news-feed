# Baseline capabilities

Status: Gabriel accepted the small baseline: chronological feed, post details,
plain-text post creation/editing/deletion, fictional authors, seeded data, and reset.
The observable details below are a draft elaboration for refinement, not separately
approved UX/data decisions. See [decisions.md](decisions.md).
This defines the initial application. It does not assign the future exercises.

Update 2026-09-25: Gabriel resolved the open behaviour questions (see the
[decision register](decisions.md)). B1–B8 are implemented in the working tree,
awaiting his review. The exact contract is in [api.md](api.md) and the evidence is in
[verification.md](verification.md). "Implemented" here means built and checked by
Claude, not accepted.

## Baseline and proposed observable details

| ID | Capability | Observable outcome |
| --- | --- | --- |
| B1 | Read the feed | A chronological list presents each post's title, author, publication time, and enough text to choose what to read. Newest published posts appear first. |
| B2 | Read a post | A dedicated view displays the full plain-text post and its metadata. A direct link works after a browser refresh; a missing post has an understandable result. |
| B3 | Publish a post | A person enters a title and body and selects a fictional author. A successful save creates one post and makes it available in the feed and detail view. |
| B4 | Edit a post | Existing content can be changed and saved; the updated content is visible consistently. Cancelling an edit does not save it. |
| B5 | Delete a post | Deletion requires an explicit confirmation. Confirmed deletion removes the post from the feed and makes its old link resolve as missing. Cancelling preserves it. |
| B6 | Seed and reset demo data | A new local installation can load a documented fictional dataset. An explicit reset restores the known starting dataset without changing source files or unrelated data. |
| B7 | Understand app state | Loading, empty, missing-post, validation, and save/load failure states are visible. A failed save does not pretend to succeed or erase the person's draft. |
| B8 | Recognize its purpose | The app and README clearly identify it as a demo/learning system. All seed content and identities are fictional. |

B1–B8 describe targets, not claims about implemented features. In particular,
confirmation flows, routing, display fields, and failure behaviour are proposed
details to incorporate into the compact design review.

## Behaviour to settle before implementation

Keep the answers short and record them in the decision register and this document:

- Persistence: should posts survive backend restarts, and where does local data live?
- Demo authors: are they just labels selected while composing, and can anyone in
  the local demo edit/delete any post? There is no authentication proposal.
- Editing: recommend preserving publication time and feed position while recording
  an updated time. This is a proposed behaviour, not yet an accepted decision.
- Validation: title/body required, agreed practical length limits, whitespace
  handling, and author selection. Client and server must agree on accepted input.
- Equal publication times: specify a deterministic tie-break order.
- Reset: choose an explicit local command or UI action, define what it replaces,
  and keep ordinary startup from overwriting existing work.
- UX: agree the main screens, navigation, editing/deletion flow, and visual direction
  in a compact sketch or description before consequential design choices are built.

## Outside the baseline

Authentication, accounts/roles, comments, reactions/likes, follows, personalized
ranking, tags/search, saved posts, persistent drafts, scheduled publication,
notifications, media uploads, rich text/Markdown rendering, moderation, real-time
updates, analytics, and large-feed pagination are reserved for possible exercises.

No production deployment, payment integration, live news ingestion, scraping,
external identity provider, or paid runtime service is part of this assignment.
Basic keyboard access, labelled inputs, readable errors, and plain-text rendering
belong to an understandable working baseline; they are not advanced exercises.

## Baseline discipline

Build the agreed capabilities completely, with a real path from UI to backend and
the agreed persistence mechanism. Keep an honest capability list: planned,
implemented, and verified are different states.

After human acceptance, a baseline commit/tag may be recorded only with explicit
Git-delivery authorization. Later exercise solutions do not update this baseline
unless Gabriel separately chooses to promote a change.
