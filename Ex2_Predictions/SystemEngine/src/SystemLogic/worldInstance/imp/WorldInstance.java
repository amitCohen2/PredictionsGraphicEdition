package SystemLogic.worldInstance.imp;

import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.grid.imp.Grid;
import SystemLogic.history.imp.HistoryArchive;
import SystemLogic.rule.api.Rule;
import SystemLogic.termination.imp.TerminationImpl;
import SystemLogic.worldInstance.api.World;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorldInstance implements World {
    private final HistoryArchive historyArchive;
    private final Map<String, Rule> ruleMap;
    private final Map<String, EntityDefinition> entityMap;
    int threadsNumber;
    private final TerminationImpl termination;
    private Grid grid;
    private ExecutorService threadExecutor;

    public WorldInstance(HistoryArchive historyArchive) {
        this.historyArchive = historyArchive;
        this.ruleMap = new HashMap<>();
        this.entityMap = new HashMap<>();
        this.termination = new TerminationImpl(null, null, false);
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }
    public TerminationImpl getTermination() {
        return termination;
    }

    @Override
    public ExecutorService getExecutorService() {
        return threadExecutor;
    }

    @Override
    public Map<String, Rule> getRuleMap() {
        return ruleMap;
    }
    @Override
    public Map<String, EntityDefinition> getEntityMap() {
        return entityMap;
    }
    @Override
    public HistoryArchive getHistoryArchive() {
        return historyArchive;
    }

    @Override
    public Grid getGrid() {
        return grid;
    }

    public void setThreadsNumber(int threadsNumber) {
        this.threadsNumber = threadsNumber;
        setThreadPool();
    }

    private void setThreadPool() {
        threadExecutor = Executors.newFixedThreadPool(threadsNumber);
    }


}
