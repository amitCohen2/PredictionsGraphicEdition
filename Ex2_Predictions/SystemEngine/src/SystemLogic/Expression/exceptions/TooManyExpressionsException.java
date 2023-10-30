package SystemLogic.Expression.exceptions;

public class TooManyExpressionsException extends Exception {
    public TooManyExpressionsException() {
        super("This function has too many arguments (expressions) !");
    }

    public TooManyExpressionsException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "TooManyExpressionsException: " + getMessage();
    }
}
