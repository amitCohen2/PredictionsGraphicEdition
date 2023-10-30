package SystemLogic.execution.instance.property;

import SystemLogic.definition.property.api.PropertyDefinition;

import java.io.Serializable;


public interface PropertyInstance extends Serializable {
    PropertyDefinition getPropertyDefinition();
    Object getValue();
    void updateValue(Object val);

}
