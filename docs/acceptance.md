# Review and proportionate verification

This is a proposed acceptance checklist for the baseline after its scope and open
behaviour choices are approved. Nothing is checked off at the documentation stage.
This is not a production certification or a demand for one automated test per row.

## User-visible outcomes

- [ ] Feed, detail view, publishing, editing, and confirmed deletion work together.
- [ ] Agreed ordering and timestamp behaviour survive edits and equal timestamps.
- [ ] Invalid/blank inputs are handled consistently by UI and backend.
- [ ] Loading, empty, missing-post, and backend-error states are understandable.
- [ ] A failed save preserves entered content; cancelling edits/deletion preserves saved data.
- [ ] Plain-text post content remains text rather than executable markup.
- [ ] Primary flows work with keyboard navigation and labelled controls.
- [ ] Direct post links and browser refresh/navigation work as documented.
- [ ] Fictional authors and demo-only purpose are explicit; no simulated identity is presented as real authentication.
- [ ] Seed/reset behaviour matches the accepted data policy; startup does not silently overwrite data.
- [ ] If durable storage is accepted, successful changes survive application restart.

## Maintainability and reproducibility

- [ ] A fresh checkout can be started using the documented prerequisites and commands.
- [ ] Frontend and backend build using their recorded versions and build tools.
- [ ] Code is split into understandable responsibilities, without unnecessary frameworks.
- [ ] Actual capabilities, limitations, data/API contract, and reset scope are documented.
- [ ] Runtime data, generated output, and local secrets are not accidentally included in source changes.
- [ ] No optional exercise solution has been built into the baseline.

## How much checking

Use a short end-to-end walkthrough and focused automated checks where they protect
meaningful rules: validation, ordering, CRUD persistence, missing records, and reset
behaviour are candidates. Pick checks suited to the actual implementation. Do not
add redundant tests that merely mirror code, a blanket coverage target, or an
exhaustive browser/platform matrix.

Record what actually ran, its result, and any skipped or blocked checks. Never
substitute a passing build for evidence that the user flows work. Use only fictional
fixture data; destructive reset checks must target disposable demo data explicitly.

## Final review checkpoint

Give Gabriel:

1. The running app or exact checked instructions for starting it.
2. A short capability summary and any differences from the approved scope.
3. Verification results and material known limitations.
4. The local working-tree state and any uncommitted changes.
5. The specific acceptance or decision still needed.

Implementation complete, human accepted, and committed/pushed are separate states.
Do not mark later states complete on Gabriel's behalf.
