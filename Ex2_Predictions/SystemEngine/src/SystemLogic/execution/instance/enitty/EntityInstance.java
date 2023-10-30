package SystemLogic.execution.instance.enitty;

import SystemLogic.Location.api.Location;
import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.execution.instance.property.PropertyInstance;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface EntityInstance extends Serializable {
    int getId();
    void incrementPropertyTick(PropertyInstance propertyInstance);
    Map<String, PropertyInstance> getPropertyMap();
    PropertyInstance getPropertyByName(String name);
    void addPropertyInstance(PropertyInstance propertyInstance);
    Map<String, List<Integer>> getConsistency();
    EntityDefinition getEntityDefinition();
    EntityInstance getSeconderyEntityInstance();

    void setSecondaryEntityInstance(EntityInstance SecoendEntityInstance);
   Location getLocation();
   void setLocation(Location location);
    public void setProperties(Map<String, PropertyInstance> properties);
    void resetPropertyTick(PropertyInstance propertyInstance);
    public Map<PropertyInstance , Integer> getTicksNotChangeMap();
}
