package XmlRunner;

//import Menu.Menu;
import SystemLogic.execution.context.Context;
import SystemLogic.execution.context.ContextImpl;
import SystemLogic.execution.instance.enitty.EntityInstance;
import SystemLogic.execution.instance.enitty.manager.EntityInstanceManager;
import SystemLogic.execution.instance.enitty.manager.EntityInstanceManagerImpl;
import SystemLogic.execution.runner.imp.SimulationRunner;
import SystemLogic.grid.imp.Grid;
import SystemLogic.history.imp.HistoryArchive;
import SystemLogic.termination.imp.TerminationImpl;
import SystemLogic.worldInstance.api.World;
import SystemLogic.worldInstance.imp.WorldInstance;
import XmlLoader.schema.*;
import SystemLogic.execution.details.imp.EnvironmentValuesFromUser;
import convertor.imp.GeneratedXmlClassConverter;
import design.body.execution.pageManager.imp.SimulationExecutionPageManager;
import design.header.QueueManagement;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;

import java.util.*;
import java.util.stream.IntStream;

public class XmlRunner {
    private WorldInstance worldInstance;
    private SimulationRunner simulationRunner;
    PRDWorld world;


    private boolean isFirstRun;

    public void setIsFirstRun(boolean isFirstRun){
        this.isFirstRun = isFirstRun;
    }


    public void initRunSimulation(int simulationsCounter, HistoryArchive historyArchive, PRDWorld world,
                                  Map<String, Integer> entityPopulationMap, Map<Integer, SimulationRunner> simulationsRunners,Boolean saveHistory) {
        if (worldInstance == null) {
            worldInstance = new WorldInstance(historyArchive);
            this.world = world;
            InitWorldInstance(entityPopulationMap);
        }
        this.simulationRunner = new SimulationRunner(entityPopulationMap, simulationsCounter, saveHistory);
        simulationsRunners.put(simulationsCounter, simulationRunner);
    }

    public void InitWorldInstance(Map<String, Integer> entityPopulationMap)
    {
        // init worldInstance
        isFirstRun = true;
        worldInstance.setGrid(GeneratedXmlClassConverter.initGrid(world.getPRDGrid()));
        GeneratedXmlClassConverter.initTermination(world.getPRDTermination(), worldInstance.getTermination());
        GeneratedXmlClassConverter.PrdEntity2EntityMap(world.getPRDEntities().getPRDEntity(), worldInstance.getEntityMap(), entityPopulationMap);
        GeneratedXmlClassConverter.PrdRule2RuleMap(world.getPRDRules().getPRDRule(), worldInstance.getRuleMap(), worldInstance.getEntityMap() , worldInstance.getGrid());
        worldInstance.setThreadsNumber(GeneratedXmlClassConverter.getThreadsNumber(world));
    }

    private void initContexts() {
        //Grid currGrid =  new Grid(worldInstance.getGrid().getNumOfRows(),worldInstance.getGrid().getNumOfColumns());
        //worldInstance.setGrid(currGrid);
       // simulationRunner.setGrid(currGrid);
        //worldInstance.setGrid(new Grid(worldInstance.getGrid().getNumOfRows(),worldInstance.getGrid().getNumOfColumns()));
        Grid currGrid = worldInstance.getGrid();

        currGrid.clearAll();


        EntityInstanceManager entityInstanceManager = new EntityInstanceManagerImpl();
        worldInstance.getEntityMap().forEach((entityName, entityDefinition) -> {
            //init the instanceManager and the list of the current entity contexts
            List<Context> entityContexts = new ArrayList<>();
            // make the contexts lists
            IntStream.range(0, entityDefinition.getPopulation()).forEach(i -> {
                EntityInstance currEntityInstance = entityInstanceManager.create(entityDefinition, currGrid);
                Context currentContext = new ContextImpl(currEntityInstance, simulationRunner.getActiveEnvironment());
                entityContexts.add(currentContext);
            });
            simulationRunner.getContexts().put(entityName, entityContexts);

        });
        simulationRunner.setEntityInstanceManager(entityInstanceManager);
    }

    public void Run(SimulationExecutionPageManager simulationExecutionManager, int simulationCounter, QueueManagement queueManagement)
    {
        Map<String, EnvironmentValuesFromUser> userEnvInput = simulationExecutionManager.getSimulationExecutionManagerImp().getExecutions().get(simulationCounter).getEnvironmentsFromUserInputMap();
        GeneratedXmlClassConverter.PrdEnvProperty2EvironmentManager(world.getPRDEnvironment().getPRDEnvProperty(), simulationRunner.getenvVariablesManager(), simulationRunner.getActiveEnvironment(), userEnvInput);
        initContexts();
        simulationRunner.setTermination(worldInstance.getTermination());
        simulationRunner.setWorldInstance(worldInstance);
        Platform.runLater(() -> queueManagement.getSimulationsWaiting().set(queueManagement.getSimulationsWaiting().get() + 1));
        //TODO: change the runner to not initialize all the time the worldInstance and to change the logic in the controllers
        //simulationRunner.run();
        worldInstance.getExecutorService().execute(()-> {
                Platform.runLater(() -> {
                    queueManagement.getSimulationsWaiting().set(queueManagement.getSimulationsWaiting().get() - 1);
                    queueManagement.getSimulationsInProgress().set(queueManagement.getSimulationsInProgress().get() + 1);
                });
                simulationRunner.run();
        });

    }

    public PRDWorld getWorld() {
        return this.world;
    }

    public void setWorld(PRDWorld world) {
        this.world = world;
    }

    public WorldInstance getWorldInstance() {
        return worldInstance;
    }
}
