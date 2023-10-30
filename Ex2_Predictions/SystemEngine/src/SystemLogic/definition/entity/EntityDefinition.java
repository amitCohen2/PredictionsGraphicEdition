package SystemLogic.definition.entity;

import SystemLogic.definition.property.api.PropertyDefinition;

import java.io.Serializable;
import java.util.List;

public interface EntityDefinition extends Serializable {
    String getName();
    int getPopulation();
    List<PropertyDefinition> getProps();
     void setProps(List<PropertyDefinition> props);
}
