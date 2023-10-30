package SystemLogic.Expression.api;

import SystemLogic.Expression.api.enums.ExpressionType;
import SystemLogic.Expression.exceptions.DivideByZeroException;
import SystemLogic.Expression.exceptions.FunctionNotOccurException;
import SystemLogic.Expression.exceptions.NoFunctionExpressionsException;
import SystemLogic.Expression.exceptions.TooManyExpressionsException;
import SystemLogic.execution.context.Context;

import java.io.Serializable;

public interface Expression  extends Serializable {
    ExpressionType getExpressionType();
    //public void invoke() throws FunctionNotOccurException, NoFunctionExpressionsException, TooManyExpressionsException;

    public Object getExpressionValue(Context context) throws NoFunctionExpressionsException, FunctionNotOccurException, TooManyExpressionsException, DivideByZeroException;
    public String getExpressionString();
}
