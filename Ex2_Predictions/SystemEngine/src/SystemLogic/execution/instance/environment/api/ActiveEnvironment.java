package SystemLogic.execution.instance.environment.api;

import SystemLogic.execution.instance.property.PropertyInstance;

import java.io.Serializable;
import java.util.Map;

public interface ActiveEnvironment {
    PropertyInstance getProperty(String name);
    void addPropertyInstance(PropertyInstance propertyInstance);
    void deepCopy(Map<String, PropertyInstance> envVariablesToCopy);
    public Map<String, PropertyInstance> getEnvVariables();
}
