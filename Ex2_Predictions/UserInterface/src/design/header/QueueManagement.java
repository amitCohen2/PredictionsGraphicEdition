package design.header;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class QueueManagement implements SimulationsQueue{
    private IntegerProperty simulationsInProgress;
    private IntegerProperty simulationsFinished;
    private IntegerProperty simulationsWaiting;
    public QueueManagement() {
        simulationsWaiting = new SimpleIntegerProperty(0);
        simulationsFinished = new SimpleIntegerProperty(0);
        simulationsInProgress = new SimpleIntegerProperty(0);
    }

    @Override
    public IntegerProperty getSimulationsFinished() {
        return simulationsFinished;
    }

    @Override
    public IntegerProperty getSimulationsWaiting() {
        return simulationsWaiting;
    }

    @Override
    public IntegerProperty getSimulationsInProgress() {
        return simulationsInProgress;
    }
}
