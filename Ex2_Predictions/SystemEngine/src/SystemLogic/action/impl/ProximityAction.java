package SystemLogic.action.impl;

import SystemLogic.Expression.api.Expression;
import SystemLogic.Expression.exceptions.DivideByZeroException;
import SystemLogic.Expression.exceptions.FunctionNotOccurException;
import SystemLogic.Expression.exceptions.NoFunctionExpressionsException;
import SystemLogic.Expression.exceptions.TooManyExpressionsException;
import SystemLogic.Location.api.Location;
import SystemLogic.action.api.AbstractAction;
import SystemLogic.action.api.Action;
import SystemLogic.action.api.ActionType;
import SystemLogic.action.impl.Exceptions.ValueOutOfRangeException;
import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.execution.context.Context;
import SystemLogic.execution.context.ContextImpl;
import SystemLogic.execution.instance.enitty.EntityInstance;
import SystemLogic.execution.instance.enitty.manager.EntityInstanceManager;
import SystemLogic.grid.imp.Grid;

import java.util.ArrayList;

public class ProximityAction extends AbstractAction {


    private Expression depthExpression;

    private EntityDefinition sourceEntity;

    private EntityDefinition targetEntity;

    private Grid grid;

    private ArrayList<Action> thenActions;


    public ProximityAction(ActionType actionType, EntityDefinition sourceEntity, EntityDefinition targetEntity,String byExpression,Grid grid,  ArrayList<Action> thenActions) {
        super(actionType, sourceEntity, byExpression);
        this.sourceEntity = sourceEntity;
        this.targetEntity = targetEntity;
        this.depthExpression = initExpression(byExpression);
        this.grid = grid;
        this.thenActions = thenActions;
    }


    @Override
    public void invoke(Context context, EntityInstanceManager entityInstanceManager, Grid grid) throws TooManyExpressionsException, FunctionNotOccurException, NoFunctionExpressionsException, ValueOutOfRangeException , DivideByZeroException{

        if(context.getPrimaryEntityInstance().getEntityDefinition().equals(sourceEntity)) {
            EntityInstance entityAround = findEntitysAround(context);
            context.getPrimaryEntityInstance().setSecondaryEntityInstance(entityAround);
            Context secondContext = new ContextImpl(entityAround, context.getActiveEnvironment());
            // if the condition apply
            if (entityAround != null) {
                ArrayList<Action> actions = thenActions;
                if (actions != null) {
                    for (Action action : actions) {
                        action.invoke(context,entityInstanceManager,grid);//TODO: find a way to pass also the secound entity data to the function
                    }
                }
            }
        }
        context.getPrimaryEntityInstance().setSecondaryEntityInstance(null);
    }
    public void setExpression(String byExpression){
        this.depthExpression = initExpression(byExpression);
    }



    private EntityInstance findEntitysAround(Context context) throws TooManyExpressionsException, FunctionNotOccurException, NoFunctionExpressionsException, DivideByZeroException {
        Location sourceLocation = context.getPrimaryEntityInstance().getLocation();
        double depth = (double) depthExpression.getExpressionValue(context);
       return grid.findEntitysAround(sourceLocation, targetEntity,depth);


        }}
