package SystemLogic.execution.runner.imp;

import SystemLogic.Expression.exceptions.DivideByZeroException;
import SystemLogic.Expression.exceptions.FunctionNotOccurException;
import SystemLogic.Expression.exceptions.NoFunctionExpressionsException;
import SystemLogic.Expression.exceptions.TooManyExpressionsException;
import SystemLogic.Timer.imp.TimeMeasurement;
import SystemLogic.action.impl.Exceptions.ValueOutOfRangeException;
import SystemLogic.definition.environment.impl.EnvVariableManagerImpl;
import SystemLogic.definition.property.api.PropertyDefinition;
import SystemLogic.definition.property.impl.BooleanPropertyDefinition;
import SystemLogic.definition.property.impl.FloatPropertyDefinition;
import SystemLogic.definition.property.impl.StringPropertyDefinition;
import SystemLogic.definition.value.generator.api.ValueGeneratorFactory;
import SystemLogic.execution.SystemState.SystemState;
import SystemLogic.execution.context.Context;
import SystemLogic.execution.context.ContextImpl;
import SystemLogic.execution.instance.enitty.EntityInstance;
import SystemLogic.execution.instance.enitty.EntityInstanceImpl;
import SystemLogic.execution.instance.enitty.manager.EntityInstanceManager;
import SystemLogic.execution.instance.enitty.manager.EntityInstanceManagerImpl;
import SystemLogic.execution.instance.environment.api.ActiveEnvironment;
import SystemLogic.execution.instance.property.PropertyInstance;
import SystemLogic.execution.instance.property.PropertyInstanceImpl;
import SystemLogic.execution.runner.api.SimulationCompletionListener;
import SystemLogic.execution.runner.api.SimulationRunnable;
import SystemLogic.execution.status.ExecutionStatus;
import SystemLogic.grid.imp.Grid;
import SystemLogic.history.imp.SimulationHistory;
import SystemLogic.termination.imp.TerminationImpl;
import SystemLogic.worldInstance.imp.WorldInstance;
import javafx.application.Platform;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimulationRunner implements Runnable, SimulationRunnable {
    private final EnvVariableManagerImpl envVariablesManager;
    private  Map<String, List<Context>> contexts;

    private final Map<Integer,SystemState>  systemStateByTicks;
    private final ActiveEnvironment activeEnvironment;
    private final Map<String, Integer> entityPopulationMap;
    private SimulationHistory simulationHistory;
    private final int simulationId;
    private WorldInstance worldInstance;
    private final TimeMeasurement timer;
    private final Random random;
    private final int[] ticksCounter;
    private final int[] simulationBackCounter;
    private ExecutionStatus executionStatus;
    private Map<String, List<Integer>> entityPopulationByTicks;
    EntityInstanceManager entityInstanceManager;
    SystemState systemState;
    Grid grid;
    boolean isPaused;
    private boolean isStopped;
    private SimulationCompletionListener completionListener;

    private Boolean SavePrevSimulation;

    private boolean isTickBeforePressed;
    private final Lock lock = new ReentrantLock();
    private final Condition pauseCondition = lock.newCondition();

    private TerminationImpl termination;

    private boolean isFaild;

    private String reasonForFailure;



    public SimulationRunner(Map<String, Integer> entityPopulationMap, int simulationId ,Boolean SavePrevSimulation) {
        this.simulationId = simulationId;
        this.contexts = new HashMap<>();
        this.entityPopulationMap = entityPopulationMap;
        this.envVariablesManager = EnvVariableManagerImpl.getInstance();
        this.activeEnvironment = envVariablesManager.createActiveEnvironment();
        this.ticksCounter = new int[]{0};
        this.timer = new TimeMeasurement();
        this.random = new Random();
        this.executionStatus = ExecutionStatus.IN_PROGRESS;
        systemStateByTicks= new HashMap<>();
        this.entityInstanceManager= new EntityInstanceManagerImpl();
        isTickBeforePressed = false;
        this.isPaused = false;
        this.simulationBackCounter =new int[]{0};;
        this.SavePrevSimulation =SavePrevSimulation;
        this.isFaild = false;
        this.reasonForFailure ="";

    }
    public void setIsFaild(boolean flag){
        this.isFaild=flag;
    }
    public boolean getIsFaild(){
        return isFaild;
    }

    public void setReasonForFailure(String reason){
        this.reasonForFailure=reason;
    }
    public String getReasonForFailure(){
        return reasonForFailure;
    }
    public void setEntityInstanceManager( EntityInstanceManager newEntityInstanceManager){
        this.entityInstanceManager = newEntityInstanceManager;
    }

    public void setTermination(TerminationImpl termination) {
        this.termination = termination;
    }
    public TerminationImpl getTermination(){
        return  termination;
    }

    public void setPaused(boolean paused) {
        lock.lock();
        try {
            isPaused = paused;
            if (!isPaused) {
                pauseCondition.signal();
                executionStatus = ExecutionStatus.IN_PROGRESS;
            } else {
                executionStatus = ExecutionStatus.PAUSED;
            }
        } finally {
            lock.unlock();
        }
    }

    public void SetSimulationBefore(Boolean SavePrevSimulation){
        this.SavePrevSimulation = SavePrevSimulation;
    }

    public void setStopped(boolean isStopped){

        lock.lock();
        try {
            executionStatus = ExecutionStatus.DONE;
            isPaused = false;
            if (!isPaused) {
                isStopped = true;
                pauseCondition.signal();
            }
        } finally {
            lock.unlock();
        }

    }

    private void SystemStateInit(EntityInstanceManager entityInstanceManager, Grid grid , int ticks , Map<String, List<Context>> contexts){
        this.systemState = new SystemState(entityInstanceManager,grid,ticks,contexts);
    }
    public void setCompletionListener(SimulationCompletionListener listener) {
        this.completionListener = listener;
    }


    @Override
    public void run()  {
        worldInstance.getHistoryArchive().addSimulation(contexts, entityInstanceManager);
        entityPopulationByTicks = initEntityPopulationByTicks();
        grid =worldInstance.getGrid();
        SystemStateInit(entityInstanceManager,grid,0,contexts);
        Integer terminationInTicks = termination.getNumOfTicks();//termination.getNumOfTicks();
        Integer terminationInSec = termination.getNumOfSec();//termination.getNumOfSec();//


        timer.start();
        while ((terminationInSec != null &&timer.checkElapsedTime() < terminationInSec) ||(terminationInTicks!=null && ticksCounter[0] < terminationInTicks) ||( ! executionStatus.equals(ExecutionStatus.DONE))) {
            lock.lock();
            try {
                while (isPaused) {
                    try {
                        // Wait until isPaused becomes false
                        pauseCondition.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            } finally {
                lock.unlock();
            }

            if(!isStopped && !isFaild) {
                runTickAfter(false);

            }
        }
        timer.stop();


        simulationHistory = worldInstance.getHistoryArchive().getSimulationHistory(simulationId);
        simulationHistory.updateNumberOfEndTicks(ticksCounter[0]);
        simulationHistory.updateSimulationHistory(contexts, entityPopulationByTicks);
        simulationHistory.setCurrentTick(ticksCounter[0]);
        if(ticksCounter[0]>20){simulationBackCounter[0] =20;}
        else{
            simulationBackCounter[0] =ticksCounter[0];
        }

        executionStatus = ExecutionStatus.DONE;

/*        System.out.print("Simulation number: " + simulationId + " was ended Successfully! End by: ");
        if(terminationInTicks != null &&ticksCounter[0] == terminationInTicks){
            System.out.println(ticksCounter[0] + " ticks.\n");
        } else {
            System.out.println(getTimer().getTotalTime() + " Seconds.\n");
        }*/


        // Notify the completion listener on the JavaFX thread
        Platform.runLater(() -> {
            if (completionListener != null) {
                completionListener.onSimulationCompleted(simulationId);
            }
        });
    }

    private Map<String, List<Integer>> initEntityPopulationByTicks() {
        Map<String, List<Integer>> entityPopulationByTicks = new HashMap<>();
        contexts.forEach((entity, entityContexts) -> {
            List<Integer> population = new ArrayList<>();
            population.add(entityInstanceManager.getInstances().get(entity).size());
            entityPopulationByTicks.put(entity, population);
        });
        return entityPopulationByTicks;
    }

    private void setGrid(Grid grid){
        this.grid = grid;
    }
    private void updateEntityPopulationByTick(Map<String, List<Integer>> entityPopulationByTicks,Map<String, Integer> CurrentPopulationStatus ) {
        contexts.forEach((entity, entityContexts) -> {
            List<Integer> population = entityPopulationByTicks.get(entity);
            population.add(CurrentPopulationStatus.get(entity));
            entityPopulationByTicks.put(entity, population);
        });
    }

    public boolean isAlive(List<EntityInstance> entityInstanceArrayList, int id){
        for(EntityInstance entityInstance : entityInstanceArrayList){
            if(entityInstance.getId() == id){
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<String, List<Context>> getContexts() {
        return contexts;
    }

    @Override
    public SimulationHistory getSimulationHistory() {
        return simulationHistory;
    }

    @Override
    public int getSimulationId() {
        return simulationId;
    }

    @Override
    public ActiveEnvironment getActiveEnvironment() {
        return activeEnvironment;
    }

    @Override
    public EnvVariableManagerImpl getenvVariablesManager() {
        return envVariablesManager;
    }

    @Override
    public void setSimulationHistory(SimulationHistory simulationHistory) {
        this.simulationHistory = simulationHistory;
    }

    @Override
    public void setWorldInstance(WorldInstance worldInstance) {
        this.worldInstance = worldInstance;
    }

    @Override
    public TimeMeasurement getTimer() {
        return timer;
    }

    @Override
    public int getTicks() {
        return ticksCounter[0];
    }

    public String getStatus() {
        if (executionStatus.equals(ExecutionStatus.DONE)) {
            return "DONE!";
        } else if (executionStatus.equals(ExecutionStatus.IN_PROGRESS)){
            return "In Progress...";
        } else {
            return "Paused";
        }
    }

    public ExecutionStatus getStatusType() {
       return executionStatus;
    }

    @Override
    public void runTickAfter(boolean isUserPressTickAfter)   {
        if (simulationHistory != null && simulationHistory.getCurrentTick() < simulationHistory.getSizeOfEntityPopulationByTicks()-1){
            systemState =  systemStateByTicks.get(simulationHistory.getCurrentTick());
            contexts = systemState.getContexts();
            entityInstanceManager = systemState.getEntityInstanceManager();
            grid = systemState.getGrid();
            //systemStateByTicks.remove(currentNumOfTicks);
            ticksCounter[0]++;
            simulationBackCounter[0]++;
            simulationHistory.setCurrentTick( ticksCounter[0]);
        }
        else {
            Map<String, Integer> currentPopulationStatus = new HashMap<>();
            timer.start();
            if(SavePrevSimulation) {
            saveStateByTicks(ticksCounter[0], contexts, systemState);
            }


            executionStatus = ExecutionStatus.IN_PROGRESS;

            //updateEntityPopulationByTick(entityPopulationByTicks,currentPopulationStatus);
            worldInstance.getRuleMap().forEach((ruleName, rule) -> {
                if (rule.isActive(ticksCounter[0], random.nextDouble())) {
                    contexts.forEach((entityName, contextsList) -> {
                        contextsList.forEach(context -> {
                            if (isAlive(entityInstanceManager.getInstances().get(entityName), context.getPrimaryEntityInstance().getId())) {
                                context.getPrimaryEntityInstance().getTicksNotChangeMap().forEach((propertyInstance, integer) ->
                                        context.getPrimaryEntityInstance().incrementPropertyTick(propertyInstance));
                            }
                            rule.getActionsToPerform().forEach(action -> {
                                try {
                                    if (isAlive(entityInstanceManager.getInstances().get(context.getPrimaryEntityInstance().getEntityDefinition().getName()), context.getPrimaryEntityInstance().getId())) {
                                        action.invoke(context, entityInstanceManager, grid);
                                        context.getPrimaryEntityInstance().getLocation().updateLocation(grid);
                                        setCurrentPopulationStatus(currentPopulationStatus, context);

                                    }

                               } catch (TooManyExpressionsException | FunctionNotOccurException |
                                         IllegalArgumentException | NoFunctionExpressionsException |
                                         DivideByZeroException |
                                         ValueOutOfRangeException e) {
                                    //System.out.println(e.getMessage());
                                    if(!e.getClass().equals(ValueOutOfRangeException.class)){
                                        executionStatus = ExecutionStatus.DONE;
                                        isFaild = true;
                                        reasonForFailure =e.getMessage();

                                    }


                                }
                            });
                        });
                    });
                }
            });


            updateEntityPopulationByTick(entityPopulationByTicks, currentPopulationStatus);
            ticksCounter[0]++;
            if(  simulationHistory != null){
                simulationHistory.setCurrentTick( ticksCounter[0]);
            }

            isTickBeforePressed = false;
            if (isUserPressTickAfter) {
                executionStatus = ExecutionStatus.DONE;
            }
            timer.stop();
        }
        try {
            Thread.sleep(100); // Sleep for 100 milliseconds
        } catch (InterruptedException e) {
            // Handle the InterruptedException, if needed
        }
        if(isFaild){
            timer.stop();

        }
    }

    public void runTickBefore(boolean isUserPressTickBefore){
        if(systemStateByTicks.size() > 0 &&  simulationBackCounter[0]>0){
            int currentNumOfTicks = simulationBackCounter[0];//systemStateByTicks.size();
            Integer lastKey = null;
            SystemState lastSystemState = null;

            for (Map.Entry<Integer, SystemState> entry : systemStateByTicks.entrySet()) {
                Integer currentKey = entry.getKey();
                if (lastKey == null || currentKey > lastKey) {
                    lastKey = currentKey;
                    lastSystemState = entry.getValue();
                }
            }
            systemState =  lastSystemState;
            contexts = systemState.getContexts();
            entityInstanceManager = systemState.getEntityInstanceManager();
            grid = systemState.getGrid();
            //systemStateByTicks.remove(currentNumOfTicks);
            simulationBackCounter[0]--;
            ticksCounter[0]--;
            simulationHistory.setCurrentTick( ticksCounter[0]);
        }


    }

    public boolean isHistoryOver(){
        return simulationBackCounter[0] <=0;
    }

    public void  setCurrentPopulationStatus(Map<String,Integer> currentPopulationStatus,Context context){
        entityInstanceManager.getInstances().forEach((s, entityInstances) -> {
            currentPopulationStatus.put(s,entityInstances.size());
        });
    }
    public void  saveStateByTicks(int ticks,Map<String, List<Context>> contexts,SystemState curremtSystemState){
        SystemState state = deepCopyContextMap(contexts,curremtSystemState);
        if(systemStateByTicks.size() >20){
            if (!systemStateByTicks.isEmpty()) {
                Iterator<Map.Entry<Integer, SystemState>> iterator = systemStateByTicks.entrySet().iterator();
                if (iterator.hasNext()) {
                    iterator.next();
                    iterator.remove();
                }
            }
        }

        systemStateByTicks.put(ticks,state);
    }
    private SystemState deepCopyContextMap(Map<String, List<Context>> original,SystemState curremtSystemState) { //TODO Update population by ticks
        Map<String, List<Context>> copyMap = new HashMap<>();
        EntityInstanceManager copyEntitymanger =new EntityInstanceManagerImpl();
        //ActiveEnvironment activeEnvironment1= new ActiveEnvironmentImpl();
        //activeEnvironment1.deepCopy( context.getActiveEnvironment().getEnvVariables());
        Grid copyGrid = new Grid(grid.getNumOfRows(),grid.getNumOfColumns());
        copyGrid.deepCopy(grid.getEntityMatrix());

        copyEntitymanger.deepCopy(entityInstanceManager.getInstances(),entityInstanceManager.getEntityDefinitions(), grid);
        original.forEach((s, contexts1) -> {
            List contextList = new ArrayList<>();
            contexts1.forEach(context -> {
                Map<String, PropertyInstance> properties = new HashMap<>();
                List<PropertyDefinition> propList = new ArrayList<>();
                EntityInstance entity = new EntityInstanceImpl(context.getPrimaryEntityInstance().getEntityDefinition(), context.getPrimaryEntityInstance().getId(), context.getPrimaryEntityInstance().getLocation());
                context.getPrimaryEntityInstance().getEntityDefinition().getProps().forEach(propertyDefinition -> {

                    propList.add(propertyDefinition);
                });
                entity.getEntityDefinition().setProps(propList);
                context.getPrimaryEntityInstance().getPropertyMap().forEach((s1, propertyInstance) -> {

                    PropertyInstance propertyInstance1 =deepCopyPropertyInstance(propertyInstance);
                    properties.put(s1,propertyInstance1);
                });
                entity.setProperties(properties);
                //activeEnvironment1.deepCopy(context.getActiveEnvironment().getEnvVariables());
                ActiveEnvironment activeEnvironment1= context.getActiveEnvironment();
                Context context1 = new ContextImpl(entity,activeEnvironment1);
                contextList.add(context1);
            });
            copyMap.put(s,contextList);
        });


        SystemState state = new SystemState(copyEntitymanger,copyGrid,curremtSystemState.getTicks(),copyMap);
        return state;
    }


    public PropertyInstance deepCopyPropertyInstance(PropertyInstance prop){
        PropertyDefinition propertyDefinition  =null;
        switch (prop.getPropertyDefinition().getType().name().toLowerCase()) {
            case "float":
                try {
                      propertyDefinition =  (new FloatPropertyDefinition(prop.getPropertyDefinition().getName(),
                                ValueGeneratorFactory.createFixed(Double.parseDouble( prop.getValue().toString())),
                               (Double)  prop.getPropertyDefinition().getRange().getKey(),
                               ((Double)prop.getPropertyDefinition().getRange().getValue())
                              ));

                } catch (Exception e) {
                    throw new IllegalArgumentException("Cannot do casting from this initial value type: " + prop.getValue() + "to float");
                }

                break;
            case "boolean":
                try {

                    propertyDefinition=  (new BooleanPropertyDefinition(prop.getPropertyDefinition().getName(),
                                ValueGeneratorFactory.createFixed(Boolean.parseBoolean(prop.getValue().toString()))));

                } catch (Exception e) {
                    throw new IllegalArgumentException("Cannot do casting from this initial value type: " + prop.getValue() + "to boolean");
                }
                break;
            case "string":
                try {
                    propertyDefinition = new StringPropertyDefinition(prop.getPropertyDefinition().getName(),
                            ValueGeneratorFactory.createFixed((String) prop.getValue()));
                }  catch (Exception e) {
            throw new IllegalArgumentException("Cannot do casting from this initial value type: " + prop.getValue() + "to String");
        }

                break;
        }
        PropertyInstance propertyInstance = new PropertyInstanceImpl(propertyDefinition,prop.getValue());
        return propertyInstance;
    }

    public void setSimulationStatus(ExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;
    }
}
