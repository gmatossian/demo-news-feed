import { Routes } from '@angular/router';

const APP_NAME = 'Demo News Feed';

export const routes: Routes = [
  {
    path: '',
    title: `Feed · ${APP_NAME}`,
    loadComponent: () => import('./posts/feed/feed-page').then((m) => m.FeedPage),
  },
  {
    path: 'posts/new',
    title: `New post · ${APP_NAME}`,
    loadComponent: () => import('./posts/form/new-post-page').then((m) => m.NewPostPage),
  },
  {
    path: 'posts/:id',
    title: `Post · ${APP_NAME}`,
    loadComponent: () => import('./posts/detail/post-detail-page').then((m) => m.PostDetailPage),
  },
  {
    path: 'posts/:id/edit',
    title: `Edit post · ${APP_NAME}`,
    loadComponent: () => import('./posts/form/edit-post-page').then((m) => m.EditPostPage),
  },
  {
    path: '**',
    title: `Page not found · ${APP_NAME}`,
    loadComponent: () => import('./not-found/not-found-page').then((m) => m.NotFoundPage),
  },
];
