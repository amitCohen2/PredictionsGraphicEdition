package SystemLogic.definition.property.api;

import javafx.util.Pair;

import java.io.Serializable;
import java.util.List;

public interface PropertyDefinition<T> extends Serializable {
    String getName();
    PropertyType getType();
    Object generateValue();


    Pair<T, T> getRange();
}