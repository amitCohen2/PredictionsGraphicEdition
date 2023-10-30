package SystemLogic.execution.context;

import SystemLogic.execution.details.imp.EnvironmentValuesFromUser;
import SystemLogic.execution.instance.environment.api.ActiveEnvironment;
import SystemLogic.execution.instance.environment.impl.ActiveEnvironmentImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FullContext {
    private Map<String, List<Context>> contextMap;
    private ActiveEnvironment activeEnvironment;

    public FullContext() {
        contextMap = new HashMap<>();
        activeEnvironment = new ActiveEnvironmentImpl();

    }

    public FullContext(Map<String, List<Context>> contextMap , ActiveEnvironment activeEnvironment) {
        this.contextMap = contextMap;
        this.activeEnvironment = activeEnvironment;
    }

    public Map<String, List<Context>> getContextMap() {
        return contextMap;
    }

    public void setContextMap(Map<String, List<Context>> contextMap) {
        this.contextMap = contextMap;
    }

    public ActiveEnvironment getActiveEnvironment() {
        return activeEnvironment;
    }

    public void setActiveEnvironment(ActiveEnvironment activeEnvironment) {
        this.activeEnvironment = activeEnvironment;
    }
}