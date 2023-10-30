package SystemLogic.execution.instance.enitty;

import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.execution.instance.property.PropertyInstance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import SystemLogic.Location.api.Location;
public class EntityInstanceImpl implements EntityInstance , Serializable {

    private final EntityDefinition entityDefinition;
    private final int id;
    private Map<String, PropertyInstance> properties;
    private Map<String, List<Integer>> consistencyList;
    private Map<PropertyInstance , Integer>  ticksNotChange;
    private EntityInstance SecoendEntityInstance;

    private Location location;

@Override
    public void setLocation(Location location){
        this.location = location;
    }
    public EntityInstanceImpl(EntityDefinition entityDefinition, int id , Location location) {
        this.entityDefinition = entityDefinition;
        this.id = id;
        properties = new HashMap<>();
        this.location = location;
        this.ticksNotChange = new HashMap<>();
        this.consistencyList = new HashMap<>();
        this.SecoendEntityInstance = null;
        entityDefinition.getProps().forEach(property -> {
            List<Integer> currList = new ArrayList<>();
            consistencyList.put(property.getName(), currList);
        });
    }

    @Override
    public void setProperties(Map<String, PropertyInstance> properties){
        this.properties = properties;
    }
@Override
public EntityInstance getSeconderyEntityInstance(){
    return this.SecoendEntityInstance;
}
    @Override
    public Map<PropertyInstance , Integer> getTicksNotChangeMap() {
        return ticksNotChange;
    }
    @Override
    public Map<String, PropertyInstance> getPropertyMap() {
        return properties;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public PropertyInstance getPropertyByName(String name) {
        if (!properties.containsKey(name)) {
            throw new IllegalArgumentException("for entity of type " + entityDefinition.getName() + " has no property named " + name);
        }

        return properties.get(name);
    }
 @Override
 public void setSecondaryEntityInstance(EntityInstance SecoendEntityInstance){
    this.SecoendEntityInstance =SecoendEntityInstance;
 }
    @Override
    public void addPropertyInstance(PropertyInstance propertyInstance) {
        properties.put(propertyInstance.getPropertyDefinition().getName(), propertyInstance);
        ticksNotChange.put(propertyInstance,0);
    }

    @Override
    public Map<String, List<Integer>> getConsistency() {
        return consistencyList;
    }

    @Override
    public Location getLocation(){ return this.location;}
    @Override
    public EntityDefinition getEntityDefinition() {
        return entityDefinition;
    }

    @Override
    public void incrementPropertyTick(PropertyInstance propertyInstance) {
        Integer tickCount = ticksNotChange.get(propertyInstance);
        if (tickCount != null) {
            ticksNotChange.put(propertyInstance, tickCount + 1);
        }
    }

    @Override
    public void resetPropertyTick(PropertyInstance propertyInstance) {
        consistencyList.get(propertyInstance.getPropertyDefinition().getName()).add(ticksNotChange.get(propertyInstance));
        ticksNotChange.put(propertyInstance, 0);
    }
}
