package SystemLogic.Expression.exceptions;

public class DivideByZeroException extends Exception {
    public DivideByZeroException() {
        super("Division by zero is not allowed.");
    }
}