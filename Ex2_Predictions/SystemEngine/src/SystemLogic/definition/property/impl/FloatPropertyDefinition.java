package SystemLogic.definition.property.impl;

import SystemLogic.definition.property.api.AbstractPropertyDefinition;
import SystemLogic.definition.property.api.PropertyType;
import SystemLogic.definition.value.generator.api.ValueGenerator;
import javafx.util.Pair;

public class FloatPropertyDefinition extends AbstractPropertyDefinition<Double>  {
    private final Pair<Double, Double> range;

    public FloatPropertyDefinition(String name, ValueGenerator<Double> valueGenerator, Double from, Double to) {
        super(name, PropertyType.FLOAT, valueGenerator);
        this.range = new Pair<>(from, to);
    }

    @Override
    public Pair<Double, Double> getRange() {
        return this.range;
    }
}
