import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { Title } from '@angular/platform-browser';
import { Router, RouterLink } from '@angular/router';

import { toApiError } from '../../core/api-error';
import { Notice } from '../../core/notice';
import { ConfirmDialog } from '../../shared/confirm-dialog';
import { LoadError } from '../../shared/load-error';
import { parsePostId } from '../post-id';
import { PostMeta } from '../post-meta';
import { PostNotFound } from '../post-not-found';
import { PostsApi } from '../posts-api';

@Component({
  selector: 'app-post-detail-page',
  imports: [RouterLink, PostMeta, PostNotFound, LoadError, ConfirmDialog],
  templateUrl: './post-detail-page.html',
  styleUrl: './post-detail-page.css',
})
export class PostDetailPage {
  /** Route parameter, bound by the router. */
  readonly id = input<string>();

  private readonly postsApi = inject(PostsApi);
  private readonly router = inject(Router);
  private readonly notice = inject(Notice);

  protected readonly postId = computed(() => parsePostId(this.id()));
  protected readonly post = rxResource({
    params: () => this.postId() ?? undefined,
    stream: ({ params: id }) => this.postsApi.get(id),
  });

  protected readonly loadError = computed(() => {
    const error = this.post.error();
    return error ? toApiError(error) : null;
  });
  protected readonly notFound = computed(
    () => this.postId() === null || this.loadError()?.status === 404,
  );

  protected readonly deleting = signal(false);
  protected readonly deleteError = signal<string | null>(null);

  constructor() {
    const title = inject(Title);
    effect(() => {
      if (this.post.hasValue()) {
        title.setTitle(`${this.post.value().title} · Demo News Feed`);
      }
    });
  }

  protected deletePost(id: number, dialog: ConfirmDialog): void {
    this.deleting.set(true);
    this.deleteError.set(null);
    this.postsApi.delete(id).subscribe({
      next: () => {
        dialog.close();
        this.notice.showOn('/', 'Post deleted.');
        void this.router.navigateByUrl('/');
      },
      error: (error: unknown) => {
        this.deleting.set(false);
        this.deleteError.set(`The post was not deleted. ${toApiError(error).message}`);
      },
    });
  }
}
