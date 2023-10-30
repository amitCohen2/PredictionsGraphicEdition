package design.body.execution.business;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public interface SimulationProgressData {

    DoubleProperty getSeconds();
    IntegerProperty getTicks();
    StringProperty getStatus();
    Integer getSimulationID();
}
