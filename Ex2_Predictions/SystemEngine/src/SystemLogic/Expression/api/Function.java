package SystemLogic.Expression.api;

import SystemLogic.Expression.exceptions.DivideByZeroException;
import SystemLogic.Expression.exceptions.FunctionNotOccurException;
import SystemLogic.Expression.exceptions.NoFunctionExpressionsException;
import SystemLogic.Expression.exceptions.TooManyExpressionsException;
import SystemLogic.execution.context.Context;

import java.io.Serializable;

public interface Function extends Serializable {
    Object environment(String expression, Context context) throws NoFunctionExpressionsException,
            FunctionNotOccurException, TooManyExpressionsException ;
    public int random(String expression, Context context) throws TooManyExpressionsException,
            FunctionNotOccurException, NoFunctionExpressionsException;
    public Object evaluate(String expression, Context context) throws TooManyExpressionsException,
            FunctionNotOccurException, NoFunctionExpressionsException;

    public int ticks(String expression, Context context) throws TooManyExpressionsException,
            FunctionNotOccurException, NoFunctionExpressionsException;

    public double precent(String expression, Context context) throws
            TooManyExpressionsException, FunctionNotOccurException, NoFunctionExpressionsException, DivideByZeroException;


}
