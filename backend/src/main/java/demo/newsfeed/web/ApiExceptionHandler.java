package demo.newsfeed.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import demo.newsfeed.post.InvalidPostException;
import demo.newsfeed.post.PostNotFoundException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

/**
 * Maps application errors to RFC 9457 problem details. Spring's base class already
 * covers framework errors such as malformed JSON, unknown routes, or bad path IDs.
 */
@RestControllerAdvice
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    ProblemDetail handleNotFound(PostNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Post not found");
        return problem;
    }

    /** Validation failures include an {@code errors} object: field name to message. */
    @ExceptionHandler
    ProblemDetail handleInvalid(InvalidPostException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problem.setTitle("Invalid post");
        problem.setProperty("errors", exception.getFieldErrors());
        return problem;
    }

    /** Names the offending field when a request contains one the endpoint does not accept. */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (exception.getCause() instanceof UnrecognizedPropertyException unknown) {
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                    "Field '" + unknown.getPropertyName() + "' is not accepted by this endpoint.");
            problem.setTitle("Unsupported field");
            return handleExceptionInternal(exception, problem, headers, HttpStatus.BAD_REQUEST, request);
        }
        return super.handleHttpMessageNotReadable(exception, headers, status, request);
    }
}
