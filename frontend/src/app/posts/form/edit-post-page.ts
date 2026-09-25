import { Component, computed, inject, input, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { Router, RouterLink } from '@angular/router';

import { ApiError, toApiError } from '../../core/api-error';
import { Notice } from '../../core/notice';
import { LoadError } from '../../shared/load-error';
import { parsePostId } from '../post-id';
import { PostNotFound } from '../post-not-found';
import { PostsApi } from '../posts-api';
import { PostForm, PostFormValue } from './post-form';

@Component({
  selector: 'app-edit-post-page',
  imports: [RouterLink, PostForm, PostNotFound, LoadError],
  templateUrl: './edit-post-page.html',
})
export class EditPostPage {
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

  protected readonly saving = signal(false);
  protected readonly saveError = signal<ApiError | null>(null);

  /** Only the title and body are sent; the backend keeps the author and publication time. */
  protected saveChanges(id: number, value: PostFormValue): void {
    this.saving.set(true);
    this.saveError.set(null);
    this.postsApi.update(id, { title: value.title, body: value.body }).subscribe({
      next: () => {
        const url = `/posts/${id}`;
        this.notice.showOn(url, 'Changes saved.');
        void this.router.navigateByUrl(url);
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.saveError.set(toApiError(error));
      },
    });
  }

  protected cancel(id: number): void {
    void this.router.navigateByUrl(`/posts/${id}`);
  }
}
