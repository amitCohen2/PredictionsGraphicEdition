package SystemLogic.worldInstance.api;

import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.definition.environment.impl.EnvVariableManagerImpl;
import SystemLogic.execution.context.Context;
import SystemLogic.execution.instance.environment.api.ActiveEnvironment;
import SystemLogic.grid.imp.Grid;
import SystemLogic.history.imp.HistoryArchive;
import SystemLogic.rule.api.Rule;
import SystemLogic.termination.imp.TerminationImpl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public interface World {
    Map<String, Rule> getRuleMap();
    Map<String, EntityDefinition> getEntityMap();
    HistoryArchive getHistoryArchive();
    Grid getGrid();
    TerminationImpl getTermination();
    ExecutorService getExecutorService();
}
