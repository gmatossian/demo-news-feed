/**
 * Text rules for post titles and bodies. These mirror the backend's PostText class
 * (backend/src/main/java/demo/newsfeed/post/PostText.java); keep both in step.
 *
 * - Line endings are normalized to "\n".
 * - Leading/trailing characters with the Unicode White_Space property are removed;
 *   everything in between (body line breaks, indentation) is kept.
 * - Lengths count Unicode code points, so an emoji counts as one character.
 */
export const TITLE_MAX = 120;
export const BODY_MAX = 5000;

const LINE_ENDINGS = /\r\n?/g;
const EDGE_WHITESPACE = /^\p{White_Space}+|\p{White_Space}+$/gu;

export function normalizeText(raw: string | null | undefined): string {
  return (raw ?? '').replace(LINE_ENDINGS, '\n').replace(EDGE_WHITESPACE, '');
}

/** Length in Unicode code points. */
export function textLength(text: string): number {
  let count = 0;
  for (const _ of text) {
    count++;
  }
  return count;
}

/** Validation message for a raw title, or null if it is acceptable. */
export function titleProblem(raw: string): string | null {
  const title = normalizeText(raw);
  if (title === '') {
    return 'Enter a title.';
  }
  if (title.includes('\n')) {
    return 'Title must be a single line.';
  }
  if (textLength(title) > TITLE_MAX) {
    return `Title must be ${TITLE_MAX} characters or fewer.`;
  }
  return null;
}

/** Validation message for a raw body, or null if it is acceptable. */
export function bodyProblem(raw: string): string | null {
  const body = normalizeText(raw);
  if (body === '') {
    return 'Enter the post text.';
  }
  if (textLength(body) > BODY_MAX) {
    return 'Post text must be 5,000 characters or fewer.';
  }
  return null;
}
