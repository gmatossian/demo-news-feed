import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { NewPost, Post, PostEdit, PostSummary } from './post';

@Injectable({ providedIn: 'root' })
export class PostsApi {
  private readonly http = inject(HttpClient);

  feed(): Observable<PostSummary[]> {
    return this.http.get<PostSummary[]>('/api/posts');
  }

  get(id: number): Observable<Post> {
    return this.http.get<Post>(`/api/posts/${id}`);
  }

  create(post: NewPost): Observable<Post> {
    return this.http.post<Post>('/api/posts', post);
  }

  update(id: number, edit: PostEdit): Observable<Post> {
    return this.http.put<Post>(`/api/posts/${id}`, edit);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`/api/posts/${id}`);
  }
}
