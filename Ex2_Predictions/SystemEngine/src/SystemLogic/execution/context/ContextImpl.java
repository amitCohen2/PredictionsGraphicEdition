package SystemLogic.execution.context;

import SystemLogic.execution.instance.enitty.EntityInstance;
import SystemLogic.execution.instance.enitty.manager.EntityInstanceManager;
import SystemLogic.execution.instance.environment.api.ActiveEnvironment;
import SystemLogic.execution.instance.property.PropertyInstance;
import SystemLogic.grid.imp.Grid;

public class ContextImpl implements Context {

    private final EntityInstance primaryEntityInstance;

    private final ActiveEnvironment activeEnvironment;

    //private Grid  grid;

    @Override
    public ActiveEnvironment getActiveEnvironment(){
        return activeEnvironment;
    }

    public ContextImpl(EntityInstance primaryEntityInstance,
                       ActiveEnvironment activeEnvironment) {
        this.primaryEntityInstance = primaryEntityInstance;

        this.activeEnvironment = activeEnvironment;

    }

    @Override
    public EntityInstance getPrimaryEntityInstance() {
        return primaryEntityInstance;
    }
   // @Override

    //public EntityInstanceManager getEntityInstanceManager() {  return this.entityInstanceManager;}

    @Override
    public void removeEntity(EntityInstance entityInstance, Grid grid, EntityInstanceManager entityInstanceManager) {
        grid.clearLocation(entityInstance.getLocation());
        entityInstanceManager.killEntity(entityInstance,entityInstance.getId());


    }



    @Override
    public PropertyInstance getEnvironmentVariable(String name) {
        return activeEnvironment.getProperty(name);
    }
}
