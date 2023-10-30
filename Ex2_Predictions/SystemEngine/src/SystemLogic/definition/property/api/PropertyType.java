package SystemLogic.definition.property.api;

public enum PropertyType {
    DECIMAL {

        public Integer convert(Object value) {
            if (!(value instanceof Integer)) {
                throw new IllegalArgumentException("value " + value +
                        " is not of a DECIMAL type (expected Integer class)");
            }
            return (Integer) value;
        }
    }, BOOLEAN {
        public Boolean convert(Object value) {
            return null;
        }
    }, FLOAT {
        public Double convert(Object value) {
            if (!(value instanceof Number)) {
                throw new IllegalArgumentException("value " + value +
                        " is not of a FLOAT type (expected Double class)");
            }
            return ((Number) value).doubleValue();
        }
    }, STRING {
        public String convert(Object value) {
            return null;
        }
    };

    public abstract <T> T convert(Object value);
}