package com.ag.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is not found.
 *
 * <p>This exception is annotated with {@link ResponseStatus} to map it to an HTTP status code.
 * In this case, it maps to {@code HttpStatus.NOT_FOUND} (404), indicating that the server cannot find
 * the requested resource. This is often used to indicate that the resource does not exist or is unavailable.</p>
 *
 * @see org.springframework.http.HttpStatus#NOT_FOUND
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
