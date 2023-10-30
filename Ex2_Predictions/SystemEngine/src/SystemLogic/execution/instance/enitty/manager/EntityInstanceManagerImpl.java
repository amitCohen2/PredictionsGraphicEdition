package SystemLogic.execution.instance.enitty.manager;
import SystemLogic.Location.api.Location;
import SystemLogic.definition.entity.EntityDefinition;

import SystemLogic.definition.property.api.PropertyDefinition;
import SystemLogic.execution.context.Context;
import SystemLogic.execution.instance.enitty.EntityInstance;
import SystemLogic.execution.instance.enitty.EntityInstanceImpl;
import SystemLogic.execution.instance.property.PropertyInstance;
import SystemLogic.execution.instance.property.PropertyInstanceImpl;
import SystemLogic.grid.imp.Grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityInstanceManagerImpl implements EntityInstanceManager {

    private int count;
    private Map <String,List<EntityInstance>> instances;

    private List<EntityDefinition> EntityDefinitions;

    public int getInstancesSize() {
        return this.instances.size();
    }

    public EntityInstanceManagerImpl() {
        count = 0;
        instances = new HashMap<>();

        EntityDefinitions =  new ArrayList<>();
    }
@Override
    public void deepCopy(Map <String,List<EntityInstance>> newInstances,List<EntityDefinition> definitions,Grid grid ){
    this.instances = null;
    this.instances = new HashMap<>();
    Grid copyGrid = new Grid(grid.getNumOfRows(),grid.getNumOfColumns());
        count =newInstances.size();
        List entityList = new ArrayList();
        definitions.forEach(entityDefinition -> {
            EntityDefinitions.add(entityDefinition);
            newInstances.get(entityDefinition.getName()).forEach(entityInstance -> {
                EntityInstance copyEntity =create(entityInstance.getEntityDefinition(),copyGrid);
                //entityList.add(copyEntity);
            });
           // instances.put(entityDefinition.getName(),entityList);
        });
    }
    @Override
    public List<EntityDefinition> getEntityDefinitions(){
        return EntityDefinitions;
    }
    @Override
    public void Replace(Context context,EntityInstance oldEntityInstance, EntityInstance newEntityInstance, Grid grid,EntityInstanceManager entityInstanceManager) {
        int index = entityInstanceManager.getInstances().get(newEntityInstance.getEntityDefinition().getName()).size() ;

        newEntityInstance.getEntityDefinition().getProps().forEach(prop -> {
            if (oldEntityInstance.getPropertyMap().containsKey(prop.getName())) {
                PropertyInstance newPropertyInstance = new PropertyInstanceImpl(prop, oldEntityInstance.getPropertyByName(prop.getName()).getValue());
                Integer ticksNotChange = oldEntityInstance.getTicksNotChangeMap().get(oldEntityInstance);
                newEntityInstance.getTicksNotChangeMap().put(newPropertyInstance,ticksNotChange);
                newEntityInstance.addPropertyInstance(newPropertyInstance);
            } else {
                Object value = prop.generateValue();
                PropertyInstance newPropertyInstance = new PropertyInstanceImpl(prop, value);
                newEntityInstance.getTicksNotChangeMap().put(newPropertyInstance,0);
                newEntityInstance.addPropertyInstance(newPropertyInstance);
            }
        });

        grid.getEntityMatrix()[newEntityInstance.getLocation().getLocation().getRow()][newEntityInstance.getLocation().getLocation().getColum()] =newEntityInstance;
        if (index != -1) {

            if(index >= entityInstanceManager.getInstances().get(newEntityInstance.getEntityDefinition().getName()).size()){
                String debug = "debug";
            }
            context.removeEntity(oldEntityInstance,grid,entityInstanceManager);

            entityInstanceManager.getInstances().get(newEntityInstance.getEntityDefinition().getName()).add(index, newEntityInstance);

        }


    }

    @Override
    public EntityInstance create(EntityDefinition entityDefinition, Grid grid) {

        Location location = grid.getAvailableLocation();
        if(!EntityDefinitions.contains(entityDefinition)){
            EntityDefinitions.add(entityDefinition);
        }

        count++;
        EntityInstance newEntityInstance = new EntityInstanceImpl(entityDefinition, count,location);
        if(instances.containsKey(entityDefinition.getName())) {
            instances.get(entityDefinition.getName()).add(newEntityInstance);
        }
        else{
            List<EntityInstance> entityInstanceList = new ArrayList<>();
            instances.put(entityDefinition.getName(),entityInstanceList);
            instances.get(entityDefinition.getName()).add(newEntityInstance);
        }

        for (PropertyDefinition propertyDefinition : entityDefinition.getProps()) {
            Object value = propertyDefinition.generateValue();
            PropertyInstance newPropertyInstance = new PropertyInstanceImpl(propertyDefinition, value);

            newEntityInstance.addPropertyInstance(newPropertyInstance);
        }
        grid.addEntity(newEntityInstance);
        return newEntityInstance;
    }



    @Override
    public Map <String,List<EntityInstance>> getInstances() {
        return instances;
    }

    @Override
    public void killEntity(EntityInstance entityInstance ,int id) {
        int idx;
        //ArrayList<EntityInstanceImpl> entitis = new ArrayList<>();
       List<EntityInstance> entitis =instances.get(entityInstance.getEntityDefinition().getName());
        for(int i = 0; i <entitis.size(); i++) {
            if(entitis.get(i).getId() ==id) {
                idx = i;
                instances.get(entityInstance.getEntityDefinition().getName()).remove(idx);
                count--;
                break;
            }
        }


    }

}

