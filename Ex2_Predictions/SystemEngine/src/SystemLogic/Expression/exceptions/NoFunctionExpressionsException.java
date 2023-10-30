package SystemLogic.Expression.exceptions;

public class NoFunctionExpressionsException extends Exception {

    public NoFunctionExpressionsException() {
        super("This function does not have any expressions");
    }

    public NoFunctionExpressionsException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "NoFunctionExpressionsException: " + getMessage();
    }
}
