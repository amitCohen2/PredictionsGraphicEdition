package design.body.execution.xmlSimulationDetails.api;

import XmlLoader.schema.PRDEnvProperty;

import java.util.List;
import java.util.Map;

public interface XmlDetails {

    public Map<String, List<String>> getEntityPropertiesSimulationMap();

    public Map<String, PRDEnvProperty> getEnvironments();
}
