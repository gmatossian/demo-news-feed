import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { Author } from './author';

@Injectable({ providedIn: 'root' })
export class AuthorsApi {
  private readonly http = inject(HttpClient);

  list(): Observable<Author[]> {
    return this.http.get<Author[]>('/api/authors');
  }
}
