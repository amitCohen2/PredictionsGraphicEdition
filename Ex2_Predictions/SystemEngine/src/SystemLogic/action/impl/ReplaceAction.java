package SystemLogic.action.impl;

import SystemLogic.Expression.exceptions.FunctionNotOccurException;
import SystemLogic.Expression.exceptions.NoFunctionExpressionsException;
import SystemLogic.Expression.exceptions.TooManyExpressionsException;
import SystemLogic.Location.api.Location;
import SystemLogic.action.api.AbstractAction;
import SystemLogic.action.api.ActionType;
import SystemLogic.action.impl.Exceptions.ValueOutOfRangeException;
import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.execution.context.Context;
import SystemLogic.execution.instance.enitty.EntityInstance;
import SystemLogic.execution.instance.enitty.EntityInstanceImpl;
import SystemLogic.execution.instance.enitty.manager.EntityInstanceManager;
import SystemLogic.grid.imp.Grid;

public class ReplaceAction  extends AbstractAction {
    EntityDefinition entityToKill;
     EntityDefinition entityToCreate;
    String mode;

    Grid grid;
    public EntityDefinition getEntityToCreate(){
        return entityToCreate;
    }

    public EntityDefinition getEntityToKill(){
        return entityToKill;
    }

    public ReplaceAction(EntityDefinition entityToKill,EntityDefinition entityToCreate, String mode, Grid grid) {
        super(ActionType.Replace, entityToKill, mode);
        this.entityToCreate = entityToCreate;
        this.entityToKill = entityToKill;
        this.mode = mode;
        this.grid =grid;
    }

    @Override
    public void invoke(Context context, EntityInstanceManager entityInstanceManager, Grid grid) throws TooManyExpressionsException, FunctionNotOccurException, NoFunctionExpressionsException, ValueOutOfRangeException {
        Location location =context.getPrimaryEntityInstance().getLocation();
        if(mode.equals("scratch")){
            context.removeEntity(context.getPrimaryEntityInstance(),grid,entityInstanceManager);


            entityInstanceManager.create(entityToCreate,grid);
        }
        else if(mode.equals("derived")){
            EntityInstance newEntityInstance = new EntityInstanceImpl(entityToCreate,context.getPrimaryEntityInstance().getId(),context.getPrimaryEntityInstance().getLocation());
            entityInstanceManager.Replace(context ,context.getPrimaryEntityInstance(),newEntityInstance, grid,entityInstanceManager);
        }


        context.getPrimaryEntityInstance().getTicksNotChangeMap().forEach((propertyInstance, integer) -> {
            integer =0;
        });
    }


}
