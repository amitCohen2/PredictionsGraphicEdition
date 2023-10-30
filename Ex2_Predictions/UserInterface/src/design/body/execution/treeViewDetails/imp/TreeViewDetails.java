package design.body.execution.treeViewDetails.imp;

import XmlLoader.exceptions.FileFormatException;
import XmlLoader.schema.*;
import design.body.execution.xmlSimulationDetails.imp.SimulationDetails;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static convertor.imp.GeneratedXmlClassConverter.*;

public class TreeViewDetails {
    public static TreeItem<String> setOtherDetails(PRDWorld world, SimulationDetails simulationDetails) throws FileFormatException {
        TreeItem<String> rootItem = new TreeItem<>("Other details");
        // init threads pool
        TreeItem<String> threadSizeBranch = new TreeItem<>("Thread pool count: " + world.getPRDThreadCount());
        //init Grid

        TreeItem<String> gridBranchRoot = new TreeItem<>("Grid");
        if (gridSizeError(world.getPRDGrid())) {
            throw new IllegalArgumentException("The grid size is wrong! it should be between 10-100 in columns and rows!");
        }
        TreeItem<String> gridRowsBranch = new TreeItem<>("Rows: " + world.getPRDGrid().getRows());
        TreeItem<String> gridColsBranch = new TreeItem<>("Columns: " + world.getPRDGrid().getColumns());
        gridBranchRoot.getChildren().addAll(gridRowsBranch, gridColsBranch);

        //init Termination
        TreeItem<String> terminationBranchRoot = new TreeItem<>("Termination");
        if (world.getPRDTermination().getPRDBySecondOrPRDByTicks().size() != 0) {
            world.getPRDTermination().getPRDBySecondOrPRDByTicks().forEach(obj -> {
                TreeItem<String> terminationBranch = new TreeItem<>();
                if (obj instanceof PRDByTicks) {
                    terminationBranch.setValue("Ticks: " + ((PRDByTicks) obj).getCount());
                } else if (obj instanceof PRDBySecond) {
                    terminationBranch.setValue("Seconds: " + ((PRDBySecond) obj).getCount());
                }
                terminationBranchRoot.getChildren().add(terminationBranch);
            });
        }
        else if (world.getPRDTermination().getPRDByUser() != null) {
            terminationBranchRoot.getChildren().add(new TreeItem<>("Simulation Ends By User"));
        }
        else {
            throw new FileFormatException("File Format Exception!\n" +
                    " This format should have one of this two end conditions:\n1. End by user.\n2. End by ticks or Seconds.");
        }

        if (world.getPRDTermination().getPRDBySecondOrPRDByTicks().size() != 0 && world.getPRDTermination().getPRDByUser() != null) {
            throw new FileFormatException("File Format Exception!\n" +
                    " This format should have one of this two end conditions:\n1. End by user.\n2. End by ticks or Seconds.");
        }


        rootItem.getChildren().addAll(threadSizeBranch, gridBranchRoot, terminationBranchRoot);
        return rootItem;
    }
    private static boolean gridSizeError(PRDWorld.PRDGrid prdGrid) {
        return (prdGrid.getColumns() < 10 || prdGrid.getRows() < 10 || prdGrid.getRows() > 100 || prdGrid.getColumns() > 100);
    }
    public static TreeItem<String> setEnvironmentsDetails(PRDWorld world, SimulationDetails simulationDetails) {
        TreeItem<String> rootItem = new TreeItem<>("Environments");
        Map<String, PRDEnvProperty> environmentsMap = new HashMap<>();
        List<TreeItem<String>> environments = new ArrayList<>();

        world.getPRDEnvironment().getPRDEnvProperty().forEach(environmentProperty -> {
            environmentsMap.put(environmentProperty.getPRDName(), environmentProperty);
            TreeItem<String> currEnvironmentBranch = new TreeItem<>(environmentProperty.getPRDName());
            TreeItem<String> currEnvironmentType = new TreeItem<>("Type: " + environmentProperty.getType());
            TreeItem<String> currEnvironmentRange = new TreeItem<>();
            String currRange = null;
            if (environmentProperty.getPRDRange() != null) {
                currRange = "Range: [" + environmentProperty.getPRDRange().getFrom() + ", "
                        + environmentProperty.getPRDRange().getTo() + "]";
            }
            else {
                currRange = "Range: no range";
            }
            currEnvironmentRange.setValue(currRange);

            currEnvironmentBranch.getChildren().addAll(currEnvironmentType, currEnvironmentRange);
            environments.add(currEnvironmentBranch);
        });

        environments.forEach(environmentBranch -> rootItem.getChildren().add(environmentBranch));
        simulationDetails.setEnvironments(environmentsMap);
        return rootItem;
    }
    public static TreeItem<String> setEntitiesDetails(PRDWorld world, SimulationDetails simulationDetails) {
        TreeItem<String> rootItem = new TreeItem<>("Entities");
        Map<String, List<String>> entityPropertiesSimulationMap = new HashMap<>();

        List<TreeItem<String>> entities = new ArrayList<>();

        world.getPRDEntities().getPRDEntity().forEach(entity -> {
            //check duplication of same entity
            if (entityPropertiesSimulationMap.containsKey(entity.getName())) {
                throw new IllegalArgumentException("You have duplication in the entity name: " + entity.getName());
            }
            TreeItem<String> currEntityBranch = new TreeItem<>(entity.getName());
            List<TreeItem<String>> properties = new ArrayList<>();
            List<String> propertiesNames = new ArrayList<>();

            entity.getPRDProperties().getPRDProperty().forEach(property -> {
                //check duplication of same property in same entity
                if (propertiesNames.contains(property.getPRDName())) {
                    throw new IllegalArgumentException("You have duplication in the property name: " + property.getPRDName()
                            + " in the entity: " + entity.getName());
                }

                TreeItem<String> currPropertyNameRoot = new TreeItem<>(property.getPRDName());
                propertiesNames.add(property.getPRDName());
                //set the value
                TreeItem<String> currValueRoot = new TreeItem<>("Value");
                TreeItem<String> isRandomInitBranch = new TreeItem<>("random-initialize: " + property.getPRDValue().isRandomInitialize());
                currValueRoot.getChildren().add(isRandomInitBranch);
                if (!property.getPRDValue().isRandomInitialize()) {
                    currValueRoot.getChildren().add(new TreeItem<>("init: " + property.getPRDValue().getInit()));
                }

                //set the value
                TreeItem<String> currPropertyTypeBranch = new TreeItem<>("Type: " + property.getType());

                //set the range
                TreeItem<String> currPropertyRangeBranch = new TreeItem<>();
                String currRange = null;
                if (property.getPRDRange() != null) {
                    currRange = "Range: [" + property.getPRDRange().getFrom() + ", "
                            + property.getPRDRange().getTo() + "]";
                }
                else {
                    currRange = "Range: no range";
                }
                currPropertyRangeBranch.setValue(currRange);

                // add all the property details
                currPropertyNameRoot.getChildren().addAll(currPropertyTypeBranch, currPropertyRangeBranch, currValueRoot);
                properties.add(currPropertyNameRoot);

            });

            // add the curr properties to the entity dad
            properties.forEach(propertyBranch -> currEntityBranch.getChildren().add(propertyBranch));
            entities.add(currEntityBranch);
            entityPropertiesSimulationMap.put(entity.getName(), propertiesNames);
        });

        //add all the entities branches to the root
        entities.forEach(entityBranch -> rootItem.getChildren().add(entityBranch));
        simulationDetails.setEntityPropertiesSimulationMap(entityPropertiesSimulationMap);
        return rootItem;
    }
    public static TreeItem<String> setRulesDetails(PRDWorld world, SimulationDetails simulationDetails) {
        TreeItem<String> rootItem = new TreeItem<>("Rules");
        List<TreeItem<String>> rules = new ArrayList<>();
        world.getPRDRules().getPRDRule().forEach(rule -> {

            TreeItem<String> currRuleBranch = getRuleActionsBranch(rule, simulationDetails);

            rules.add(currRuleBranch);
        });

        rules.forEach(rule -> rootItem.getChildren().add(rule));
        return rootItem;
    }

