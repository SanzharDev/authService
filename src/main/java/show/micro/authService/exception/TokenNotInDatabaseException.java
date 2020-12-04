package show.micro.authService.exception;

public class TokenNotInDatabaseException extends Exception {

    private String message = "Token not present in Database";

    @Override
    public String getMessage() {
        return message;
    }
}
