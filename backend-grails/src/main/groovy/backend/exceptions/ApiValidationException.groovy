package backend.exceptions

class ApiValidationException extends RuntimeException {
    ApiValidationException(String message) {
        super(message)
    }
}
