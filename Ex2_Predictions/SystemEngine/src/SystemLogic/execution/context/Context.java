package SystemLogic.execution.context;
import SystemLogic.execution.instance.enitty.EntityInstance;
import SystemLogic.execution.instance.enitty.manager.EntityInstanceManager;
import SystemLogic.execution.instance.environment.api.ActiveEnvironment;
import SystemLogic.execution.instance.property.PropertyInstance;
import SystemLogic.grid.imp.Grid;

import java.io.Serializable;

public interface Context  extends Serializable {
    EntityInstance getPrimaryEntityInstance();
   void removeEntity(EntityInstance entityInstance, Grid grid, EntityInstanceManager entityInstanceManager);
    PropertyInstance getEnvironmentVariable(String name);

    ActiveEnvironment getActiveEnvironment();



}
