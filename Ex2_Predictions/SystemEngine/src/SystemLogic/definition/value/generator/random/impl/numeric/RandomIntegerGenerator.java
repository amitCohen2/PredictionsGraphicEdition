package SystemLogic.definition.value.generator.random.impl.numeric;

public class RandomIntegerGenerator extends AbstractNumericRandomGenerator<Integer> {

    public RandomIntegerGenerator(Integer from, Integer to) {
        super(from, to);
    }

    @Override
    public Integer generateValue() {
        if (from > to) {
            //throw new IllegalArgumentException("Invalid range: 'from' must be less than 'to'");
            int range = from - to;
           return to + random.nextInt(range);
        }
        else if (from.equals(to)) {
            return from;
        }

        int range = to - from; // Calculate the range of values (to - from)
        return from + random.nextInt(range); // Generate random integer in the range [from, to)
    }
}
