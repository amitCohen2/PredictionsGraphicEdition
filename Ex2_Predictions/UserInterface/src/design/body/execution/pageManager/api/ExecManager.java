package design.body.execution.pageManager.api;

import SystemLogic.execution.manager.imp.SimulationExecutionManagerImp;
import XmlLoader.schema.PRDWorld;
import design.body.execution.xmlSimulationDetails.imp.SimulationDetails;

public interface ExecManager {
    SimulationExecutionManagerImp getSimulationExecutionManagerImp();
    SimulationDetails getSimulationDetails();
    int getPopulationNumber(PRDWorld world);

}
