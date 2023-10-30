package design.body.execution.xmlSimulationDetails.imp;

import XmlLoader.schema.PRDEnvProperty;
import design.body.execution.xmlSimulationDetails.api.XmlDetails;

import java.util.List;
import java.util.Map;

public class SimulationDetails implements XmlDetails {

    private Map<String, List<String>> entityPropertiesSimulationMap;
    private Map<String, PRDEnvProperty> environments;

    public void setEntityPropertiesSimulationMap(Map<String, List<String>> entityPropertiesSimulationMap) {
        this.entityPropertiesSimulationMap = entityPropertiesSimulationMap;
    }

    public void setEnvironments(Map<String, PRDEnvProperty> environments) {
        this.environments = environments;
    }

    public Map<String, List<String>> getEntityPropertiesSimulationMap() {
        return this.entityPropertiesSimulationMap;
    }

    public Map<String, PRDEnvProperty> getEnvironments() {
        return this.environments;
    }

}
