import {
  Component,
  ElementRef,
  Injector,
  OnInit,
  afterNextRender,
  computed,
  effect,
  inject,
  input,
  output,
  signal,
} from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';

import { Author } from '../../authors/author';
import { ApiError, isUncertainOutcome } from '../../core/api-error';
import {
  BODY_MAX,
  TITLE_MAX,
  bodyProblem,
  normalizeText,
  textLength,
  titleProblem,
} from '../post-text-rules';

/** Normalized form output. `authorId` is only meaningful when composing. */
export interface PostFormValue {
  title: string;
  body: string;
  authorId: number | null;
}

type FieldName = 'title' | 'body' | 'authorId';

/**
 * The compose/edit form. It validates with the same rules as the backend, keeps the
 * person's text on failed saves, and shows server messages next to the matching field.
 */
@Component({
  selector: 'app-post-form',
  imports: [ReactiveFormsModule],
  templateUrl: './post-form.html',
  styleUrl: './post-form.css',
})
export class PostForm implements OnInit {
  /** Authors to choose from when composing. */
  readonly authors = input<Author[]>([]);
  /** When editing: the post's author, shown read-only. */
  readonly fixedAuthor = input<Author | null>(null);
  readonly initial = input<{ title: string; body: string }>({ title: '', body: '' });
  readonly submitLabel = input.required<string>();
  readonly saving = input(false);
  readonly saveError = input<ApiError | null>(null);

  readonly save = output<PostFormValue>();
  readonly cancel = output<void>();

  /** The save may have succeeded even though no confirmation arrived. */
  protected readonly saveUncertain = computed(() => {
    const error = this.saveError();
    return error !== null && isUncertainOutcome(error);
  });

  protected readonly titleMax = TITLE_MAX;
  protected readonly bodyMax = BODY_MAX;
  protected readonly submitted = signal(false);
  protected readonly form = new FormGroup({
    authorId: new FormControl<number | null>(null),
    title: new FormControl('', { nonNullable: true, validators: textRule(titleProblem) }),
    body: new FormControl('', { nonNullable: true, validators: textRule(bodyProblem) }),
  });

  private readonly host = inject<ElementRef<HTMLElement>>(ElementRef);
  private readonly injector = inject(Injector);

  constructor() {
    effect(() => {
      const fieldErrors = this.saveError()?.fieldErrors ?? {};
      for (const [field, message] of Object.entries(fieldErrors)) {
        this.form.get(field)?.setErrors({ rule: message });
      }
    });
  }

  ngOnInit(): void {
    this.form.patchValue(this.initial());
    const author = this.fixedAuthor();
    if (author) {
      this.form.controls.authorId.setValue(author.id);
    } else {
      this.form.controls.authorId.setValidators(Validators.required);
      this.form.controls.authorId.updateValueAndValidity();
    }
  }

  /** The message to show for a field, once the person has interacted or submitted. */
  protected errorFor(field: FieldName): string | null {
    const control = this.form.controls[field];
    if (!control.errors || !(control.touched || this.submitted())) {
      return null;
    }
    return control.errors['rule'] ?? (control.errors['required'] ? 'Choose an author.' : null);
  }

  /** Characters counted exactly as the backend counts them. */
  protected lengthOf(field: 'title' | 'body'): number {
    return textLength(normalizeText(this.form.controls[field].value));
  }

  protected submit(): void {
    this.submitted.set(true);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      afterNextRender(() => this.focusFirstInvalidField(), { injector: this.injector });
      return;
    }
    const value = this.form.getRawValue();
    this.save.emit({
      title: normalizeText(value.title),
      body: normalizeText(value.body),
      authorId: value.authorId,
    });
  }

  private focusFirstInvalidField(): void {
    this.host.nativeElement.querySelector<HTMLElement>('[aria-invalid="true"]')?.focus();
  }
}

/** Adapts a post text rule to an Angular validator: `{ rule: message }` when it fails. */
function textRule(problem: (value: string) => string | null): ValidatorFn {
  return (control: AbstractControl<string>): ValidationErrors | null => {
    const message = problem(control.value);
    return message ? { rule: message } : null;
  };
}
