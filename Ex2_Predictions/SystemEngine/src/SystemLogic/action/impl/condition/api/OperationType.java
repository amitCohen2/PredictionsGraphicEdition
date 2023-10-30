package SystemLogic.action.impl.condition.api;

import java.io.Serializable;

public enum OperationType implements Serializable {
    EQUAL {
        public int activateOperation(Object object1, Object object2) {
            return object1.equals(object2) ? 1 : 0;
        }
    }, NOT_EQUAL {
        public int activateOperation(Object object1, Object object2) {
            return object1.equals(object2) ? 0 : 1;
        }
    }, BT {
        public int activateOperation(Object object1, Object object2) {
            if (!(object1 instanceof Number) || !(object2 instanceof Number)) {
                throw new IllegalArgumentException("value is not of a Number type (expected Double class)");
            }
            double value1 = ((Number) object1).doubleValue();
            double value2 = ((Number) object2).doubleValue();
            return value1 > value2 ? 1 : 0;
        }
    }, LT {
        public int activateOperation(Object object1, Object object2) {
            if (!(object1 instanceof Number) || !(object2 instanceof Number)) {
                throw new IllegalArgumentException("value is not of a Number type (expected Double class)");
            }
            double value1 = ((Number) object1).doubleValue();
            double value2 = ((Number) object2).doubleValue();

            return value1 < value2 ? 1 : 0;
        }
    };

    public abstract int activateOperation(Object object1, Object object2);
}

