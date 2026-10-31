package com.ideationportal.core.error;

/**
 * Base for domain/business errors. Carries an HTTP-ish status and a safe, user-facing message.
 */
public class DomainException extends RuntimeException {

    public enum Kind {
        VALIDATION(400),
        UNAUTHORIZED(401),
        FORBIDDEN(403),
        NOT_FOUND(404),
        CONFLICT(409);

        private final int status;

        Kind(int status) {
            this.status = status;
        }

        public int status() {
            return status;
        }
    }

    private final Kind kind;

    public DomainException(Kind kind, String message) {
        super(message);
        this.kind = kind;
    }

    public Kind kind() {
        return kind;
    }

    public static DomainException validation(String message) {
        return new DomainException(Kind.VALIDATION, message);
    }

    public static DomainException unauthorized(String message) {
        return new DomainException(Kind.UNAUTHORIZED, message);
    }

    public static DomainException forbidden(String message) {
        return new DomainException(Kind.FORBIDDEN, message);
    }

    public static DomainException notFound(String message) {
        return new DomainException(Kind.NOT_FOUND, message);
    }

    public static DomainException conflict(String message) {
        return new DomainException(Kind.CONFLICT, message);
    }
}
