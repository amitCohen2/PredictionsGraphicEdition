package SystemLogic.execution.results.api;

import javafx.beans.property.SimpleStringProperty;

public interface PropertyRowColumn {
    public SimpleStringProperty getPropertyName();

    public SimpleStringProperty getConsistency();

    public SimpleStringProperty getAverage();
}
