package convertor;

import SystemLogic.Expression.api.enums.FunctionType;
import SystemLogic.action.api.Action;
import SystemLogic.action.api.ActionType;
import SystemLogic.action.impl.*;
import SystemLogic.action.impl.condition.Imp.ConditionAction;
import SystemLogic.action.impl.condition.api.Condition;

import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.definition.entity.EntityDefinitionImpl;
import SystemLogic.definition.environment.impl.EnvVariableManagerImpl;
import SystemLogic.definition.property.api.PropertyDefinition;
import SystemLogic.definition.property.impl.BooleanPropertyDefinition;
import SystemLogic.definition.property.impl.FloatPropertyDefinition;
import SystemLogic.definition.property.impl.StringPropertyDefinition;
import SystemLogic.definition.value.generator.api.ValueGeneratorFactory;
import SystemLogic.execution.instance.environment.api.ActiveEnvironment;
import SystemLogic.execution.instance.property.PropertyInstanceImpl;
import SystemLogic.grid.imp.Grid;
import SystemLogic.rule.Rule;
import SystemLogic.rule.RuleImpl;
import SystemLogic.termination.TerminationImpl;
import XmlLoader.schema.PRDBySecond;
import XmlLoader.schema.*;
import design.body.execution.simulation.SimulationDetails;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class GeneratedXmlClassConverter implements Serializable {


    public static void initTermination(PRDTermination prdTermination, TerminationImpl termination) {

        for (Object ter : prdTermination.getPRDBySecondOrPRDByTicks()) {
            if (ter instanceof PRDByTicks) {
                termination.setTerminationByTicks(((PRDByTicks) ter).getCount());
            } else if (ter instanceof PRDBySecond) {
                termination.setTerminationBySec(((PRDBySecond) ter).getCount());
            }
        }
    }

    public static  Grid initGrid(PRDWorld.PRDGrid prdGrid, Grid grid){
       return new Grid(prdGrid.getRows(),prdGrid.getColumns());

    }

    public static void PrdEnvProperty2EvironmentManager(List<PRDEnvProperty> prdEnvProperties,
                                                 EnvVariableManagerImpl envVariablesManager,
                                                 ActiveEnvironment activeEnvironment,
                                                 Map<String, EnvironmentValuesFromUser> userEnvInput) {

        Set<String> envPropertyNames = new HashSet<>();
        for (PRDEnvProperty envProperty : prdEnvProperties) {
            String propName = envProperty.getPRDName();
            if (envPropertyNames.contains(propName)) {
                throw new IllegalArgumentException("Property with name '" + propName + "' is duplicated.");
            }
            envPropertyNames.add(propName);
        }

        prdEnvProperties.forEach(envprop -> {
            //TODO: add option to user enter an environment values and use FixedValueGenerator instead
            PropertyDefinition intProp = null;
            Object valueGenerated = null;

            if (userEnvInput.get(envprop.getPRDName()).isValueFromUser()) { // get the value from user
                if (envprop.getType().equals("float")) {
                    intProp = new FloatPropertyDefinition(envprop.getPRDName(),
                            ValueGeneratorFactory.createFixed((Double) userEnvInput.get(envprop.getPRDName()).getValue()),
                            envprop.getPRDRange().getFrom(), envprop.getPRDRange().getTo());
                }
                else if (envprop.getType().equals("boolean")) {
                    intProp = new BooleanPropertyDefinition(envprop.getPRDName(),
                            ValueGeneratorFactory.createFixed((Boolean) userEnvInput.get(envprop.getPRDName()).getValue()));
                }
                else if (envprop.getType().equals("string")) {
                    intProp = new StringPropertyDefinition(envprop.getPRDName(),
                            ValueGeneratorFactory.createFixed((String) userEnvInput.get(envprop.getPRDName()).getValue()));
                }
                else {
                    throw new IllegalArgumentException("This argument type: " + envprop.getType() + "does not supported!");
                }
            }
            else {
                if (envprop.getType().equals("float")) {
                    intProp = new FloatPropertyDefinition(envprop.getPRDName(),
                            ValueGeneratorFactory.createRandomFloat(envprop.getPRDRange().getFrom(), envprop.getPRDRange().getTo()),
                            envprop.getPRDRange().getFrom(), envprop.getPRDRange().getTo());
                }
                else if (envprop.getType().equals("boolean")) {
                    intProp = new BooleanPropertyDefinition(envprop.getPRDName(),
                            ValueGeneratorFactory.createRandomBoolean());
                }
                else if (envprop.getType().equals("string")) {
                    intProp = new StringPropertyDefinition(envprop.getPRDName(),
                            ValueGeneratorFactory.createRandomString());
                }
                else {
                    throw new IllegalArgumentException("This argument type: " + envprop.getType() + "does not supported!");
                }
            }

            valueGenerated = intProp.generateValue();
            activeEnvironment.addPropertyInstance(new PropertyInstanceImpl(intProp, valueGenerated));
            envVariablesManager.addEnvironmentVariable(intProp);
            envVariablesManager.addEnvironmentValues(envprop.getPRDName(), valueGenerated);
        });
    }

    //TODO: כן
    public static void PrdEntity2EntityMap(List<PRDEntity> prdEntity, Map<String, EntityDefinition> EntityMap, Map<String, Integer> entityPopulationMap) {
        prdEntity.stream()
                .map(entity -> {
                    int population = entityPopulationMap.get(entity.getName());
                    String name = entity.getName();
                    if (EntityMap.containsKey(name)) {
                        throw new IllegalArgumentException("Entity with name '" + name + "' already exists.");
                    }

                    EntityDefinition Entity = new EntityDefinitionImpl(name, population);
                    entity.getPRDProperties().getPRDProperty().forEach(prop -> {

                        switch (prop.getType()) {
                            case "float":
                                try {
                                    if (prop.getPRDValue().isRandomInitialize()) {
                                        Entity.getProps().add(new FloatPropertyDefinition(prop.getPRDName(),
                                                ValueGeneratorFactory.createRandomFloat(prop.getPRDRange().getFrom(), prop.getPRDRange().getTo()),
                                                prop.getPRDRange().getFrom(), prop.getPRDRange().getTo()));
                                    } else {
                                        Entity.getProps().add(new FloatPropertyDefinition(prop.getPRDName(),
                                                ValueGeneratorFactory.createFixed(Double.parseDouble(prop.getPRDValue().getInit())),
                                                prop.getPRDRange().getFrom(), prop.getPRDRange().getTo()));
                                    }
                                } catch (Exception e) {
                                    throw new IllegalArgumentException("Cannot do casting from this initial value type: " + prop.getPRDValue().getInit() + "to float");
                                }

                                break;
                            case "boolean":
                                try {
                                    if (prop.getPRDValue().isRandomInitialize()) {
                                        Entity.getProps().add(new BooleanPropertyDefinition(prop.getPRDName(),
                                                ValueGeneratorFactory.createRandomBoolean()));
                                    } else {
                                        Entity.getProps().add(new BooleanPropertyDefinition(prop.getPRDName(),
                                                ValueGeneratorFactory.createFixed(Boolean.parseBoolean(prop.getPRDValue().getInit()))));
                                    }
                                } catch (Exception e) {
                                    throw new IllegalArgumentException("Cannot do casting from this initial value type: " + prop.getPRDValue().getInit() + "to boolean");
                                }
                                break;
                            case "string":
                                if (prop.getPRDValue().isRandomInitialize()) {
                                    Entity.getProps().add(new StringPropertyDefinition(prop.getPRDName(),
                                            ValueGeneratorFactory.createRandomString()));
                                } else {
                                    Entity.getProps().add(new StringPropertyDefinition(prop.getPRDName(),
                                            ValueGeneratorFactory.createFixed(prop.getPRDValue().getInit())));
                                }
                                break;
                        }
                    });
                    Set<String> propertyNames = new HashSet<>();
                    for (PropertyDefinition prop : Entity.getProps()) {
                        String propName = prop.getName();
                        if (propertyNames.contains(propName)) {
                            throw new IllegalArgumentException("Property with name '" + propName + "' is duplicated.");
                        }
                        propertyNames.add(propName);
                    }
                    return Entity;
                })
                .forEach(entity -> EntityMap.put(entity.getName(), entity));
    }

    public static void PrdRule2RuleMap(List<PRDRule> prdRules, Map<String, Rule> RuleMap, Map<String , EntityDefinition> entity, Grid grid) {
        prdRules.forEach(prdRule ->
        {
            RuleImpl rule = new RuleImpl(prdRule.getName());

            prdRule.getPRDActions().getPRDAction().forEach(prdAction -> {

                //set activations
                Integer ticks = getTicks(prdRule.getPRDActivation());
                Double prob = getProbability(prdRule.getPRDActivation());
                rule.setActivation(ticks,prob);

                checkGetBy(prdAction.getBy());

                if(prdAction.getType().equals("increase"))
                {
                    rule.addAction(new IncreaseAction(entity.get(prdAction.getEntity()),prdAction.getProperty(),prdAction.getBy()));
                } else if (prdAction.getType().equals("kill")) {
                    rule.addAction(new KillAction(entity.get(prdAction.getEntity())));
                } else if (prdAction.getType().equals("condition")) {
                    //init then and Else conditions lists
                    ArrayList<Action> thenActions = null;
                    ArrayList<Action> elseActions = null;
                    if (prdAction.getPRDThen() != null) {
                        checkPRDinerConditionAction(prdAction.getPRDThen().getPRDAction());

                        thenActions =  prdToActionList(prdAction,entity, prdAction.getPRDThen().getPRDAction(),grid);
                    }
                    if (prdAction.getPRDElse() != null) {
                        checkPRDinerConditionAction(prdAction.getPRDElse().getPRDAction());
                        elseActions =  prdToActionList(prdAction,entity, prdAction.getPRDElse().getPRDAction(),grid);
                    }
                    ArrayList<Condition> conditions = prdCondition2ConditionList(prdAction.getPRDCondition());
                    Condition condition = new Condition(prdAction.getEntity(),prdAction.getProperty(), prdAction.getBy(),prdAction.getPRDCondition().getOperator(),prdAction.getPRDCondition().getSingularity(),prdAction.getPRDCondition().getLogical()
                    ,thenActions, elseActions, conditions);
                    rule.addAction(new ConditionAction(ActionType.CONDITION,entity.get(prdAction.getEntity()),"condition",condition));
                } else if (prdAction.getType().equals("decrease")) {
                    rule.addAction(new DecreaseAction(entity.get(prdAction.getEntity()),prdAction.getProperty(),prdAction.getBy()));
                } else if (prdAction.getType().equals("set")) {
                    rule.addAction(new SetAction(entity.get(prdAction.getEntity()),prdAction.getProperty(),prdAction.getValue()));
                }
                else if ((prdAction.getType().equals("proximity"))){
                    ArrayList<Action>  thenActions =  prdToActionList(prdAction,entity, prdAction.getPRDActions().getPRDAction(),grid);
                    rule.addAction(new ProximityAction(ActionType.Proximity,entity.get(prdAction.getPRDBetween().getSourceEntity()), entity.get(prdAction.getPRDBetween().getTargetEntity()), prdAction.getPRDEnvDepth().getOf(),grid,thenActions));
                } else if ((prdAction.getType().equals("ticks"))) {
                    // TODO: employ this - logic in TreeViewDetails
                }
                else if((prdAction.getType().equals("replace"))){
                    rule.addAction(new ReplaceAction(entity.get(prdAction.getPRDBetween().getSourceEntity()),entity.get(prdAction.getPRDBetween().getTargetEntity()),prdAction.getMode(),grid));
                }

            });
            RuleMap.put(prdRule.getName(),rule);
        });
    }

    public static void checkPRDinerConditionAction(List<PRDAction> condList){
        for(PRDAction cond : condList){
            checkGetBy(cond.getBy());
        }

    }
    public static void checkGetBy(String by) {
        if (by != null) {
            if (isNumeric(by)) {
                return;
            }
            else if(!isFunction(by)) {
                throw new IllegalArgumentException("This By expression: " + by + " is not valid!");
            }
        }
    }
    public static void checkByExpression(String by, SimulationDetails simulationDetails) {
        if (by != null) {
            if (isNumeric(by)) {
                return;
            }
            else if(!isCorrectExpression(by, simulationDetails)) {
                throw new IllegalArgumentException("This By expression: " + by + " is not valid!");
            }
        }
    }

    private static boolean isCorrectExpression(String by, SimulationDetails simulationDetails) {
        if (by == null || by.isEmpty()) {
            return false;
        }
        String functionExtract = extractSubstring(by);
        for (FunctionType func : FunctionType.values()) {
            if (func.name().equals(functionExtract.toUpperCase())) {
                return checkInnerExpression(by, functionExtract, simulationDetails);
            }
        }
        return false;
    }

    //TODO: finish this function
    private static boolean checkInnerExpression(String by, String functionExtract, SimulationDetails simulationDetails) {
        if(functionExtract.equals("environment")) {
            String pattern = "^(.*?)\\(([^)]+)\\)$";

            Pattern regexPattern = Pattern.compile(pattern);
            Matcher matcher = regexPattern.matcher(by);

            if (matcher.matches()) {
                String functionType = matcher.group(1);
                String environmentParam = matcher.group(2);
                if (simulationDetails.getEnvironments().containsKey(environmentParam)) {
                    return true;
                } else {
                    throw new IllegalArgumentException("This environment variable: '" + environmentParam +"' does not exist!");
                }
            }
            else {
                throw new IllegalArgumentException("Wrong format pattern for environment function!");
            }
        } else if (functionExtract.equals("ticks") || functionExtract.equals("evaluate")){
            String pattern = "^(.*?)\\((.*?)\\.(.*?)\\)$";

            Pattern regexPattern = Pattern.compile(pattern);
            Matcher matcher = regexPattern.matcher(by);

            if (matcher.matches()) {
                String functionType = matcher.group(1);
                String entityName = matcher.group(2);
                String propertyName = matcher.group(3);

                if (simulationDetails.getEntityPropertiesSimulationMap().containsKey(entityName)) {
                    if (simulationDetails.getEntityPropertiesSimulationMap().get(entityName).contains(propertyName)) {
                        return true;
                    } else {
                        throw new IllegalArgumentException("This property: '" + propertyName +"' does not exist in the entity: ' " + entityName + "' in this expression:\n" + by);
                    }
                } else {
                    throw new IllegalArgumentException("This entity: '" + entityName +"' does not exist in this expression:\n" + by);
                }
            } else {
                throw new IllegalArgumentException("Wrong format pattern for '" + functionExtract + "' function!");
            }
        } else if (functionExtract.equals("random")) {
            String pattern = "^(.*?)\\((.*?)\\.(.*?)\\)$";

            Pattern regexPattern = Pattern.compile(pattern);
            Matcher matcher = regexPattern.matcher(by);

            if (matcher.matches()) {
                String functionType = matcher.group(1);
                String numberString  = matcher.group(2);

                try {
                    int number = Integer.parseInt(numberString);
                    return true;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("This random function should get an integer parameter instead: " + numberString);
                }
            }
            else {
                throw new IllegalArgumentException("Wrong format pattern for '" + functionExtract + "' function!");
            }
        } else if (functionExtract.equals("percent")) {
            String pattern = "^(.*?)\\((\\d+),(\\d+)\\)$";

            Pattern regexPattern = Pattern.compile(pattern);
            Matcher matcher = regexPattern.matcher(by);

            if (matcher.matches()) {
                String functionType = matcher.group(1);
                String number1String = matcher.group(2);
                String number2String = matcher.group(3);

                try {
                    double number1 = Double.parseDouble(number1String);
                    double number2 = Double.parseDouble(number2String);
                    return true;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("This random function should get an number parameters instead: " + number1String + ", " + number2String);
                }
            }
            else {
                throw new IllegalArgumentException("Wrong format pattern for '" + functionExtract + "' function!");
            }
        } else {
            throw new IllegalArgumentException("This function: " + functionExtract + "does not supported!");

        }
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private boolean ifByIsFunction(String by) {

        int openParenthesisIndex = by.indexOf('(');

        if (openParenthesisIndex != -1 && isFunction(by.substring(0, openParenthesisIndex))) {
            return true;
        }

        return false;
    }

    public static boolean isFunction(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        input = extractSubstring(input);
        for (FunctionType func : FunctionType.values()) {
            if (func.name().equals(input.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public static String extractSubstring(String input) {
        int indexOfParenthesis = input.indexOf("(");
        if (indexOfParenthesis != -1) {
            return input.substring(0, indexOfParenthesis);
        } else {
            return input;
        }
    }


    public static boolean isPropertyExist(String prop, EntityDefinition entity)
    {

        for(PropertyDefinition property : entity.getProps())
        {
            if(property.getName().equals(prop)){

                return true;
            }
        }
            return false;

    }



    public static Integer getTicks(PRDActivation prdActivation) {
        Integer result = 1;
        if (prdActivation != null) {
            if (prdActivation.getTicks() != null) {
                result = prdActivation.getTicks();
            }
        }
        return result;
    }
    public static Double getProbability(PRDActivation prdActivation) {
        Double result = new Double(1);
        if (prdActivation != null) {
            if (prdActivation.getProbability() != null)
            {
                result = prdActivation.getProbability();
            }
        }
        return result;
    }


    public static ArrayList<Action> prdToActionList(PRDAction prdAction, Map<String, EntityDefinition> entity, List<PRDAction> predActionList, Grid grid){ // TODO: check if the entity map is good here to save this
        ArrayList<Action> resArrayList = null;

        if (predActionList != null) {
            resArrayList = new ArrayList<>();

            for(PRDAction action : predActionList) {
                if(action.getType().equals("increase"))
                {
                    resArrayList.add(new IncreaseAction(entity.get(action.getEntity()),action.getProperty(),action.getBy()));
                }
                else if(action.getType().equals("decrease"))
                {
                    resArrayList.add(new DecreaseAction(entity.get(action.getEntity()),action.getProperty(),action.getBy()));
                }
                else if(action.getType().equals("set"))
                {
                    resArrayList.add(new SetAction(entity.get(action.getEntity()),action.getProperty(),action.getValue()));
                }
                else if(action.getType().equals("kill"))
                {
                    resArrayList.add(new KillAction(entity.get(action.getEntity())));
                }
                else if (action.getType().equals("replace")){
                    resArrayList.add(new ReplaceAction(entity.get(action.getKill()),entity.get(action.getCreate()),action.getMode(),grid));
                }

            }
        }
          return  resArrayList;
    }

    //TODO: understand what is going on here
    public static ArrayList<Condition> prdCondition2ConditionList(PRDCondition prdCondition) {
        ArrayList<Condition> conditionArrayList = new ArrayList<>();

    if(prdCondition.getPRDCondition().size() == 0){
        Condition condition = new Condition(prdCondition.getEntity(), prdCondition.getProperty(), prdCondition.getValue(), prdCondition.getOperator(),
                prdCondition.getSingularity(), prdCondition.getLogical(), null,null, null);
        conditionArrayList.add(condition);
    }
    else{
       // ArrayList<Condition> subConditionArrayList = new ArrayList<>();
        for(PRDCondition prdCondition1 : prdCondition.getPRDCondition()){
            Condition condition = new Condition(prdCondition1.getEntity(), prdCondition1.getProperty(), prdCondition1.getValue(), prdCondition1.getOperator(),
                    prdCondition1.getSingularity(), prdCondition1.getLogical(), null,null, null);
            conditionArrayList.add(condition);
        }
    }
    return conditionArrayList;
    }
    public static double generateRandomNumber(double from, double to) {
        if (from >= to) {
            throw new IllegalArgumentException("The 'from' number must be less than the 'to' number.");
        }

        Random random = new Random();
        return random.nextDouble() * (to - from) + from;
    }

    public static int getThreadsNumber(PRDWorld world) {
        return world.getPRDThreadCount();
    }
}