    private static TreeItem<String> getRuleActionsBranch(PRDRule rule, SimulationDetails simulationDetails) {
        TreeItem<String> resRoot = new TreeItem<>(rule.getName());
        final int[] actionsCounter = {0};
        rule.getPRDActions().getPRDAction().forEach(action -> {
            actionsCounter[0]++;
            TreeItem<String> actionDetails = getActionBranch(rule, simulationDetails, action, actionsCounter[0]);
            resRoot.getChildren().addAll(actionDetails);
        });

        return resRoot;
    }

    private static TreeItem<String> getActionBranch(PRDRule rule, SimulationDetails simulationDetails, PRDAction action, int counter) {
        TreeItem<String> result = new TreeItem<>("Action " + counter);

        //set activations
        result.getChildren().add(getActivationRule(rule));;
        checkGetBy(action.getBy());

        if (action.getType().equals("increase") || action.getType().equals("decrease"))
        {
            checkEntityAndPropertyExist(rule, simulationDetails, action);
            getIncreaseDecreaseDetails(result, action, simulationDetails);

        } else if (action.getType().equals("kill")) {
            checkEntityExist(rule, simulationDetails, action);
            getKillDetails(result, action);
            //rule.action(new KillAction(entity.get(action.getEntity())));
        } else if (action.getType().equals("condition")) {
            // add the conditions branch
            List<TreeItem<String>> conditions = makeConditionsListTree(action.getPRDCondition(), simulationDetails);
            TreeItem<String> conditionsBranch = new TreeItem<>("Conditions");
            conditions.forEach(condition -> conditionsBranch.getChildren().add(condition));
            result.getChildren().add(conditionsBranch);

            //add the then else branches
            if (action.getPRDThen() != null) {
                checkPRDinerConditionAction(action.getPRDThen().getPRDAction());
                List<TreeItem<String>> thenActions =  initTreeItemList(rule, action, simulationDetails, action.getPRDThen().getPRDAction());
                TreeItem<String> thenBranch = new TreeItem<>("Then");
                thenActions.forEach(tr -> thenBranch.getChildren().add(tr));
                result.getChildren().add(thenBranch);
            }
            if (action.getPRDElse() != null) {
                checkPRDinerConditionAction(action.getPRDElse().getPRDAction());
                List<TreeItem<String>> elseActions =  initTreeItemList(rule, action, simulationDetails, action.getPRDElse().getPRDAction());
                TreeItem<String> elseBranch = new TreeItem<>("Else");
                elseActions.forEach(tr -> elseBranch.getChildren().add(tr));
                result.getChildren().add(elseBranch);
            }
        } else if (action.getType().equals("set")) {
            checkEntityAndPropertyExist(rule, simulationDetails, action);
            getSetDetails(result, action, simulationDetails);
        } else if (action.getType().equals("replace")) {
            getReplaceDetails(result, action, simulationDetails);
        } else if (action.getType().equals("proximity")) {
            getProximityDetails(rule, result, action, simulationDetails);
        } else if (action.getType().equals("calculation")) {
            checkEntityExist(rule, simulationDetails, action);
            if(!simulationDetails.getEntityPropertiesSimulationMap().get(action.getEntity()).contains(action.getResultProp())) {
                throw new IllegalArgumentException("Property with name '" + action.getResultProp() +
                        "' is not present in entity " + action.getEntity());
            }
            getCalculationDetails(result, action, simulationDetails);
        }
        else {
            throw new IllegalArgumentException("This action type '" + action.getType() + "'does not supported!");
        }
        return result;
    }

