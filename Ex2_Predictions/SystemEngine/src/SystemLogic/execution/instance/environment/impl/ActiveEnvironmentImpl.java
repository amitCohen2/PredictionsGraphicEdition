package SystemLogic.execution.instance.environment.impl;

import SystemLogic.execution.instance.environment.api.ActiveEnvironment;
import SystemLogic.execution.instance.property.PropertyInstance;
import SystemLogic.execution.instance.property.PropertyInstanceImpl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ActiveEnvironmentImpl implements ActiveEnvironment, Serializable {

    private final Map<String, PropertyInstance> envVariables;

    public ActiveEnvironmentImpl() {
        envVariables = new HashMap<>();
    }
    @Override
    public void deepCopy(Map<String, PropertyInstance>envVariablesToCopy){
        envVariablesToCopy.forEach((s, propertyInstance) -> {
            PropertyInstance propertyInstance1 = new PropertyInstanceImpl(propertyInstance.getPropertyDefinition(),propertyInstance.getValue());
            envVariables.put(s,propertyInstance1);
        });
    }
    @Override
    public Map<String, PropertyInstance> getEnvVariables(){
        return envVariables;
    }

    @Override
    public PropertyInstance getProperty(String name) {
        if (!envVariables.containsKey(name)) {
            throw new IllegalArgumentException("Can't find env variable with name " + name);
        }
        return envVariables.get(name);
    }

    @Override
    public void addPropertyInstance(PropertyInstance propertyInstance) {
        envVariables.put(propertyInstance.getPropertyDefinition().getName(), propertyInstance);
    }
}
