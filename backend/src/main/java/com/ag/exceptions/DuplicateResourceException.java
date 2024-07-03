package com.ag.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an attempt is made to create a resource that already exists.
 *
 * <p>This exception is annotated with {@link ResponseStatus} to map it to an HTTP status code.
 * In this case, it maps to {@code HttpStatus.CONFLICT} (409), indicating that the request could
 * not be completed due to a conflict with the current state of the resource.</p>
 *
 * @see org.springframework.http.HttpStatus#CONFLICT
 */
@ResponseStatus(code = HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
