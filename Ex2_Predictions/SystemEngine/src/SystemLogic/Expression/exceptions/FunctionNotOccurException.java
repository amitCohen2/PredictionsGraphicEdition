package SystemLogic.Expression.exceptions;

public class FunctionNotOccurException extends Exception {
    public FunctionNotOccurException() {
        super("This function type does not occur in our system!");
    }

    public FunctionNotOccurException(String message) {
        super("This function type does not occur in our system!" +message);
    }

    @Override
    public String toString() {
        return "FunctionNotOccurException: " + getMessage();
    }
}
