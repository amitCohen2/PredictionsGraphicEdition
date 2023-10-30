package SystemLogic.definition.environment.api;

import SystemLogic.definition.property.api.PropertyDefinition;
import SystemLogic.execution.instance.environment.api.ActiveEnvironment;

import java.io.Serializable;
import java.util.Collection;

public interface EnvVariablesManager extends Serializable {
    void addEnvironmentVariable(PropertyDefinition propertyDefinition);
    ActiveEnvironment createActiveEnvironment();
    Collection<PropertyDefinition> getEnvVariables();
}
