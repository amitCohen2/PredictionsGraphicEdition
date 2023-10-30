package SystemLogic.execution.results.imp;

import SystemLogic.execution.results.api.PropertyRowColumn;
import javafx.beans.property.SimpleStringProperty;

public class PropertyResultRow implements PropertyRowColumn {
    private final SimpleStringProperty propertyName;
    private final SimpleStringProperty consistency;
    private final SimpleStringProperty average;

    public PropertyResultRow(String propertyName, String consistency, String average) {
        this.propertyName = new SimpleStringProperty(propertyName);
        this.consistency = new SimpleStringProperty(consistency);
        this.average = new SimpleStringProperty(average);
    }

    public SimpleStringProperty getPropertyName() {
        return propertyName;
    }

    public SimpleStringProperty getConsistency() {
        return consistency;
    }

    public SimpleStringProperty getAverage() {
        return average;
    }
}







