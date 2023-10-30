package SystemLogic.action.impl.condition.api;

import SystemLogic.action.api.Action;
import SystemLogic.Expression.api.Expression;
import SystemLogic.execution.instance.enitty.EntityInstance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Condition  implements Serializable {
    private String entityName;
    private String byExpression;
    private String propertyName;
    private String operatorName;
    private String singularity;
    private String logical;
    private ArrayList<Action> thenActions;
    private ArrayList<Action> elseActions;

    private OperationType operationType;

    private ArrayList<Condition> conditions;

    // Constructor
    public Condition(String entityName , String propertyName, String byExpression,
                     String operatorName, String singularity, String logical ,
                     ArrayList<Action> thenActions, ArrayList<Action> elseActions, ArrayList<Condition> conditions) {
        this.entityName = entityName;
        this.byExpression = byExpression;
        this.propertyName = propertyName;
        this.operatorName = operatorName;
        this.operationType = initOperationType();
        this.singularity = singularity;
        this.logical = logical;
        this.conditions = conditions;
        this.thenActions = thenActions;
        this.elseActions = elseActions;
    }

    private OperationType initOperationType() {
        if(operatorName == null){
            return null;
        }

        if (operatorName.equals("=")) {
            return OperationType.EQUAL;
        }
        else if (operatorName.equals("!=")) {
            return OperationType.NOT_EQUAL;
        }
        else if (operatorName.equals("bt")) {
            return OperationType.BT;
        }
        else if (operatorName.equals("lt")) {
            return OperationType.LT;
        }

        else {
            throw new IllegalArgumentException("Unsupported operation type");
        }
    }

    // Getters and Setters
    public ArrayList<Condition> getConditions() {
        return conditions;
    }
    public OperationType getOperationType() {
        return operationType;
    }

    public ArrayList<Action> getThenActions() {
        return thenActions;
    }

    public ArrayList<Action> getElseActions() {
        return elseActions;
    }

    public void setAction(ArrayList<Action> thenActions) {
        this.thenActions = thenActions;
    }

    public void setConditions(ArrayList<Condition> conditions) {
        this.conditions = conditions;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String gerByExpression() {
        return byExpression;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getSingularity() {
        return singularity;
    }

    public boolean isSingle() {
        return singularity.equals("single");
    }

    public void setSingularity(String singularity) {
        this.singularity = singularity;
    }

    public String getLogical() {
        return logical;
    }

    public void setLogical(String logical) {
        this.logical = logical;
    }


}