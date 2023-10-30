package SystemLogic.definition.value.generator.random.impl.numeric;

public class RandomFloatGenerator extends AbstractNumericRandomGenerator<Double> {

    public RandomFloatGenerator(Double from, Double to) {
        super(from, to);
    }

    @Override
    public Double generateValue() {
        if (from > to) {
            //throw new IllegalArgumentException("Invalid range: 'from' must be less than 'to'");
            double range = from - to;
            return to + random.nextDouble() * range;
        }
        else if (from.equals(to)) {
            return from;
        }

        double range = to - from; // Calculate the range of values (to - from)
        return from + random.nextDouble() * range; // Generate random value in the range [from, to)
    }
}
