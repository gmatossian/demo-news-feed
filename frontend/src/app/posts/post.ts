import { Author } from '../authors/author';

/** A feed entry: metadata plus a short excerpt produced by the backend. */
export interface PostSummary {
  id: number;
  title: string;
  excerpt: string;
  author: Author;
  /** ISO-8601 instant. */
  publishedAt: string;
  /** ISO-8601 instant, or null if the post was never edited. */
  updatedAt: string | null;
}

export interface Post {
  id: number;
  title: string;
  body: string;
  author: Author;
  publishedAt: string;
  updatedAt: string | null;
}

export interface NewPost {
  title: string;
  body: string;
  authorId: number;
}

/** Only the title and body can change after publication. */
export interface PostEdit {
  title: string;
  body: string;
}
