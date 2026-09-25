# Frontend — Demo News Feed

Angular 22 single-page app for the demo news feed. **Demo and learning purposes only.**

See the [repository README](../README.md) for prerequisites and how to run the whole
application, and [docs/api.md](../docs/api.md) for the backend contract it uses.

| Command | Purpose |
| --- | --- |
| `npm ci` | Install the locked dependencies (project-local; nothing global). |
| `npm start` | Dev server on http://localhost:4301, forwarding `/api` to http://localhost:8081 (see `proxy.conf.json`). |
| `npm test` | Run the unit tests once (Vitest). |
| `npm run build` | Production build into `dist/`. |

Code layout (`src/app/`):

- `posts/` — feed, detail, and compose/edit pages; the shared post form; text rules
  mirrored from the backend (`post-text-rules.ts`).
- `authors/`, `demo-data/` — API access for authors and the demo-data reset banner.
- `core/` — API error handling and the confirmation notice.
- `shared/` — confirmation dialog and load-error panel.
- `layout/`, `not-found/` — site header and the unknown-route page.
- `src/styles.css` — shared design tokens (colours, spacing, type) and base styles.
