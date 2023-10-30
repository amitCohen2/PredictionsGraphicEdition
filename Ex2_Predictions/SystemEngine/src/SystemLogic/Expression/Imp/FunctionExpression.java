package SystemLogic.Expression.Imp;

import SystemLogic.Expression.api.AbstractExpression;
import SystemLogic.Expression.api.Expression;
import SystemLogic.Expression.api.Function;
import SystemLogic.Expression.exceptions.DivideByZeroException;
import SystemLogic.Expression.exceptions.FunctionNotOccurException;
import SystemLogic.Expression.exceptions.NoFunctionExpressionsException;
import SystemLogic.Expression.exceptions.TooManyExpressionsException;
import SystemLogic.Expression.api.enums.ExpressionType;
import SystemLogic.Expression.api.enums.FunctionType;

import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.definition.environment.impl.EnvVariableManagerImpl;
import SystemLogic.definition.property.api.PropertyDefinition;
import SystemLogic.execution.context.Context;
import SystemLogic.execution.instance.enitty.EntityInstance;
import SystemLogic.execution.instance.environment.impl.ActiveEnvironmentImpl;
import SystemLogic.execution.instance.property.PropertyInstance;
import javafx.beans.binding.StringExpression;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionExpression extends AbstractExpression implements Function {

    private final String functionName;
    private final FunctionType functionType;
    private final List<Expression> expressions;

    private ActiveEnvironmentImpl activeEnvironment;
    public FunctionExpression(ExpressionType expressionType, EntityDefinition entityDefinition,
                              String functionName, FunctionType functionType, List<Expression> expressions, String StringExpression) {
        super(expressionType, entityDefinition, StringExpression);
        this.functionName = functionName;
        this.functionType = functionType;
        this.expressions = new ArrayList<>(expressions);


    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public void addExpression(Expression expression) {
        expressions.add(expression);
    }

    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.FUNCTION;
    }


    @Override
    public Object getExpressionValue(Context context) throws NoFunctionExpressionsException, FunctionNotOccurException, TooManyExpressionsException , DivideByZeroException{
        if (expressions.size() == 0) {
            throw new NoFunctionExpressionsException();
        }

        switch (functionType) {
            case ENVIRONMENT:
                return environment(expressions.get(0).getExpressionString(), context);
            case RANDOM:
                return random(expressions.get(0).getExpressionString(), context);
            case EVALUATE:
                return evaluate(expressions.get(0).getExpressionString(), context);
            case PERCENT:
                return precent(expressions.get(0).getExpressionString(), context);
            case TICKS:
                return ticks(expressions.get(0).getExpressionString(),context);
            default:
                throw new FunctionNotOccurException();
        }
    }
    @Override
    public Object evaluate(String StringExpression, Context context) throws
            TooManyExpressionsException, FunctionNotOccurException, NoFunctionExpressionsException {
        String[] parts = StringExpression.split("\\.");

        String entity =parts[0];
        String prop = parts[1];
        String dot = ".";
        if(!context.getPrimaryEntityInstance().getEntityDefinition().getName().equals(entity) && context.getPrimaryEntityInstance().getSeconderyEntityInstance() !=null &&!context.getPrimaryEntityInstance().getSeconderyEntityInstance().getEntityDefinition().getName().equals(entity)){
            throw new FunctionNotOccurException(entity+dot+prop);}
        if(context.getPrimaryEntityInstance().getSeconderyEntityInstance().getEntityDefinition().getName().equals(entity)){
            return context.getPrimaryEntityInstance().getSeconderyEntityInstance().getPropertyByName(prop).getValue();
        }
        else{
            return context.getPrimaryEntityInstance().getPropertyByName(prop).getValue();
        }

        }
@Override
 public double precent(String StringExpression, Context context) throws
         TooManyExpressionsException, FunctionNotOccurException, NoFunctionExpressionsException,  DivideByZeroException{
     String[] parts = splitString(StringExpression);
        double value =0;
    double[] args = new double[2];
    int idx =0;
     for (String part : parts) {
         String[] expressionParts = splitString(part);
         for(String expressionPart : expressionParts){
             String[] functionParts = splitFunctionString(expressionPart);
             if(functionParts.length ==2){
                 String functionName = functionParts[0];
                 String varaible =functionParts[1];
                 args[idx]= sendToFunction(functionName, varaible,context);
             }
             else if(functionParts.length ==1){
                 try {
                     // Try to parse the input as a number (integer or floating-point)

                     double number = Double.parseDouble(functionParts[0]);

                     // If parsing succeeds, return the number as a string
                     args[idx]=number;
                 } catch (NumberFormatException e) {
                     // If parsing fails, return the input string as is
                     throw new NumberFormatException();
                 }
             }

         }
        idx++;
         if(idx ==2){
             break;
         }

     }
     if(args[1] ==0){
         throw new DivideByZeroException();
     }
     return args[0]/args[1];
 }

 double sendToFunction(String functionName , String varaible,Context context) throws NoFunctionExpressionsException, FunctionNotOccurException, TooManyExpressionsException, DivideByZeroException{
        double res =0;
        if(functionName.equals("random")){
            res+= random(varaible,context);
        }
        else if (functionName.equals("evaluate")){
         res+=(double)  evaluate(varaible,context);
     }
        else if  (functionName.equals("precent")){
         res+= precent(varaible,context);
     }
        else if (functionName.equals("ticks")){
         res+=ticks(varaible,context);
     }
        else if(functionName.equals("environment")){
         res += (double) environment(varaible,context);
     }
        return res;
 }
    public static String[] splitFunctionString(String input) {
        // Find the index of the opening and closing parentheses
        int openParenIndex = input.indexOf("(");
        int closeParenIndex = input.indexOf(")");

        if (openParenIndex != -1 && closeParenIndex != -1) {
            // Extract the part before the opening parenthesis
            String firstPart = input.substring(0, openParenIndex).trim();

            // Extract the part inside the parentheses
            String secondPart = input.substring(openParenIndex + 1, closeParenIndex).trim();

            String[] parts = { firstPart, secondPart };
            return parts;
        } else {
            // If the string does not contain parentheses, return the whole string as the first part
            String[] parts = { input };
            return parts;
        }
    }

    public static String[] splitString(String input) {
        // Define a regular expression pattern to match the desired format
        String pattern = "\\w+\\([^)]+\\)";

        // Create a Pattern object
        Pattern regexPattern = Pattern.compile(pattern);

        // Use Matcher to find all matches of the pattern in the input string
        Matcher matcher = regexPattern.matcher(input);

        // Initialize a StringBuilder to store the matched substrings
        StringBuilder builder = new StringBuilder();

        while (matcher.find()) {
            // Append the matched substring to the StringBuilder
            builder.append(matcher.group()).append(" ");
        }

        // Convert the StringBuilder to a string and split it by spaces to get individual parts
        String[] parts = builder.toString().trim().split(" ");
        return parts;
    }

    @Override
    public Object environment(String StringExpression, Context context) throws
            TooManyExpressionsException, FunctionNotOccurException, NoFunctionExpressionsException {
        if (expressions.size() > 1) {
            throw new TooManyExpressionsException();
        }

        return context.getEnvironmentVariable(StringExpression).getValue();

//        EnvVariableManagerImpl envVariablesManager = EnvVariableManagerImpl.getInstance();
//        return envVariablesManager.getEnvironmentValues().get(expression.getExpressionValue(context));
    }
    public int ticks(String stringExpression, Context context) throws
            TooManyExpressionsException, FunctionNotOccurException, NoFunctionExpressionsException {
        String[] parts = stringExpression.split("\\.");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid StringExpression format. It should be in the format 'entity.property'.");
        }

        String entityName = parts[0];
        String propName = parts[1];

        EntityInstance primaryEntityInstance = context.getPrimaryEntityInstance();
        Map<PropertyInstance, Integer> ticksNotChangeMap = primaryEntityInstance.getTicksNotChangeMap();

        // Find the PropertyInstance with the given propName
        for (Map.Entry<PropertyInstance, Integer> entry : ticksNotChangeMap.entrySet()) {
            PropertyInstance propertyInstance = entry.getKey();
            if (propertyInstance.getPropertyDefinition().getName().equals(propName)) {
                return entry.getValue(); // Return the tick count for the property
            }
        }

        // If the property was not found, you might want to handle this case accordingly
        throw new IllegalArgumentException("Property not found: " + propName);
    }

    @Override
    public int random(String StringExpression, Context context) throws TooManyExpressionsException, FunctionNotOccurException, NoFunctionExpressionsException {
        // Attempt to parse the input as an integer
        try {
            Integer num =Integer.parseInt(StringExpression);
            if(num <0) {
                throw new IllegalArgumentException("Invalid input format. random number must be bigger then 0.");
            }
            Random rand = new Random();
            return rand.nextInt(num + 1);

        } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid input format. Expected format: random(n), where n is an integer.");
        }

    }

}

