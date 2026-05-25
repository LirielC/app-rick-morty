package backend.exceptions

class UnauthorizedException extends RuntimeException {
    UnauthorizedException(String message) {
        super(message)
    }
}
