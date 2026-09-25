import { Component, ElementRef, input, output, viewChild } from '@angular/core';

let nextId = 0;

/**
 * A modal confirmation built on the native <dialog> element, which provides focus
 * trapping, Escape to cancel, and focus return. Projected content explains the action.
 */
@Component({
  selector: 'app-confirm-dialog',
  templateUrl: './confirm-dialog.html',
  styleUrl: './confirm-dialog.css',
})
export class ConfirmDialog {
  readonly heading = input.required<string>();
  readonly confirmLabel = input.required<string>();
  /** While true, both buttons are disabled and Escape does nothing. */
  readonly busy = input(false);
  readonly error = input<string | null>(null);

  readonly confirmed = output<void>();
  readonly closed = output<void>();

  protected readonly headingId = `confirm-dialog-heading-${nextId++}`;
  private readonly dialog = viewChild.required<ElementRef<HTMLDialogElement>>('dialog');

  open(): void {
    this.dialog().nativeElement.showModal();
  }

  close(): void {
    this.dialog().nativeElement.close();
  }

  protected onCancel(event: Event): void {
    if (this.busy()) {
      event.preventDefault();
    }
  }
}
