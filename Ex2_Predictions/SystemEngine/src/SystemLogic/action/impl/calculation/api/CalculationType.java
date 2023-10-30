package SystemLogic.action.impl.calculation.api;


import java.io.Serializable;

public enum CalculationType implements Serializable {
    MULTIPLY {
        public Object calculate(Object object1, Object object2) {
            if (!(object1 instanceof Number) || !(object2 instanceof Number)) {
                throw new IllegalArgumentException("value is not of a Number type (expected Double class)");
            }
            double value1 = ((Number) object1).doubleValue();
            double value2 = ((Number) object2).doubleValue();
            return value1 * value2;
        }
    }, DIVIDE {
        public Object calculate(Object object1, Object object2) {
            if (!(object1 instanceof Number) || !(object2 instanceof Number)) {
                throw new IllegalArgumentException("value is not of a Number type (expected Double class)");
            }
            double value1 = ((Number) object1).doubleValue();
            double value2 = ((Number) object2).doubleValue();
            return value1 / value2;
        }
    };

    public abstract Object calculate(Object object1, Object object2);
}

