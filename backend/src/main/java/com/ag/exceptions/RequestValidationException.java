package com.ag.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a request fails validation.
 *
 * <p>This exception is annotated with {@link ResponseStatus} to map it to an HTTP status code.
 * In this case, it maps to {@code HttpStatus.BAD_REQUEST} (400), indicating that the server
 * cannot process the request due to a client error (e.g., malformed request syntax, invalid
 * request message framing, or deceptive request routing).</p>
 *
 * @see org.springframework.http.HttpStatus#BAD_REQUEST
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class RequestValidationException extends RuntimeException {
    public RequestValidationException(String message) {
        super(message);
    }
}
