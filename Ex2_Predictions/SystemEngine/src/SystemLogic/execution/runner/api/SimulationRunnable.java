package SystemLogic.execution.runner.api;

import SystemLogic.Expression.exceptions.DivideByZeroException;
import SystemLogic.Expression.exceptions.FunctionNotOccurException;
import SystemLogic.Expression.exceptions.NoFunctionExpressionsException;
import SystemLogic.Expression.exceptions.TooManyExpressionsException;
import SystemLogic.Timer.imp.TimeMeasurement;
import SystemLogic.action.impl.Exceptions.ValueOutOfRangeException;
import SystemLogic.definition.environment.impl.EnvVariableManagerImpl;
import SystemLogic.execution.context.Context;
import SystemLogic.execution.instance.environment.api.ActiveEnvironment;
import SystemLogic.history.imp.SimulationHistory;
import SystemLogic.worldInstance.imp.WorldInstance;

import java.util.List;
import java.util.Map;

public interface SimulationRunnable  {
    Map<String, List<Context>> getContexts();
    SimulationHistory getSimulationHistory();
    int getSimulationId();
    ActiveEnvironment getActiveEnvironment();
    EnvVariableManagerImpl getenvVariablesManager();
    void setSimulationHistory(SimulationHistory simulationHistory);
    void setWorldInstance(WorldInstance worldInstance);
    TimeMeasurement getTimer();
    int getTicks();
    String getStatus();
    void runTickAfter(boolean isUserPressTickAfter) ;
}
