package  SystemLogic.definition.value.generator.random.impl.bool;

import SystemLogic.definition.value.generator.random.api.AbstractRandomValueGenerator;

public class RandomBooleanValueGenerator extends AbstractRandomValueGenerator<Boolean> {

    @Override
    public Boolean generateValue() {
        return random.nextDouble() < 0.5;
    }
}
