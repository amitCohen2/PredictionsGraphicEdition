package SystemLogic.action.impl;

import SystemLogic.Expression.exceptions.DivideByZeroException;
import SystemLogic.action.api.Rangeable;
import SystemLogic.action.impl.Exceptions.ValueOutOfRangeException;
import SystemLogic.action.api.AbstractAction;
import SystemLogic.action.api.ActionType;
import SystemLogic.Expression.api.Expression;
import SystemLogic.Expression.exceptions.FunctionNotOccurException;
import SystemLogic.Expression.exceptions.NoFunctionExpressionsException;
import SystemLogic.Expression.exceptions.TooManyExpressionsException;
import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.execution.context.Context;
import SystemLogic.definition.property.api.PropertyType;
import SystemLogic.execution.instance.enitty.EntityInstance;
import SystemLogic.execution.instance.enitty.manager.EntityInstanceManager;
import SystemLogic.execution.instance.property.PropertyInstance;
import SystemLogic.grid.imp.Grid;

import java.util.List;

public class IncreaseAction extends AbstractAction implements Rangeable {

    private final String property;
    private final Expression expression;


    public IncreaseAction(EntityDefinition entityDefinition, String property,
                          String byExpression) {
        super(ActionType.INCREASE, entityDefinition, byExpression);
        this.property = property;
        this.expression = getExpression();
    }

    public void invoke(Context context, EntityInstanceManager entityInstanceManager, Grid grid) throws TooManyExpressionsException, FunctionNotOccurException, NoFunctionExpressionsException, ValueOutOfRangeException, DivideByZeroException {
        PropertyInstance propertyInstance = context.getPrimaryEntityInstance().getPropertyByName(property);

        if (!verifyNumericPropertyType(propertyInstance)) {
            throw new IllegalArgumentException("increase action can't operate on a non-number property [" + property + "]");
        }

        Double propertyValue = PropertyType.FLOAT.convert(propertyInstance.getValue());
        Object increaseValue = expression.getExpressionValue(context);

        Double currentIncreaseValue = null;
        if (increaseValue instanceof Integer) {
            currentIncreaseValue = ((Integer) increaseValue).doubleValue();
        } else if (increaseValue instanceof Double) {
            currentIncreaseValue = (Double) increaseValue;
        } else {
            throw new IllegalArgumentException("increaseValue is not a Number");
        }

        double result = propertyValue + currentIncreaseValue;

        if (!isInsideRange(propertyInstance, result)) {
            throw new ValueOutOfRangeException(ActionType.INCREASE, ((Number) propertyInstance.getPropertyDefinition().getRange().getKey()).doubleValue()
                    , ((Number) propertyInstance.getPropertyDefinition().getRange().getValue()).doubleValue(), result, propertyInstance.getPropertyDefinition().getName());
        }

/*        if (result == Math.floor(result)) {
            propertyInstance.updateValue((int) result);

        } else {
            propertyInstance.updateValue(result);

        }*/
        propertyInstance.updateValue(result);
        context.getPrimaryEntityInstance().resetPropertyTick(propertyInstance);

    }

    private boolean verifyNumericPropertyType(PropertyInstance propertyValue) {
        return PropertyType.DECIMAL.equals(propertyValue.getPropertyDefinition().getType()) ||
                PropertyType.FLOAT.equals(propertyValue.getPropertyDefinition().getType());
    }

}
