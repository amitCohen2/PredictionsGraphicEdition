package SystemLogic.execution.instance.property;
import SystemLogic.definition.property.api.PropertyDefinition;

import java.io.Serializable;

public class PropertyInstanceImpl implements PropertyInstance , Serializable {

    private PropertyDefinition propertyDefinition;
    private Object value;

    public PropertyInstanceImpl(PropertyDefinition propertyDefinition, Object value) {
        this.propertyDefinition = propertyDefinition;
        this.value = value;
    }

    @Override
    public PropertyDefinition getPropertyDefinition() {
        return propertyDefinition;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void updateValue(Object val) {
        this.value = val;
    }

}
