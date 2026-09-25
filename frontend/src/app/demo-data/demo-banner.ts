import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

import { toApiError } from '../core/api-error';
import { Notice } from '../core/notice';
import { ConfirmDialog } from '../shared/confirm-dialog';
import { DemoData } from './demo-data';

/** Identifies the app as a demo and offers the demo-data reset. */
@Component({
  selector: 'app-demo-banner',
  imports: [ConfirmDialog],
  templateUrl: './demo-banner.html',
  styleUrl: './demo-banner.css',
})
export class DemoBanner {
  private readonly demoData = inject(DemoData);
  private readonly notice = inject(Notice);
  private readonly router = inject(Router);

  protected readonly resetting = signal(false);
  protected readonly error = signal<string | null>(null);

  protected reset(dialog: ConfirmDialog): void {
    this.resetting.set(true);
    this.error.set(null);
    this.demoData.reset().subscribe({
      next: () => {
        this.resetting.set(false);
        dialog.close();
        this.notice.showOn('/', 'Demo data restored to the original fictional posts.');
        void this.router.navigateByUrl('/');
      },
      error: (error: unknown) => {
        this.resetting.set(false);
        this.error.set(`Reset failed. ${toApiError(error).message}`);
      },
    });
  }
}
