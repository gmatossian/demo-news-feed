import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  TestRequest,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { NewPostPage } from './new-post-page';

const UNCERTAIN =
  'Couldn’t confirm whether your changes were saved—check the feed before retrying.';

describe('NewPostPage', () => {
  let fixture: ComponentFixture<NewPostPage>;
  let http: HttpTestingController;
  let element: HTMLElement;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    });
    http = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(NewPostPage);
    element = fixture.nativeElement;
    // Render without waiting for the pending authors request, then answer it.
    TestBed.tick();
    http.expectOne('/api/authors').flush([{ id: 1, name: 'Juniper Hale' }]);
    await fixture.whenStable();
  });

  afterEach(() => http.verify());

  function fillAndSubmit(): TestRequest {
    const select = element.querySelector<HTMLSelectElement>('#post-author')!;
    select.selectedIndex = 1;
    select.dispatchEvent(new Event('change'));
    const title = element.querySelector<HTMLInputElement>('#post-title')!;
    title.value = 'My draft';
    title.dispatchEvent(new Event('input'));
    const body = element.querySelector<HTMLTextAreaElement>('#post-body')!;
    body.value = 'Words I do not want to lose';
    body.dispatchEvent(new Event('input'));

    element.querySelector<HTMLButtonElement>('button[type="submit"]')!.click();
    return http.expectOne({ method: 'POST', url: '/api/posts' });
  }

  function alertText(): string {
    return element.querySelector('[role="alert"]')?.textContent ?? '';
  }

  function expectDraftKept(): void {
    expect(element.querySelector<HTMLInputElement>('#post-title')!.value).toBe('My draft');
    expect(element.querySelector<HTMLTextAreaElement>('#post-body')!.value).toBe(
      'Words I do not want to lose',
    );
    expect(element.querySelector<HTMLButtonElement>('button[type="submit"]')!.disabled).toBe(false);
  }

  it('sends the normalized draft', () => {
    const request = fillAndSubmit();
    expect(request.request.body).toEqual({
      title: 'My draft',
      body: 'Words I do not want to lose',
      authorId: 1,
    });
    request.flush({ id: 1000 });
  });

  it('reports an unconfirmed outcome and keeps the draft when the backend is unreachable', async () => {
    fillAndSubmit().flush('Bad gateway', { status: 502, statusText: 'Bad Gateway' });
    await fixture.whenStable();

    expect(alertText()).toContain(UNCERTAIN);
    expect(alertText()).not.toContain('Not saved.');
    expectDraftKept();
  });

  it('reports an unconfirmed outcome for a server error', async () => {
    fillAndSubmit().flush(
      { status: 500, title: 'Internal Server Error' },
      { status: 500, statusText: 'Internal Server Error' },
    );
    await fixture.whenStable();

    expect(alertText()).toContain(UNCERTAIN);
    expectDraftKept();
  });

  it('keeps definite validation failures distinct', async () => {
    fillAndSubmit().flush(
      {
        status: 400,
        title: 'Invalid post',
        detail: 'Some fields need attention.',
        errors: { title: 'Title must be a single line.' },
      },
      { status: 400, statusText: 'Bad Request' },
    );
    await fixture.whenStable();

    expect(alertText()).toContain('Not saved. Some fields need attention.');
    expect(alertText()).not.toContain(UNCERTAIN);
    expect(element.querySelector('#post-title-error')?.textContent).toContain(
      'Title must be a single line.',
    );
    expectDraftKept();
  });
});
