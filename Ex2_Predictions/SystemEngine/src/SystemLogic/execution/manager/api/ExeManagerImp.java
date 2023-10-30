package SystemLogic.execution.manager.api;

import SystemLogic.execution.details.imp.ExecutionDetails;
import SystemLogic.execution.results.imp.ExecutionResult;
import SystemLogic.execution.runner.imp.SimulationRunner;
import SystemLogic.history.imp.HistoryArchive;

import java.util.Map;

public interface ExeManagerImp {

    Map<Integer, ExecutionResult> getExecutionsResults();
    Map<Integer, ExecutionDetails> getExecutions();
    HistoryArchive getHistoryArchive();
    int getSimulationCounter();
    void updateSimulationCounter();
    Map<Integer, SimulationRunner> getSimulationsRunners();


}
