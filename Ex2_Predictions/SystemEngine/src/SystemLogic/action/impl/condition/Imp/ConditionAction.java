package SystemLogic.action.impl.condition.Imp;

import SystemLogic.Expression.api.Expression;
import SystemLogic.Expression.exceptions.DivideByZeroException;
import SystemLogic.action.api.Rangeable;
import SystemLogic.action.impl.Exceptions.ValueOutOfRangeException;
import SystemLogic.action.api.AbstractAction;
import SystemLogic.action.api.Action;
import SystemLogic.action.api.ActionType;
import SystemLogic.Expression.exceptions.FunctionNotOccurException;
import SystemLogic.Expression.exceptions.NoFunctionExpressionsException;
import SystemLogic.Expression.exceptions.TooManyExpressionsException;
import SystemLogic.action.impl.condition.api.Condition;
import SystemLogic.action.impl.condition.api.OperationType;
import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.execution.context.Context;
import SystemLogic.execution.instance.enitty.EntityInstance;
import SystemLogic.execution.instance.enitty.manager.EntityInstanceManager;
import SystemLogic.grid.imp.Grid;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionAction extends AbstractAction {

    private final Condition condition;
    private final EntityDefinition entityDefinition;
    private  Expression expression;

    private Expression propExpression;

    public ConditionAction(ActionType actionType, EntityDefinition entityDefinition, String byExpression, Condition condition) {
        super(actionType, entityDefinition, byExpression);
        this.entityDefinition = entityDefinition;
        this.condition = condition;
        this.expression = initExpression(byExpression);
    }


    @Override
    public void invoke(Context context, EntityInstanceManager entityInstanceManager, Grid grid) throws TooManyExpressionsException, FunctionNotOccurException, NoFunctionExpressionsException, ValueOutOfRangeException , DivideByZeroException {

    if(context.getPrimaryEntityInstance().getEntityDefinition().equals(entityDefinition)) {
        int result = isConditionApply(this.condition, context);

        // if the condition apply
        if (result > 0) {
            ArrayList<Action> actions = condition.getThenActions();
            if (actions != null) {
                for (Action action : actions) {
                    action.invoke(context, entityInstanceManager,  grid);
                }
            }
        } else {
            ArrayList<Action> actions = condition.getElseActions();
            if (actions != null) {
                for (Action action : actions) {
                    action.invoke(context,entityInstanceManager,grid);
                }
            }

        }
    }
    }
    public void setExpression(String byExpression){
        this.expression = initExpression(byExpression);
    }

    public void setPropExpression(String propExpression){
        this.propExpression= initExpression(propExpression);
    }

    private int isConditionApply(Condition condition, Context context) throws TooManyExpressionsException, FunctionNotOccurException, NoFunctionExpressionsException , DivideByZeroException{
        //check if this is single condition
        Object  propertyValue;
        if (condition.isSingle()) {
            String propertyName;

            //check if this the root
            if (condition.getPropertyName() == null && condition.getConditions().size() > 0){
                propertyName = condition.getConditions().get(0).getPropertyName();
                setExpression(condition.getConditions().get(0).gerByExpression());
            }
            else { // not the root
                 propertyName =condition.getPropertyName();
                 setExpression(condition.gerByExpression());
            }
            if(propertyName.contains("ticks")){
                setPropExpression(propertyName);
                 propertyValue=  propExpression.getExpressionValue(context);
            }

            else{
                propertyValue = context.getPrimaryEntityInstance().getPropertyByName(propertyName).getValue();

            }


            switch (condition.getOperationType()) {
                case EQUAL:
                    return OperationType.EQUAL.activateOperation(
                            propertyValue,
                            expression.getExpressionValue(context));
                case NOT_EQUAL:
                    return OperationType.NOT_EQUAL.activateOperation(
                            propertyValue,
                            expression.getExpressionValue(context));
                case BT:
                    return OperationType.BT.activateOperation(
                            propertyValue,
                            expression.getExpressionValue(context));
                case LT:
                    return OperationType.LT.activateOperation(
                            propertyValue,
                            expression.getExpressionValue(context));
                default:
                    throw new IllegalArgumentException("Unsupported operation type");
            }
        } else {
            int result = condition.getLogical().equals("and") ? 1 : 0;
            if (condition.getConditions() != null) {
                for (Condition cond : condition.getConditions()) {
                    try {
                        if (condition.getLogical().equals("and")) {
                            result *= isConditionApply(cond, context);
                        } else if (condition.getLogical().equals("or")) {
                            result += isConditionApply(cond, context);
                        }
                    } catch (TooManyExpressionsException | FunctionNotOccurException | NoFunctionExpressionsException | NullPointerException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            return result;
        }}}


