package show.micro.authService.exception;

public class UsernameExtractionException extends Exception {

    private String message = "Can't extract username from presented JWT";

    @Override
    public String getMessage() {
        return message;
    }
}