    private static void getCalculationDetails(TreeItem<String> actionDetails, PRDAction action, SimulationDetails simulationDetails) {
        TreeItem<String> typeBranch = new TreeItem<>("Type: " + action.getType());
        TreeItem<String> entityBranch = new TreeItem<>("Entity: " + action.getEntity());
        TreeItem<String> propertyBranch = new TreeItem<>("Result Property: " + action.getResultProp());

        TreeItem<String> mathOperationBranch = new TreeItem<>("Math Operation");
        if (action.getPRDMultiply() != null) {

            TreeItem<String> operationBranch = new TreeItem<>("Operation: Multiply");
            TreeItem<String> arg1Branch = new TreeItem<>("Argument 1: " + action.getPRDMultiply().getArg1());
            arg1Branch.getChildren().addAll(getByExpressionTreeItemList(action.getPRDMultiply().getArg1(), simulationDetails));
            TreeItem<String> arg2Branch = new TreeItem<>("Argument 2: " + action.getPRDMultiply().getArg2());
            arg2Branch.getChildren().addAll(getByExpressionTreeItemList(action.getPRDMultiply().getArg2(), simulationDetails));
            mathOperationBranch.getChildren().addAll(operationBranch, arg1Branch, arg2Branch);

        }   else if (action.getPRDDivide() != null) {

            TreeItem<String> operationBranch = new TreeItem<>("Operation: Divide");
            TreeItem<String> arg1Branch = new TreeItem<>("Argument 1: " + action.getPRDDivide().getArg1());
            arg1Branch.getChildren().addAll(getByExpressionTreeItemList(action.getPRDDivide().getArg1(), simulationDetails));
            TreeItem<String> arg2Branch = new TreeItem<>("Argument 2: " + action.getPRDDivide().getArg2());
            arg2Branch.getChildren().addAll(getByExpressionTreeItemList(action.getPRDDivide().getArg2(), simulationDetails));
            mathOperationBranch.getChildren().addAll(operationBranch, arg1Branch, arg2Branch);
        }


        actionDetails.getChildren().addAll(typeBranch, entityBranch, propertyBranch, mathOperationBranch);
    }

