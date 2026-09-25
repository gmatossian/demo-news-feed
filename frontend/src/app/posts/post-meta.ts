import { DatePipe } from '@angular/common';
import { Component, input } from '@angular/core';

import { Author } from '../authors/author';

/** Author, publication time, and (if edited) last edit time, in local time. */
@Component({
  selector: 'app-post-meta',
  imports: [DatePipe],
  template: `
    <p class="post-meta">
      <span
        >By <span class="post-meta__author">{{ author().name }}</span></span
      >
      <span aria-hidden="true">·</span>
      <time [attr.datetime]="publishedAt()">{{ publishedAt() | date: format }}</time>
      @if (updatedAt(); as updated) {
        <span aria-hidden="true">·</span>
        <span
          >Edited <time [attr.datetime]="updated">{{ updated | date: format }}</time></span
        >
      }
    </p>
  `,
  styles: `
    .post-meta {
      display: flex;
      flex-wrap: wrap;
      gap: 0 var(--space-2);
      color: var(--color-muted);
      font-size: var(--font-size-small);
    }

    .post-meta__author {
      font-weight: 600;
      color: var(--color-text);
    }
  `,
})
export class PostMeta {
  readonly author = input.required<Author>();
  readonly publishedAt = input.required<string>();
  readonly updatedAt = input<string | null>(null);

  protected readonly format = 'd MMM y, HH:mm';
}
