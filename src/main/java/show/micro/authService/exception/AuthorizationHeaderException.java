package show.micro.authService.exception;

public class AuthorizationHeaderException extends Exception {

    private String message = "Authorization header with valid JWT token must be present in request";

    @Override
    public String getMessage() {
        return message;
    }
}
