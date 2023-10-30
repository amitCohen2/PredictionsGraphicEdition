package SystemLogic.execution.results.api;

import SystemLogic.history.imp.SimulationHistory;

public interface ExeResult {
    public SimulationHistory getSimulationHistory();
    public void showLineChart();

}
