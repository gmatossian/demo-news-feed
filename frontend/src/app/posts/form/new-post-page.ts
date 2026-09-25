import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { Router, RouterLink } from '@angular/router';

import { AuthorsApi } from '../../authors/authors-api';
import { ApiError, toApiError } from '../../core/api-error';
import { Notice } from '../../core/notice';
import { LoadError } from '../../shared/load-error';
import { PostsApi } from '../posts-api';
import { PostForm, PostFormValue } from './post-form';

@Component({
  selector: 'app-new-post-page',
  imports: [RouterLink, PostForm, LoadError],
  templateUrl: './new-post-page.html',
})
export class NewPostPage {
  private readonly postsApi = inject(PostsApi);
  private readonly authorsApi = inject(AuthorsApi);
  private readonly router = inject(Router);
  private readonly notice = inject(Notice);

  protected readonly authors = rxResource({ stream: () => this.authorsApi.list() });
  protected readonly authorsError = computed(() => {
    const error = this.authors.error();
    return error ? toApiError(error) : null;
  });

  protected readonly saving = signal(false);
  protected readonly saveError = signal<ApiError | null>(null);

  protected publish(value: PostFormValue): void {
    this.saving.set(true);
    this.saveError.set(null);
    this.postsApi
      .create({ title: value.title, body: value.body, authorId: value.authorId! })
      .subscribe({
        next: (post) => {
          const url = `/posts/${post.id}`;
          this.notice.showOn(url, 'Post published.');
          void this.router.navigateByUrl(url);
        },
        error: (error: unknown) => {
          this.saving.set(false);
          this.saveError.set(toApiError(error));
        },
      });
  }

  protected cancel(): void {
    void this.router.navigateByUrl('/');
  }
}
