package SystemLogic.history.api;

import SystemLogic.execution.context.Context;
import SystemLogic.execution.instance.enitty.manager.EntityInstanceManager;
import SystemLogic.history.imp.SimulationHistory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface Archive extends Serializable {
    public void addSimulation(Map<String, List<Context>> entities, EntityInstanceManager entityInstanceManager);
    public StringBuilder getSimulationsListByDates();
    public SimulationHistory getSimulationHistory(int number);

}
