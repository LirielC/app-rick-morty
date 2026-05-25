package backend.exceptions

class ResourceNotFoundException extends RuntimeException {
    ResourceNotFoundException(String message) {
        super(message)
    }
}
