package SystemLogic.execution.instance.enitty.manager;

import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.execution.context.Context;
import SystemLogic.execution.instance.enitty.EntityInstance;
import SystemLogic.grid.imp.Grid;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface EntityInstanceManager extends Serializable {

    EntityInstance  create(EntityDefinition entityDefinition, Grid grid);
     Map<String,List<EntityInstance>> getInstances();

    void Replace(Context context,EntityInstance oldEntityInstance, EntityInstance newEntityInstance, Grid grid,EntityInstanceManager entityInstanceManager);

    void killEntity(EntityInstance entityInstance,int id);

     List<EntityDefinition> getEntityDefinitions();
    void deepCopy(Map <String,List<EntityInstance>> newInstances,List<EntityDefinition> definitions,Grid grid );
}
