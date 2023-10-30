package design.header;

import javafx.beans.property.IntegerProperty;

public interface SimulationsQueue {
    IntegerProperty getSimulationsFinished();
    IntegerProperty getSimulationsWaiting();
    IntegerProperty getSimulationsInProgress();


}
