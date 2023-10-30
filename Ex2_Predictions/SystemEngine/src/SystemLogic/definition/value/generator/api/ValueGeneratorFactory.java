package SystemLogic.definition.value.generator.api;

import SystemLogic.definition.value.generator.fixed.FixedValueGenerator;
import SystemLogic.definition.value.generator.random.impl.bool.RandomBooleanValueGenerator;
import SystemLogic.definition.value.generator.random.impl.numeric.RandomFloatGenerator;
import SystemLogic.definition.value.generator.random.impl.numeric.RandomIntegerGenerator;
import SystemLogic.definition.value.generator.random.impl.string.StringRandomGenerator;

import java.io.Serializable;

public interface ValueGeneratorFactory extends Serializable {

    static <T> ValueGenerator<T> createFixed(T value) {
        return new FixedValueGenerator<>(value);
    }

    static ValueGenerator<Boolean> createRandomBoolean() {
        return new RandomBooleanValueGenerator();
    }

    static ValueGenerator<String> createRandomString() {
        return new StringRandomGenerator();
    }

    static ValueGenerator<Integer> createRandomInteger(Integer from, Integer to) {
        return new RandomIntegerGenerator(from, to);
    }

    static ValueGenerator<Double> createRandomFloat(Double from, Double to) {
        return new RandomFloatGenerator(from, to);
    }

}
