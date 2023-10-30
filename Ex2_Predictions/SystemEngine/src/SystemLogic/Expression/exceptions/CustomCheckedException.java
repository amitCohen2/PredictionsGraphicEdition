package SystemLogic.Expression.exceptions;

public class CustomCheckedException extends Exception {
    public CustomCheckedException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "CustomCheckedException: " + getMessage();
    }
}





