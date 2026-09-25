import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-post-not-found',
  imports: [RouterLink],
  template: `
    <div class="panel">
      <h1 class="page-title">Post not found</h1>
      <p>This post does not exist. It may have been deleted, or the link may be wrong.</p>
      <p class="post-not-found__link"><a routerLink="/">Go to the feed</a></p>
    </div>
  `,
  styles: `
    .post-not-found__link {
      margin-top: var(--space-4);
    }
  `,
})
export class PostNotFound {}
