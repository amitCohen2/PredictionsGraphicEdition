package design.body.execution.business;

import javafx.beans.property.*;

public class SimulationProgress implements SimulationProgressData{
    DoubleProperty seconds;
    StringProperty status;
    IntegerProperty ticks;
    public SimulationProgress(String status, int ticks, double seconds) {
        this.status = new SimpleStringProperty(status);
        this.ticks = new SimpleIntegerProperty(ticks);
        this.seconds = new SimpleDoubleProperty(seconds);
    }
    @Override
    public DoubleProperty getSeconds() {
        return seconds;
    }

    @Override
    public IntegerProperty getTicks() {
        return ticks;
    }

    @Override
    public StringProperty getStatus() {
        return status;
    }

    @Override
    public Integer getSimulationID() {
        return null;
    }
}
