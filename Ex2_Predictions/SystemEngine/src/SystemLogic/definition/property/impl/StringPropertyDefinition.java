package SystemLogic.definition.property.impl;

import SystemLogic.definition.property.api.AbstractPropertyDefinition;
import SystemLogic.definition.property.api.PropertyType;
import SystemLogic.definition.value.generator.api.ValueGenerator;
import javafx.util.Pair;

public class StringPropertyDefinition extends AbstractPropertyDefinition<String> {

    public StringPropertyDefinition(String name, ValueGenerator<String> valueGenerator) {
        super(name, PropertyType.STRING, valueGenerator);
    }

    @Override
    public Pair getRange() {
        return null;
    }
}