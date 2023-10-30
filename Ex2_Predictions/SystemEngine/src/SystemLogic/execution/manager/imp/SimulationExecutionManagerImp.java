package SystemLogic.execution.manager.imp;

import SystemLogic.execution.details.imp.ExecutionDetails;
import SystemLogic.execution.manager.api.ExeManagerImp;
import SystemLogic.execution.results.imp.ExecutionResult;
import SystemLogic.execution.runner.imp.SimulationRunner;
import SystemLogic.history.imp.HistoryArchive;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.HashMap;
import java.util.Map;

public class SimulationExecutionManagerImp implements ExeManagerImp {

    private final Map<Integer, ExecutionDetails> executions;
    private final Map<Integer, SimulationRunner> simulationsRunners;
    private final Map<Integer, ExecutionResult> executionsResults;
    private final HistoryArchive historyArchive;
    private final SimpleIntegerProperty simulationCounter;

    public SimulationExecutionManagerImp() {
        this.executions = new HashMap<>();
        this.executionsResults = new HashMap<>();
        this.simulationsRunners = new HashMap<>();
        this.historyArchive = new HistoryArchive();
        this.simulationCounter = new SimpleIntegerProperty(1);
    }

    @Override
    public Map<Integer, ExecutionResult> getExecutionsResults() {
        return executionsResults;
    }
    @Override
    public Map<Integer, SimulationRunner> getSimulationsRunners() {
        return simulationsRunners;
    }
    @Override
    public Map<Integer, ExecutionDetails> getExecutions() {
        return executions;
    }
    @Override
    public HistoryArchive getHistoryArchive() {
        return historyArchive;
    }
    @Override
    public int getSimulationCounter() {
        return simulationCounter.get();
    }
    @Override
    public void updateSimulationCounter() {
        simulationCounter.set(simulationCounter.get() + 1);
    }
}
