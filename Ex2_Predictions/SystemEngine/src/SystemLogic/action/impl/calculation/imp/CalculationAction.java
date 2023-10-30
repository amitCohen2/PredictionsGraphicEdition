package SystemLogic.action.impl.calculation.imp;

import SystemLogic.Expression.exceptions.DivideByZeroException;
import SystemLogic.action.api.AbstractAction;
import SystemLogic.action.api.ActionType;
import SystemLogic.action.api.Rangeable;
import SystemLogic.action.impl.Exceptions.ValueOutOfRangeException;
import SystemLogic.action.impl.calculation.api.CalculationType;
import SystemLogic.Expression.api.Expression;
import SystemLogic.Expression.exceptions.FunctionNotOccurException;
import SystemLogic.Expression.exceptions.NoFunctionExpressionsException;
import SystemLogic.Expression.exceptions.TooManyExpressionsException;
import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.execution.context.Context;
import SystemLogic.execution.instance.enitty.manager.EntityInstanceManager;
import SystemLogic.execution.instance.property.PropertyInstance;
import SystemLogic.grid.imp.Grid;

public class CalculationAction extends AbstractAction implements Rangeable {
    private final String property;
    private final CalculationType calculationType;
    private final Expression expression1;
    private final Expression expression2;

    public CalculationAction(EntityDefinition entityDefinition, String property,
                          String byExpression1, String byExpression2, String calculationType) {
        super(ActionType.CALCULATION, entityDefinition, byExpression1);
        this.property = property;
        this.expression1 = getExpression();
        this.expression2 = initExpression(byExpression2);
        this.calculationType = initCalculationType(calculationType);
    }

    private CalculationType initCalculationType(String calculationType) {

        if (calculationType.equals(("divide"))) {
            return CalculationType.DIVIDE;
        }
        else if (calculationType.equals(("multiply"))) {
            return CalculationType.MULTIPLY;
        }
        else {
            throw new IllegalArgumentException("This Calculate Type " + calculationType + "does not supported!");
        }
    }

    @Override
    public void invoke(Context context, EntityInstanceManager entityInstanceManager, Grid grid) throws TooManyExpressionsException, FunctionNotOccurException, NoFunctionExpressionsException, ValueOutOfRangeException , DivideByZeroException {
        PropertyInstance propertyInstance = context.getPrimaryEntityInstance().getPropertyByName(property);
        double result = 0;

        switch (calculationType) {
            case DIVIDE:
                result = (Double) CalculationType.DIVIDE.calculate(expression1.getExpressionValue(context), expression2.getExpressionValue(context));
                break;
            case MULTIPLY:
                result = (Double) CalculationType.MULTIPLY.calculate(expression1.getExpressionValue(context), expression2.getExpressionValue(context));
                break;
            default:
                throw new IllegalArgumentException("This Calculate Type " + calculationType + "does not supported!");
        }

        if (!isInsideRange(propertyInstance, result)) {
            throw new ValueOutOfRangeException(ActionType.INCREASE, ((Number) propertyInstance.getPropertyDefinition().getRange().getKey()).doubleValue()
                    , ((Number) propertyInstance.getPropertyDefinition().getRange().getValue()).doubleValue(), result, propertyInstance.getPropertyDefinition().getName());
        }

        propertyInstance.updateValue(result);
    }

}
