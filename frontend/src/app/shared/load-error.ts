import { Component, input, output } from '@angular/core';

/** Error panel for data that failed to load, with a retry action. */
@Component({
  selector: 'app-load-error',
  template: `
    <div class="panel panel--error" role="alert">
      <p class="load-error__text">
        <strong>{{ what() }} could not be loaded.</strong> {{ message() }}
      </p>
      <button type="button" class="button button--secondary" (click)="retry.emit()">
        Try again
      </button>
    </div>
  `,
  styles: `
    .load-error__text {
      margin-bottom: var(--space-4);
    }
  `,
})
export class LoadError {
  /** What failed, e.g. "The feed". */
  readonly what = input.required<string>();
  readonly message = input.required<string>();
  readonly retry = output<void>();
}
