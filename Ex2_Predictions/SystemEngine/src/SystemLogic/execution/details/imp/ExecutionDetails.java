package SystemLogic.execution.details.imp;

import SystemLogic.execution.details.api.ExeDetails;

import java.util.HashMap;
import java.util.Map;


public class ExecutionDetails implements ExeDetails {

    private final Map<String, Integer> entityPopulation;
    private final Map<String, String> environmentsToStringValues;
    private final Map<String, Object> environmentsValues;
    private final Map<String, EnvironmentValuesFromUser> environmentsFromUser;


    public ExecutionDetails(Map<String, Integer> entityPopulation, Map<String, String> environmentsToStringValues, Map<String, Object> environmentsValues) {
        this.entityPopulation = entityPopulation;
        this.environmentsToStringValues = environmentsToStringValues;
        this.environmentsValues = environmentsValues;
        this.environmentsFromUser = new HashMap<>();
        initEnvironmentsFromUser();
    }

    public Map<String, Integer> getEntityPopulation() {
        return  entityPopulation;
    }

    public Map<String, Object> getEnvironmentsValues() {
        return environmentsValues;
    }

    public Map<String, EnvironmentValuesFromUser> getEnvironmentsFromUserInputMap() {
        return environmentsFromUser;
    }

    public Map<String, String> getEnvironmentsToStringValues() {
        return environmentsToStringValues;
    }

    public void initEnvironmentsFromUser() {
        for (Map.Entry<String, Object> entry : environmentsValues.entrySet()) {
            String envName = entry.getKey();
            EnvironmentValuesFromUser envValues = new EnvironmentValuesFromUser();
            envValues.setName(envName);
            envValues.setValue(entry.getValue());
            envValues.setType((entry.getValue()).getClass().getName());
            envValues.setValueFromUser(true);
            // Store the user values in a map
            environmentsFromUser.put(envName, envValues);
        }
    }

}
