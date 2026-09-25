import { Component, computed, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';

import { toApiError } from '../../core/api-error';
import { DemoData } from '../../demo-data/demo-data';
import { LoadError } from '../../shared/load-error';
import { PostMeta } from '../post-meta';
import { PostsApi } from '../posts-api';

/** The chronological feed: newest publication first. */
@Component({
  selector: 'app-feed-page',
  imports: [RouterLink, PostMeta, LoadError],
  templateUrl: './feed-page.html',
  styleUrl: './feed-page.css',
})
export class FeedPage {
  private readonly postsApi = inject(PostsApi);
  private readonly demoData = inject(DemoData);

  protected readonly posts = rxResource({
    // Reload whenever the demo data is reset.
    params: () => ({ dataVersion: this.demoData.version() }),
    stream: () => this.postsApi.feed(),
  });

  protected readonly loadError = computed(() => {
    const error = this.posts.error();
    return error ? toApiError(error) : null;
  });
}
