import { Injectable, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';

/**
 * A short confirmation message ("Post deleted.") shown on the page it was meant for.
 * It is cleared as soon as the person navigates to a different page.
 */
@Injectable({ providedIn: 'root' })
export class Notice {
  private readonly router = inject(Router);
  private readonly current = signal<{ message: string; url: string } | null>(null);

  readonly message = computed(() => this.current()?.message ?? null);

  constructor() {
    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        takeUntilDestroyed(),
      )
      .subscribe((event) => {
        if (event.urlAfterRedirects !== this.current()?.url) {
          this.current.set(null);
        }
      });
  }

  /** Shows a message on the given page; navigate there after calling this. */
  showOn(url: string, message: string): void {
    this.current.set({ message, url });
  }

  dismiss(): void {
    this.current.set(null);
  }
}
