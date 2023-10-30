package SystemLogic.execution.details.api;

import SystemLogic.execution.details.imp.EnvironmentValuesFromUser;

import java.util.Map;

public interface ExeDetails {
    public Map<String, Integer> getEntityPopulation();

    public Map<String, Object> getEnvironmentsValues();

    public Map<String, EnvironmentValuesFromUser> getEnvironmentsFromUserInputMap();

    public Map<String, String> getEnvironmentsToStringValues();
}
