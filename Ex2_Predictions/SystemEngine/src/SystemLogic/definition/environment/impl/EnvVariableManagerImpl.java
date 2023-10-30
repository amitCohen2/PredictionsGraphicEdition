package SystemLogic.definition.environment.impl;

import SystemLogic.execution.instance.environment.api.ActiveEnvironment;
import SystemLogic.definition.environment.api.EnvVariablesManager;
import SystemLogic.definition.property.api.PropertyDefinition;
import SystemLogic.execution.instance.environment.impl.ActiveEnvironmentImpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EnvVariableManagerImpl implements EnvVariablesManager {

    private static final EnvVariableManagerImpl instance = new EnvVariableManagerImpl();
    private Map<String, PropertyDefinition> propNameToPropDefinition;

    private Map<String,Object> PropertyAndValues;
    private EnvVariableManagerImpl() {
        propNameToPropDefinition = new HashMap<>();
        PropertyAndValues = new HashMap<>();
    }

    public static EnvVariableManagerImpl getInstance() {
        return instance;
    }

    @Override
    public void addEnvironmentVariable(PropertyDefinition propertyDefinition) {
        propNameToPropDefinition.put(propertyDefinition.getName(), propertyDefinition);
    }

    public void addEnvironmentValues(String envName, Object value){
        PropertyAndValues.put(envName,value);
    }
    public Map<String,Object> getEnvironmentValues(){
        return this.PropertyAndValues;
    }
    @Override
    public ActiveEnvironment createActiveEnvironment() {
        return new ActiveEnvironmentImpl();
    }

    @Override
    public Collection<PropertyDefinition> getEnvVariables() {
        return propNameToPropDefinition.values();
    }
}