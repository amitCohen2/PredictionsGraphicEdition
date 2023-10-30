package SystemLogic.action.api;

import SystemLogic.Expression.Imp.AnotherExpression;
import SystemLogic.Expression.Imp.FunctionExpression;
import SystemLogic.Expression.Imp.PropertyExpression;
import SystemLogic.Expression.api.enums.ExpressionType;
import SystemLogic.Expression.api.enums.FunctionType;
import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.Expression.api.Expression;
import SystemLogic.definition.property.api.PropertyDefinition;
import SystemLogic.definition.property.api.PropertyType;
import SystemLogic.definition.property.impl.BooleanPropertyDefinition;
import SystemLogic.definition.property.impl.FloatPropertyDefinition;
import SystemLogic.definition.property.impl.StringPropertyDefinition;
import SystemLogic.definition.value.generator.api.ValueGeneratorFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public abstract class AbstractAction implements Action {

    private final ActionType actionType;
    private final EntityDefinition entityDefinition;
    private final String input;
    private final Expression expression;


    protected AbstractAction(ActionType actionType, EntityDefinition entityDefinition, String input) {
        this.actionType = actionType;
        this.entityDefinition = entityDefinition;
        this.input = input;
        this.expression = initExpression(input);
    }

    public Expression getExpression() {
        return expression;
    }


    // TODO: fix this algorithm - you have reference in TreeViewDetails - also in TODO list
    public Expression initExpression(String input) {
        ExpressionType expressionType = findExpressionType(input);

        if (expressionType.equals(ExpressionType.FUNCTION)) {
            int openParenthesisIndex = input.indexOf('(');
            String functionName = input.substring(0, openParenthesisIndex);
            FunctionType currFunctionType = findFunctionType(functionName);
            List<Expression> params = findFunctionParams(input);
            return new FunctionExpression(expressionType, entityDefinition, functionName, currFunctionType, params,input);
        }
        else if (expressionType.equals(ExpressionType.PROPERTY)) {
            PropertyDefinition propertyDefinition = findProperty(input);
            return new PropertyExpression(expressionType, entityDefinition, propertyDefinition,input);
        }
        else { // ANOTHER
            PropertyDefinition propertyDefinition = null;
            PropertyType propertyType = findPropertyType(input);
            switch (propertyType) {
                case BOOLEAN:
                    propertyDefinition = new BooleanPropertyDefinition(input,
                            ValueGeneratorFactory.createFixed(Boolean.parseBoolean(input)));
                    break;
                case FLOAT:
                    propertyDefinition = new FloatPropertyDefinition(input,
                            ValueGeneratorFactory.createFixed(Double.parseDouble(input)), null, null);
                    break;
                case STRING:
                    propertyDefinition = new StringPropertyDefinition(input, ValueGeneratorFactory.createFixed(input));
                    break;
                case DECIMAL:
                    propertyDefinition=  new FloatPropertyDefinition(input,
                        ValueGeneratorFactory.createFixed(Double.parseDouble(input)), null, null);
                    break;
            }
            return new AnotherExpression(expressionType, entityDefinition, propertyDefinition,input);
        }
    }

    private PropertyType findPropertyType(String input) {
        if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
            return PropertyType.BOOLEAN;
        }
        try {
            Float.parseFloat(input);
            return PropertyType.FLOAT;
        } catch (NumberFormatException e) {
            return PropertyType.STRING;
        }
    }

    private PropertyDefinition findProperty(String input) {
        PropertyDefinition result = null;

        for (PropertyDefinition property : entityDefinition.getProps()) {
            if (Objects.equals(property.getName(), input)) {
                result = property;
                break;
            }
        }
        return result;
    }

    private List<Expression> findFunctionParams(String input) {
        List<Expression> params = new ArrayList<>();
        String paramsString = null;
        try {
            paramsString = input.substring(input.indexOf("(") + 1, input.lastIndexOf(")"));
        } catch (IndexOutOfBoundsException e) {
           throw new RuntimeException("Invalid input function parameter format: " + input);
        }

        String[] paramsArray = paramsString.split(", ");
        for (String str : paramsArray) {
            Expression tmpExpression = initExpression(str);
            params.add(tmpExpression);
        }
        return params;
    }

    private ExpressionType findExpressionType(String byExpression) {

        int openParenthesisIndex = byExpression.indexOf('(');

        if (openParenthesisIndex != -1 && isFunction(byExpression.substring(0, openParenthesisIndex))) {
            return ExpressionType.FUNCTION;
        }
        else if(isPropertyType(byExpression)) {
            return ExpressionType.PROPERTY;
        }
        else {
            return ExpressionType.ANOTHER;
        }
    }

    private boolean isPropertyType(String byExpression) {
        boolean result = false;

        for (PropertyDefinition property : entityDefinition.getProps()) {
            if (Objects.equals(property.getName(), byExpression)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean isFunction(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        for (FunctionType func : FunctionType.values()) {
            if (func.name().equals(input.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    public FunctionType findFunctionType(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        for (FunctionType func : FunctionType.values()) {
            if (func.name().equals(input.toUpperCase())) {
                return func;
            }
        }
        return null;
    }

    @Override
    public ActionType getActionType() {
        return actionType;
    }

    @Override
    public EntityDefinition getContextEntity() {
        return entityDefinition;
    }
}
