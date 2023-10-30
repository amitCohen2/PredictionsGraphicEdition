package SystemLogic.definition.value.generator.random.impl.string;

import SystemLogic.definition.value.generator.random.api.AbstractRandomValueGenerator;
import SystemLogic.definition.value.generator.random.impl.numeric.AbstractNumericRandomGenerator;

public class StringRandomGenerator extends AbstractRandomValueGenerator<String> {


    @Override
    public String generateValue() {

        int length = random.nextInt(50) + 1; // Generate a random length between 1 and 50

        StringBuilder stringBuilder = new StringBuilder(length);
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ";

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    }
}
