/** Parses a route parameter into a post ID, or null if it cannot be one. */
export function parsePostId(value: string | undefined): number | null {
  return value && /^[1-9]\d{0,15}$/.test(value) ? Number(value) : null;
}
