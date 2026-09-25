import { HttpErrorResponse } from '@angular/common/http';

/** A request failure reduced to what the UI needs to explain it. */
export interface ApiError {
  /** HTTP status, or 0 when the backend could not be reached. */
  status: number;
  message: string;
  /** Per-field validation messages from a 400 response, keyed by field name. */
  fieldErrors: Record<string, string>;
}

/**
 * Whether a write may or may not have been applied. Only a 4xx problem-details answer
 * from the backend is a definite rejection; an unreachable backend, a proxy failure, a
 * server error, or an unrecognized failure leaves the outcome unknown.
 */
export function isUncertainOutcome(error: ApiError): boolean {
  return error.status <= 0 || error.status >= 500;
}

const UNREACHABLE =
  'Cannot reach the backend. Check that it is running on http://localhost:8081 and try again.';

/**
 * Converts an HttpClient or resource error into an ApiError. The backend answers with
 * RFC 9457 problem details (`detail`, optional `errors`); anything else is treated as
 * an unreachable or failing backend.
 */
export function toApiError(error: unknown): ApiError {
  const cause = unwrapResourceError(error);
  if (!(cause instanceof HttpErrorResponse)) {
    return { status: -1, message: 'Something went wrong. Please try again.', fieldErrors: {} };
  }
  const problem = isProblem(cause.error) ? cause.error : null;
  if (!problem) {
    // No problem-details body: the dev-server proxy failed or the backend is down.
    return { status: 0, message: UNREACHABLE, fieldErrors: {} };
  }
  return {
    status: cause.status,
    message: problem.detail ?? `The request failed (HTTP ${cause.status}).`,
    fieldErrors: problem.errors ?? {},
  };
}

interface ProblemDetail {
  detail?: string;
  errors?: Record<string, string>;
}

function isProblem(body: unknown): body is ProblemDetail {
  return typeof body === 'object' && body !== null && 'status' in body;
}

/** Resources wrap non-Error failures (such as HttpErrorResponse) in an Error with a cause. */
function unwrapResourceError(error: unknown): unknown {
  if (error instanceof HttpErrorResponse) {
    return error;
  }
  return error instanceof Error && error.cause !== undefined ? error.cause : error;
}
