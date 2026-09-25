import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-not-found-page',
  imports: [RouterLink],
  template: `
    <div class="panel">
      <h1 class="page-title">Page not found</h1>
      <p>There is no page at this address.</p>
      <p class="not-found__link"><a routerLink="/">Go to the feed</a></p>
    </div>
  `,
  styles: `
    .not-found__link {
      margin-top: var(--space-4);
    }
  `,
})
export class NotFoundPage {}
