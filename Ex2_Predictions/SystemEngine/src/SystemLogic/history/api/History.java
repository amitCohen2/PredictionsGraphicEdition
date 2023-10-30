package SystemLogic.history.api;

import SystemLogic.execution.instance.enitty.manager.EntityInstanceManager;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface History extends Serializable {
   // public StringBuilder getBeforeAfterEntitiesAmount();
    public Map<String, List<Integer>> getEntityPopulationByTicks();
    public StringBuilder getPropertiesList(int userEntityChoice);
    public StringBuilder getPropertyHistory(int userEntityChoice, int userPropertyChoice);
    StringBuilder getBeforeAfterEntitiesAmount(EntityInstanceManager entityInstanceManager);

     void setCurrentTick(int currentTick);
     int getCurrentTick();
}
