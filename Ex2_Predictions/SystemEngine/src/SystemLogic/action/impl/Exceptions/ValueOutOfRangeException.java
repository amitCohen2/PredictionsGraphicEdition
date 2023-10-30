package SystemLogic.action.impl.Exceptions;

import SystemLogic.action.api.ActionType;

import java.io.Serializable;

public class ValueOutOfRangeException extends Exception implements Serializable {
    public ValueOutOfRangeException(ActionType type, double from, double to, double value, String propertyName) {
        super("************************************************************************************\n" +
                "This value out of the property " +  propertyName + " range! cannot do this " + type + " action.\n" +
                "The current value: " + value + " The range: [" + from + ", " + to + "]." +
                "\n************************************************************************************");
    }

    public ValueOutOfRangeException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "ValueOutOfRangeException: " + getMessage();
    }
}
