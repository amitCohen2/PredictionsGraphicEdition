package SystemLogic.definition.property.impl;

import SystemLogic.definition.property.api.AbstractPropertyDefinition;
import SystemLogic.definition.property.api.PropertyType;
import SystemLogic.definition.value.generator.api.ValueGenerator;
import javafx.util.Pair;

public class BooleanPropertyDefinition extends AbstractPropertyDefinition<Boolean> {
    public BooleanPropertyDefinition(String name, ValueGenerator<Boolean> valueGenerator) {
        super(name, PropertyType.BOOLEAN, valueGenerator);
    }

    @Override
    public Pair getRange() {
        return null;
    }
}
