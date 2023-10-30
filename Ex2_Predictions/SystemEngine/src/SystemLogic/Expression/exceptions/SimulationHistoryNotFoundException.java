package SystemLogic.Expression.exceptions;

public class SimulationHistoryNotFoundException extends Exception {
    public SimulationHistoryNotFoundException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "There is no history of simulations to show mother fucker";
    }
}