    // TODO: use this ref for the init expression
    private static List<TreeItem<String>> getByExpressionTreeItemList(String input, SimulationDetails simulationDetails) {
        List<TreeItem<String>> result = new ArrayList<>();

        int openParenthesisIndex = input.indexOf('(');
        if (openParenthesisIndex != -1 && isFunction(input.substring(0, openParenthesisIndex))) { // function
            String functionString = extractSubstring(input);
            TreeItem<String> functionBranch = new TreeItem<>("Function: " + functionString);
            if (functionString.equals("percent")) {
                String[] params = extractParams(input);
                List<TreeItem<String>> functionParam1 = getByExpressionTreeItemList(params[0] ,simulationDetails);
                List<TreeItem<String>> functionParam2 = getByExpressionTreeItemList(params[1] ,simulationDetails);
                functionBranch.getChildren().addAll(functionParam1);
                functionBranch.getChildren().addAll(functionParam2);
            } else {
                List<TreeItem<String>> functionParam = getByExpressionTreeItemList(input.substring(openParenthesisIndex + 1,
                        input.length() - 1) ,simulationDetails);
                functionBranch.getChildren().addAll(functionParam);
            }
            result.add(functionBranch);
        } else if (isEnvironmentProperty(input, simulationDetails)) {
            result.add(new TreeItem<>("Environment Property: " + input));
        } else if (isPropertyType(input, simulationDetails))  {
            result.add(getPropertyType(input));
        } else { // Another
            if (isNumeric(input)) {
                result.add(new TreeItem<>("Number: " + input));
            }
            else if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                result.add(new TreeItem<>("Boolean: " + input));
            }
            else {
                result.add(new TreeItem<>("String: " + input));
            }
        }
        return result;
    }

    public static boolean isNumeric(String str) {
        return str.chars().allMatch(Character::isDigit);
    }

    private static TreeItem<String> getPropertyType(String input) {
        TreeItem<String> result = new TreeItem<>("Property");

        String[] params = input.split("\\.");

        if (params.length == 2) {
            TreeItem<String> entity = new TreeItem<>("Entity: " + params[0].trim());
            TreeItem<String> property = new TreeItem<>("Property: " + params[1].trim());
            result.getChildren().addAll(entity, property);
        } else { // check all the properties in this world
            TreeItem<String> property = new TreeItem<>("Property: " + input);
            result.getChildren().add(property);
        }
        return result;
    }

    private static boolean isEnvironmentProperty(String input, SimulationDetails simulationDetails) {
        return simulationDetails.getEnvironments().containsKey(input);
    }

    // TODO: use this ref function to exact this params

    private static String[] extractParams(String input) {
        String[] params = new String[2];

        int start = input.indexOf('(');
        int end = input.lastIndexOf(')');

        if (start != -1 && end != -1 && start < end) {
            String innerContent = input.substring(start + 1, end);
            int commaIndex = findCommaIndex(innerContent);

            if (commaIndex != -1) {
                params[0] = innerContent.substring(0, commaIndex).trim();
                params[1] = innerContent.substring(commaIndex + 1).trim();
            }
        }

        return params;
    }

    private static int findCommaIndex(String content) {
        int parenthesesLevel = 0;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (c == '(') {
                parenthesesLevel++;
            } else if (c == ')') {
                parenthesesLevel--;
            } else if (c == ',' && parenthesesLevel == 0) {
                return i;
            }
        }

        return -1;
    }

    private static boolean isPropertyType(String input, SimulationDetails simulationDetails) {
        String[] params = input.split("\\.");

        if (params.length == 2) {
            String entity = params[0].trim();
            if (simulationDetails.getEntityPropertiesSimulationMap().containsKey(entity)) {
                String property = params[1].trim();
                return simulationDetails.getEntityPropertiesSimulationMap().get(entity).contains(property);
            }
        } else { // check all the properties in this world
            return simulationDetails.getEntityPropertiesSimulationMap().values().stream()
                    .flatMap(List::stream)
                    .anyMatch(property -> property.equals(input));
        }
        return false;
    }

    private static List<TreeItem<String>> makeConditionsListTree(PRDCondition prdCondition, SimulationDetails simulationDetails) {
        List<TreeItem<String>> result = new ArrayList<>();

        if (prdCondition.getPRDCondition().isEmpty()) {
            TreeItem<String> condition = getConditionDetails(prdCondition, simulationDetails);
            result.add(condition);
        } else {
            for (PRDCondition cond : prdCondition.getPRDCondition()) {
                TreeItem<String> condition = getConditionDetails(cond, simulationDetails);
                if (cond.getSingularity().equals("multiple")) {
                    List<TreeItem<String>> subConditionItems = makeConditionsListTree(cond, simulationDetails);
                    condition.getChildren().addAll(subConditionItems); // Add sub-conditions to the current condition
                }
                result.add(condition);
            }
        }
        return result;
    }

    private static TreeItem<String> getConditionDetails(PRDCondition prdCondition, SimulationDetails simulationDetails) {
        TreeItem<String> rootItem = new TreeItem<>("Condition");
        TreeItem<String> typeBranch = new TreeItem<>("Type: Condition");
        TreeItem<String> singularityBranch = new TreeItem<>("Singularity: " + prdCondition.getSingularity());

        if (prdCondition.getSingularity().equals("multiple")) {
            TreeItem<String> logicalBranch = new TreeItem<>("Logical: " + prdCondition.getLogical());
            rootItem.getChildren().addAll(typeBranch, singularityBranch, logicalBranch);
            return rootItem;
        } else if (prdCondition.getSingularity().equals("single")) {
            TreeItem<String> entityBranch = new TreeItem<>("Entity: " + prdCondition.getEntity());
            TreeItem<String> propertyBranch = new TreeItem<>("Property: " + prdCondition.getProperty());
            propertyBranch.getChildren().addAll(getByExpressionTreeItemList(prdCondition.getProperty(), simulationDetails));
            TreeItem<String> valueBranch = new TreeItem<>("Value: " + prdCondition.getValue());
            valueBranch.getChildren().addAll(getByExpressionTreeItemList(prdCondition.getValue(), simulationDetails));
            TreeItem<String> operatorBranch = new TreeItem<>("Operator: " + prdCondition.getOperator());
            rootItem.getChildren().addAll(typeBranch, entityBranch, propertyBranch, valueBranch,
                    operatorBranch, singularityBranch);
            return rootItem;
        } else {
            throw new IllegalArgumentException("This Singularity does not supported: " + prdCondition.getSingularity());
        }

    }

    private static List<TreeItem<String>> initTreeItemList(PRDRule rule, PRDAction prdAction, SimulationDetails simulationDetails, List<PRDAction> predActionList){
        List<TreeItem<String>> resArrayList = null;
        final int[] actionsCounter = {0};

        if (predActionList != null) {
            resArrayList = new ArrayList<>();

            for(PRDAction action : predActionList) {
                actionsCounter[0]++;
                TreeItem<String> actionDetails = new TreeItem<>("Action " + actionsCounter[0]);
                if(action.getType().equals("increase") || action.getType().equals("decrease")) {

                    getIncreaseDecreaseDetails(actionDetails ,action, simulationDetails);
                }
                else if(action.getType().equals("set")) {
                    getSetDetails(actionDetails, action, simulationDetails);
                }
                else if(action.getType().equals("kill")) {
                    getKillDetails(actionDetails, action);
                }
                else if (action.getType().equals("replace")) {
                    getReplaceDetails(actionDetails, action, simulationDetails);

                } else if (action.getType().equals("proximity")) {
                    getProximityDetails(rule, actionDetails, action, simulationDetails);
                } else if (action.getType().equals("calculation")) {
                    getCalculationDetails(actionDetails, action, simulationDetails);
                }
                resArrayList.add(actionDetails);
            }
        }
        return  resArrayList;
    }

    private static void checkEntityAndPropertyExist(PRDRule rule, SimulationDetails simulationDetails, PRDAction action) {
        checkEntityExist(rule, simulationDetails, action);

        if(!simulationDetails.getEntityPropertiesSimulationMap().get(action.getEntity()).contains(action.getProperty())) {
            throw new IllegalArgumentException("Property with name '" + action.getProperty() +
                    "' is not present in entity " + action.getEntity());
        }
    }

    private static void checkEntityExist(PRDRule rule, SimulationDetails simulationDetails, PRDAction action) {
        if (!simulationDetails.getEntityPropertiesSimulationMap().containsKey(action.getEntity())) {
            throw new IllegalArgumentException("In rule " + rule.getName() +" you are referring to entity "
                    + action.getEntity() +" and this entity is not exist!");
        }
    }

    private static void getProximityDetails(PRDRule rule, TreeItem<String> actionDetails, PRDAction action, SimulationDetails simulationDetails) {
        TreeItem<String> typeBranch = new TreeItem<>("Type: " + action.getType());

        //check is the entities occur in the map
        if (!simulationDetails.getEntityPropertiesSimulationMap().containsKey(action.getPRDBetween().getSourceEntity())
                || !simulationDetails.getEntityPropertiesSimulationMap().containsKey(action.getPRDBetween().getTargetEntity())) {
            throw new IllegalArgumentException("In the rule 'replace' - one entity does not exist!\n" +
                    "Source-Entity: " + action.getPRDBetween().getSourceEntity() +
                    "\nTarget-Entity: " + action.getPRDBetween().getTargetEntity());
        }
        TreeItem<String> sourceEntityBranch = new TreeItem<>("Source-Entity: " + action.getPRDBetween().getSourceEntity());
        TreeItem<String> targetEntityBranch = new TreeItem<>("Target-Entity: " + action.getPRDBetween().getTargetEntity());
        TreeItem<String> ofBranch = new TreeItem<>("Of: " + action.getPRDEnvDepth().getOf());
        actionDetails.getChildren().addAll(typeBranch, sourceEntityBranch, targetEntityBranch, ofBranch);

        if (action.getPRDActions().getPRDAction() != null) {
            TreeItem<String> actionsBranch = new TreeItem<>("Actions");
            final int[] actionsCounter = {0};
            action.getPRDActions().getPRDAction().forEach(act -> {
                actionsCounter[0]++;
                TreeItem<String> actionBranch = getActionBranch(rule, simulationDetails, act, actionsCounter[0]);
                actionsBranch.getChildren().addAll(actionBranch);
            });
            actionDetails.getChildren().addAll(actionsBranch);
        }

    }

    private static void getReplaceDetails(TreeItem<String> actionDetails, PRDAction action, SimulationDetails simulationDetails) {
        TreeItem<String> typeBranch = new TreeItem<>("Type: " + action.getType());
        TreeItem<String> killBranch = new TreeItem<>("Kill: " + action.getKill());

        if (!simulationDetails.getEntityPropertiesSimulationMap().containsKey(action.getKill())) {
            throw new IllegalArgumentException("You cant kill this entity: " + action.getKill() + ".\nThis entity does not exist!");
        }

        TreeItem<String> createBranch = new TreeItem<>("Create: " + action.getCreate());
        TreeItem<String> modeBranch = new TreeItem<>("Mode: " + action.getMode());
        actionDetails.getChildren().addAll(typeBranch, killBranch, createBranch, modeBranch);
    }

    private static void getSetDetails(TreeItem<String> actionDetails, PRDAction action, SimulationDetails simulationDetails) {
        TreeItem<String> typeBranch = new TreeItem<>("Type: " + action.getType());
        TreeItem<String> entityBranch = new TreeItem<>("Entity: " + action.getEntity());
        TreeItem<String> propertyBranch = new TreeItem<>("Property: " + action.getProperty());
        TreeItem<String> valueBranch = new TreeItem<>("Value: " + action.getValue());
        valueBranch.getChildren().addAll(getByExpressionTreeItemList(action.getValue(), simulationDetails));
        actionDetails.getChildren().addAll(typeBranch, entityBranch, propertyBranch, valueBranch);
    }

    private static void getKillDetails(TreeItem<String> actionDetails, PRDAction action) {
        TreeItem<String> typeBranch = new TreeItem<>("Type: " + action.getType());
        TreeItem<String> entityBranch = new TreeItem<>("Entity: " + action.getEntity());
        actionDetails.getChildren().addAll(typeBranch, entityBranch);
    }

    private static TreeItem<String> getActivationRule(PRDRule rule) {
        TreeItem<String> activationBranch = new TreeItem<>("Activation");
        TreeItem<String> ticksBranch = new TreeItem<>("Ticks: " + getTicks(rule.getPRDActivation()));
        TreeItem<String> probBranch = new TreeItem<>("Probability: " + getProbability(rule.getPRDActivation()));
        activationBranch.getChildren().addAll(ticksBranch, probBranch);
        return activationBranch;
    }

    private static void getIncreaseDecreaseDetails(TreeItem<String> actionDetails, PRDAction action, SimulationDetails simulationDetails) {
        TreeItem<String> typeBranch = new TreeItem<>("Type: " + action.getType());
        TreeItem<String> entityBranch = new TreeItem<>("Entity: " + action.getEntity());
        TreeItem<String> propertyBranch = new TreeItem<>("Property: " + action.getProperty());
        TreeItem<String> byExpression = new TreeItem<>("By expression: " + action.getBy());
        byExpression.getChildren().addAll(getByExpressionTreeItemList(action.getBy(), simulationDetails));
        actionDetails.getChildren().addAll(typeBranch, entityBranch, propertyBranch, byExpression);
    }
}
