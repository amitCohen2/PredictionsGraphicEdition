package SystemLogic.history.imp;

import SystemLogic.execution.context.Context;
import SystemLogic.execution.instance.enitty.EntityInstance;
import SystemLogic.execution.instance.enitty.manager.EntityInstanceManager;
import SystemLogic.execution.instance.property.PropertyInstance;
import SystemLogic.history.api.History;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class SimulationHistory implements History , Serializable {
    private final Map<String, List<Context>> beginningSimulationDetails;
    private Map<String, List<Context>> afterSimulationDetails;
    private Map<String, List<Integer>> entityPopulationByTicks;
    private final SimpleDateFormat dateFormat;
    private final Date currentDate;
    private  int NumberOfProperty;
    EntityInstanceManager entityInstanceManager;
    private int endTicks;

    private int  currentTick;
    @Override
   public void setCurrentTick(int currentTick){
       this.currentTick = currentTick;
   }
   @Override
   public int getCurrentTick(){
       return this.currentTick;
   }

   public  int getSizeOfEntityPopulationByTicks(){
        int res =0;
        int max =0;
       for (Map.Entry<String, List<Integer>> entry :entityPopulationByTicks.entrySet()){
           res = entry.getValue().size();
           if(res >= max){
               max = res;
           }
       }

        return max;
   }
    public int getNumberOfProperty(){
        return this.NumberOfProperty;
    }
    public int  getNumberOfEntitys(){
        return afterSimulationDetails.values().size();
    }
    public void printEntitys() {
        List<String> keys = new ArrayList<>(afterSimulationDetails.keySet());

        for (int i = 0; i < keys.size(); i++) {
            System.out.println((i + 1) + ". " + keys.get(i));
        }
    }
    public SimulationHistory(Map<String, List<Context>> entities, EntityInstanceManager entityInstanceManager) {
        this.beginningSimulationDetails = new HashMap<>(entities);
        this.dateFormat = new SimpleDateFormat("dd-MM-yyyy | HH.mm.ss");
        this.currentDate = new Date();
        this.entityInstanceManager= entityInstanceManager;
    }



    public void updateSimulationHistory(Map<String, List<Context>> entities, Map<String, List<Integer>> entityPopulationByTicks) {
        this.afterSimulationDetails = new HashMap<>(entities);
        this.entityPopulationByTicks = new HashMap<>(entityPopulationByTicks);
    }


    public String getDate() {
        return dateFormat.format(currentDate);
    }

    @Override
    public StringBuilder getBeforeAfterEntitiesAmount(EntityInstanceManager entityInstanceManager) {
        StringBuilder result = new StringBuilder();
        int count = 1;
        String prefix = "\t";
        result.append("The Entities List:\n");
        for (Map.Entry<String, List<Context>> entry : beginningSimulationDetails.entrySet()) {
            String entityName = entry.getKey();
            int beforeSize = entry.getValue().size();

            result.append(prefix).append(count).append(". ").append(entityName + " ");
            result.append("population amount before: ").append(beforeSize).append(" -> after: ");

            List<Context> contexts = this.afterSimulationDetails.get(entityName);

            if (contexts == null) {
                throw new NullPointerException("This Entity out of contexts!");
            } else {
                result.append(entityInstanceManager.getInstances().get(entityName).size()).append("\n");
            }
            count++;
        }

        return result;
    }

    @Override
    public Map<String, List<Integer>> getEntityPopulationByTicks() {
        if (currentTick < entityPopulationByTicks.size()) {
            Map<String, List<Integer>> result = new HashMap<>();
            int count = 0;

            for (Map.Entry<String, List<Integer>> entry : entityPopulationByTicks.entrySet()) {
                if (count >= currentTick) {
                    break; // Stop once we reach or exceed currentTick
                }

                result.put(entry.getKey(), entry.getValue());
                count++;
            }

            return result;
        } else {
            return entityPopulationByTicks; // Return the entire map
        }
    }


    @Override
    public StringBuilder getPropertiesList(int userEntityChoice) {
        StringBuilder result = new StringBuilder();
        String prefix = "\t";
        int count = 1, propertyCount = 1;

        if (beginningSimulationDetails.size() > userEntityChoice || userEntityChoice < 1) {
            throw new IndexOutOfBoundsException("Please choose a property between 1 to " + userEntityChoice);
        }

        for (Map.Entry<String, List<Context>> entry : beginningSimulationDetails.entrySet()) {
            if (userEntityChoice == count) {
                String entityName = entry.getKey();
                List<Context> contexts = entry.getValue();
                if (contexts.size() == 0) {
                    throw new NullPointerException("There is no entities Contexts for this entity");
                }
                //get the properties
                Map<String, PropertyInstance> properties = contexts.get(0).getPrimaryEntityInstance().getPropertyMap();
                result.append(prefix).append("The Properties List: \n");
                prefix += "\t";
                for (Map.Entry<String, PropertyInstance> property : properties.entrySet()) {
                    result.append(prefix).append(propertyCount).append(". ").append(property.getKey()).append("\n");
                    propertyCount++;
                }
                break;
            }
            count++;
        }
        NumberOfProperty=propertyCount;
        return result;
    }


    @Override
    public StringBuilder getPropertyHistory(int userEntityChoice, int userPropertyChoice) {
        String entityName = findEntityName(userEntityChoice), propertyName = findPropertyName(entityName, userPropertyChoice);
        Map<Object, Integer> resultMap = new HashMap<>();
        int countEntity = 1;

        //get entities contexts
        List<Context> contexts = afterSimulationDetails.get(entityName);
        if (contexts.size() == 0) {
            throw new NullPointerException("There is no entities Contexts for this entity");
        }

        if (entityInstanceManager.getInstances().size() == 0) {
            throw new NullPointerException("There is no population details to show for this property. All dont survive this simulation!");
        }

        for (EntityInstance instance : entityInstanceManager.getInstances().get(contexts.get(0).getPrimaryEntityInstance().getEntityDefinition().getName())) {
            Map<String, PropertyInstance> properties = instance.getPropertyMap();
            if (userPropertyChoice < 1 || userPropertyChoice > properties.size()) {
                throw new IndexOutOfBoundsException("Please choose a property between 1 to " + userPropertyChoice);
            }

            Object currPropertyValue = properties.get(propertyName).getValue();
            if (resultMap.containsKey(currPropertyValue)) {
                Integer currPropertyCount = resultMap.get(currPropertyValue);
                resultMap.put(currPropertyValue, currPropertyCount + 1);
            } else {
                resultMap.put(currPropertyValue, 1);
            }
        }

        return exactResultFromMap(resultMap, propertyName);
    }

    public Map<Object, Integer> getPropertyHistoryMap(String entityName, String propertyName) {
        Map<Object, Integer> resultMap = new HashMap<>();

        int countEntity = 1;

        //get entities contexts
        List<Context> contexts = afterSimulationDetails.get(entityName);
        if (contexts.size() == 0) {
            throw new NullPointerException("There is no entities Contexts for this entity");
        }

        if (entityInstanceManager.getInstances().size() == 0) {
            throw new NullPointerException("There is no population details to show for this property. All dont survive this simulation!");
        }

        entityInstanceManager.getInstances().get(contexts.get(0).getPrimaryEntityInstance().getEntityDefinition().getName()).forEach(instance -> {
            Map<String, PropertyInstance> properties = instance.getPropertyMap();
            Object currPropertyValue = properties.get(propertyName).getValue();
            if (currPropertyValue instanceof Double) {
                currPropertyValue = Math.round((Double) currPropertyValue);
            }
            if (resultMap.containsKey(currPropertyValue)) {
                Integer currPropertyCount = resultMap.get(currPropertyValue);
                resultMap.put(currPropertyValue, currPropertyCount + 1);
            } else {
                resultMap.put(currPropertyValue, 1);
            }
        });

        return resultMap;
    }

    public Map<String, Double> getPropertyConsistencyHistory(String entityName) {
        List<Context> contexts = afterSimulationDetails.get(entityName);
        Map<String, List<List<Integer>>> PropertyConsistenciesValues = new HashMap<>();
        // get all the entity instances consistencies
        entityInstanceManager.getInstances().get(entityName).forEach(entityInstance -> {
            entityInstance.getConsistency().forEach((property, consistency) -> {
                if (PropertyConsistenciesValues.containsKey(property)) {
                    PropertyConsistenciesValues.get(property).add(consistency);
                } else {
                    List<List<Integer>> newList = new ArrayList<>();
                    newList.add(consistency);
                    PropertyConsistenciesValues.put(property, newList);
                }
            });
        });

        Map<String, Double> result = new HashMap<>();
        //calculate it
        PropertyConsistenciesValues.forEach((property, listCons) -> {
            final int[] population = {0};
            final double[] sumValues = {0};
            listCons.forEach(consistency -> {
                sumValues[0] += calculateConsistency(consistency);
                population[0]++;
            });
            result.put(property, sumValues[0] / population[0]);
        });

        if (result.size() == 0) {
            return null;
        }

        return result;
    }

    private double calculateConsistency(List<Integer> consistency) {
        final int[] numOfValue = {0};
        final int[] prev = {0};
        final double[] sumValues = {0};

        consistency.add(endTicks);
        consistency.forEach(value -> {
            sumValues[0] += value - prev[0];
            prev[0] = value;
            numOfValue[0]++;
        });

        return sumValues[0] / numOfValue[0];
    }


    private StringBuilder exactResultFromMap(Map<Object, Integer> resultMap, String propertyName) {
        String prefix = "\t\t";
        StringBuilder result = new StringBuilder();
        result.append(prefix).append("The Property \"").append(propertyName).append("\" History:\n");
        prefix += "\t";

        for(Map.Entry<Object, Integer> entry : resultMap.entrySet()) {
            result.append(prefix).append(entry.getValue()).append(" instances with the property \"");
            result.append(propertyName).append("\" value of: ").append(entry.getKey()).append("\n");
        }
        return result;
    }

    private String findPropertyName(String entityName, int userPropertyChoice) {
        List<Context> contexts = afterSimulationDetails.get(entityName);
        String result = null;
        for(Context context : contexts) {
            int countProperty = 1;
            Map<String, PropertyInstance> properties = context.getPrimaryEntityInstance().getPropertyMap();

            for (Map.Entry<String, PropertyInstance> property : properties.entrySet()) {
                if (countProperty == userPropertyChoice) {
                    result = property.getKey();
                    break;
                }
                countProperty++;
            }
        }
        if (result == null) {
            throw new IndexOutOfBoundsException("Please choose a property between 1 to " + userPropertyChoice);
        }

        return result;
    }

    private String findEntityName(int userEntityChoice) {
        int countEntity = 1;
        String result = null;
        for (Map.Entry<String, List<Context>> entry : afterSimulationDetails.entrySet()) {
            if (countEntity == userEntityChoice) {
                result = entry.getKey();
                break;
            }
            countEntity++;
        }

        if (result == null) {
            throw new IndexOutOfBoundsException("Please choose a entity between 1 to " + userEntityChoice);
        }
        return result;

    }


    public void updateNumberOfEndTicks(int endTicks) {
        this.endTicks = endTicks;
    }
}
