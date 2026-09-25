# Data and API contract

This describes the implemented baseline (2026-09-25). The backend is the source of
truth; the frontend applies the same text rules so people see problems before saving.

## Data

| Entity | Field | Notes |
| --- | --- | --- |
| Author | `id` | Fixed seed IDs 1–5. |
| | `name` | Fictional display name. Authors are labels, not accounts; there is no sign-in. |
| Post | `id` | Seed posts use fixed IDs 1–8 (reserved range below 1000). Posts created through the API get generated IDs from 1000 upwards. |
| | `title` | Plain text, single line, 1–120 characters. |
| | `body` | Plain text, 1–5,000 characters. Line breaks and indentation are kept. |
| | `author` | Chosen at creation; cannot change afterwards. |
| | `publishedAt` | Set by the server at creation (UTC, microsecond precision); never changes. |
| | `updatedAt` | `null` until a saved edit actually changes the title or body. |

Timestamps are ISO-8601 UTC strings in JSON (for example `2026-09-18T09:00:00Z`).
The UI shows them in the browser's local time zone, formatted like `18 Sep 2026, 11:00`.

## Text rules

Applied identically by `PostText.java` (backend) and `post-text-rules.ts` (frontend):

1. Line endings `\r\n` and `\r` become `\n`.
2. Leading and trailing characters with the Unicode `White_Space` property are removed
   (spaces, tabs, line breaks, non-breaking spaces, and so on). Internal whitespace is kept.
3. Length counts Unicode code points, so `😀` counts as one character.
4. After trimming: an empty title or body is rejected, a title containing a line break is
   rejected, and titles over 120 / bodies over 5,000 characters are rejected.

The trimmed, normalized text is what gets stored.

## Ordering

`GET /api/posts` returns every post ordered by `publishedAt` descending, then `id`
descending. Editing never moves a post. There is no pagination.

## Endpoints

All paths are under `http://localhost:8081`. Requests and responses are JSON.

| Method and path | Success | Errors |
| --- | --- | --- |
| `GET /api/posts` | 200, array of feed entries | — |
| `GET /api/posts/{id}` | 200, post | 404 |
| `POST /api/posts` | 201, post; `Location: /api/posts/{id}` | 400 |
| `PUT /api/posts/{id}` | 200, post | 400, 404 |
| `DELETE /api/posts/{id}` | 204, no body | 404 |
| `GET /api/authors` | 200, array of `{id, name}` sorted by name | — |
| `POST /api/demo/reset` | 200, `{"authors": 5, "posts": 8}` | — |

Request bodies:

- Create: `{"title": "...", "body": "...", "authorId": 1}`
- Edit: `{"title": "...", "body": "..."}`. Only these two fields are accepted.

Response shapes:

- Post: `{id, title, body, author: {id, name}, publishedAt, updatedAt}`
- Feed entry: `{id, title, excerpt, author: {id, name}, publishedAt, updatedAt}`. The
  excerpt is the body with whitespace collapsed to single spaces, cut to about 200
  characters near a word boundary, and ending in `…` when shortened.

Saving an edit with unchanged (normalized) text returns 200 and leaves `updatedAt` as it was.

## Errors

Errors use [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) problem details:

```json
{
  "title": "Invalid post",
  "status": 400,
  "detail": "Some fields need attention.",
  "instance": "/api/posts",
  "errors": {
    "title": "Enter a title.",
    "authorId": "Choose one of the listed authors."
  }
}
```

| Situation | Status | Notes |
| --- | --- | --- |
| Text or author rule broken | 400 | `errors` maps `title`, `body`, `authorId` to messages. |
| Unknown JSON field (for example `authorId` or `publishedAt` sent with an edit) | 400 | Title "Unsupported field". This is how the fixed author and publication time are enforced. |
| Malformed JSON, or a non-numeric ID in the path | 400 | Spring's default problem detail. |
| Post ID does not exist (or was deleted) | 404 | Title "Post not found". |

Validation messages: "Enter a title.", "Title must be a single line.",
"Title must be 120 characters or fewer.", "Enter the post text.",
"Post text must be 5,000 characters or fewer.", "Choose an author.",
"Choose one of the listed authors."

## Seed data and reset

- The dataset is committed at `backend/src/main/resources/seed/demo-data.json`: five
  fictional authors and eight original posts about the fictional town of Larchmere.
  Posts 5 and 6 share a publication time to show the tie-break; post 3 has been edited;
  post 8 contains literal `<b>` markup to show plain-text rendering.
- Schema: `backend/src/main/resources/schema.sql` runs at every start but only creates
  missing tables and indexes.
- A database counts as fresh when its `author` table is empty. On startup a fresh
  database is seeded; any other database is left untouched, even if every post has
  been deleted.
- Reset (`POST /api/demo/reset`, or the "Reset demo data" banner control) runs in one
  transaction. It deletes all posts and authors and re-inserts the dataset with its
  fixed IDs. If any step fails, the previous data remains. The generated-ID counter is
  not restarted, so posts created after a reset never reuse a seed ID or an earlier ID.
- Reset only changes the application's database tables. It does not touch source
  files, the committed seed file, or any other data.

## Storage

- The database is a file-backed H2 database at `backend/data/newsfeed.mv.db`, relative
  to the directory the backend is started from. Set `NEWSFEED_DATA_DIR` to use a
  different directory.
- `backend/data/` is gitignored.
- Automated tests use a separate in-memory database and never touch this file.
