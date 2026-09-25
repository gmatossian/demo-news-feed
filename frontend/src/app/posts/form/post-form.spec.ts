import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostForm, PostFormValue } from './post-form';

describe('PostForm', () => {
  let fixture: ComponentFixture<PostForm>;
  let element: HTMLElement;
  let saved: PostFormValue[];

  async function render(inputs: Record<string, unknown>): Promise<void> {
    fixture = TestBed.createComponent(PostForm);
    fixture.componentRef.setInput('submitLabel', 'Publish');
    for (const [name, value] of Object.entries(inputs)) {
      fixture.componentRef.setInput(name, value);
    }
    saved = [];
    fixture.componentInstance.save.subscribe((value) => saved.push(value));
    element = fixture.nativeElement;
    await fixture.whenStable();
  }

  function type(selector: string, value: string): void {
    const field = element.querySelector<HTMLInputElement | HTMLTextAreaElement>(selector)!;
    field.value = value;
    field.dispatchEvent(new Event('input'));
  }

  async function submit(): Promise<void> {
    element.querySelector<HTMLButtonElement>('button[type="submit"]')!.click();
    await fixture.whenStable();
  }

  it('rejects whitespace-only input and does not emit', async () => {
    await render({ authors: [{ id: 1, name: 'Juniper Hale' }] });
    type('#post-title', '   ');
    type('#post-body', '\n\t ');

    await submit();

    expect(saved).toEqual([]);
    expect(element.querySelector('#post-title-error')?.textContent).toContain('Enter a title.');
    expect(element.querySelector('#post-body-error')?.textContent).toContain(
      'Enter the post text.',
    );
    expect(element.querySelector('#post-author-error')?.textContent).toContain('Choose an author.');
  });

  it('emits normalized text while keeping internal formatting', async () => {
    await render({ authors: [{ id: 1, name: 'Juniper Hale' }] });
    const select = element.querySelector<HTMLSelectElement>('#post-author')!;
    select.selectedIndex = 1;
    select.dispatchEvent(new Event('change'));
    type('#post-title', '  Hello  ');
    type('#post-body', '\r\nLine one\r\n\r\n  indented  \n');

    await submit();

    expect(saved).toEqual([{ title: 'Hello', body: 'Line one\n\n  indented', authorId: 1 }]);
  });

  it('shows the author read-only when editing', async () => {
    await render({
      fixedAuthor: { id: 2, name: 'Otto Brightwater' },
      initial: { title: 'Existing', body: 'Text' },
    });

    expect(element.querySelector('#post-author')).toBeNull();
    expect(element.textContent).toContain('Otto Brightwater');
    expect(element.querySelector<HTMLInputElement>('#post-title')!.value).toBe('Existing');
  });

  it('shows server field messages next to the matching field', async () => {
    await render({ authors: [{ id: 1, name: 'Juniper Hale' }] });
    fixture.componentRef.setInput('saveError', {
      status: 400,
      message: 'Some fields need attention.',
      fieldErrors: { title: 'Title must be a single line.' },
    });
    await submit();

    expect(element.querySelector('#post-title-error')?.textContent).toContain(
      'Title must be a single line.',
    );
  });
});
