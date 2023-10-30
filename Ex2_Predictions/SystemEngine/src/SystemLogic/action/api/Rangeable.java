package SystemLogic.action.api;

import SystemLogic.action.impl.Exceptions.ValueOutOfRangeException;
import SystemLogic.execution.instance.property.PropertyInstance;

import java.io.Serializable;

import static java.util.Collections.swap;

public interface Rangeable <T> extends Serializable {
    public default <T extends Number> boolean isInsideRange(PropertyInstance propertyInstance, T result) throws ValueOutOfRangeException {
        Number fromNumber = (Number) propertyInstance.getPropertyDefinition().getRange().getKey();
        Number toNumber = (Number) propertyInstance.getPropertyDefinition().getRange().getValue();

        double from = fromNumber.doubleValue();
        double to = toNumber.doubleValue();

        double resultValue = result.doubleValue();
        //my swap function
        if (from > to) {
            double tmp = from;
            from = to;
            to = tmp;
        }

        if (resultValue > to || resultValue < from) {
            return false;
        }

        return true;
    }
}
