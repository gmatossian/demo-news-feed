import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

export interface ResetResult {
  authors: number;
  posts: number;
}

/** Resets the local demo dataset and lets pages know when to reload. */
@Injectable({ providedIn: 'root' })
export class DemoData {
  private readonly http = inject(HttpClient);
  private readonly resets = signal(0);

  /** Increments after every successful reset; data-loading pages depend on it. */
  readonly version = this.resets.asReadonly();

  reset(): Observable<ResetResult> {
    return this.http
      .post<ResetResult>('/api/demo/reset', null)
      .pipe(tap(() => this.resets.update((count) => count + 1)));
  }
}
