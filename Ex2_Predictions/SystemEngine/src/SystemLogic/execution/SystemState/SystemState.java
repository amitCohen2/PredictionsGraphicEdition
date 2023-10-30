package SystemLogic.execution.SystemState;

import SystemLogic.execution.context.Context;
import SystemLogic.execution.instance.enitty.manager.EntityInstanceManager;
import SystemLogic.grid.imp.Grid;

import java.util.List;
import java.util.Map;


public class SystemState {
    private EntityInstanceManager entityInstanceManager;
    private Grid grid;
    private int ticks;
    private Map<String, List<Context>> contexts;

    // Constructor
    public SystemState(EntityInstanceManager entityInstanceManager, Grid grid, int ticks, Map<String, List<Context>> contexts) {
        this.entityInstanceManager = entityInstanceManager;
        this.grid = grid;
        this.ticks = ticks;
        this.contexts = contexts;
    }

    // Getters
    public EntityInstanceManager getEntityInstanceManager() {
        return entityInstanceManager;
    }

    public Grid getGrid() {
        return grid;
    }

    public int getTicks() {
        return ticks;
    }

    public Map<String, List<Context>> getContexts() {
        return contexts;
    }

    // Setters
    public void setEntityInstanceManager(EntityInstanceManager entityInstanceManager) {
        this.entityInstanceManager = entityInstanceManager;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public void setContexts(Map<String, List<Context>> contexts) {
        this.contexts = contexts;
    }
}