package SystemLogic.definition.property.api;

import SystemLogic.definition.value.generator.api.ValueGenerator;

public abstract class AbstractPropertyDefinition<T> implements PropertyDefinition {

    private final String name;
    private final PropertyType propertyType;
    private final ValueGenerator<T> valueGenerator;



    public AbstractPropertyDefinition(String name, PropertyType propertyType, ValueGenerator<T> valueGenerator) {
        this.name = name;
        this.propertyType = propertyType;
        this.valueGenerator = valueGenerator;

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PropertyType getType() {
        return propertyType;
    }

    @Override
    public T generateValue() {
        return valueGenerator.generateValue();
    }


}
