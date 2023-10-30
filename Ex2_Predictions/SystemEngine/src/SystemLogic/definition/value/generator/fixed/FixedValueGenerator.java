package  SystemLogic.definition.value.generator.fixed;

import SystemLogic.definition.value.generator.api.ValueGenerator;

public class FixedValueGenerator<T> implements ValueGenerator<T> {

    private final T fixedValue;

    public FixedValueGenerator(T fixedValue) {
        this.fixedValue = fixedValue;
    }

    @Override
    public T generateValue() {
        return fixedValue;
    }
}
