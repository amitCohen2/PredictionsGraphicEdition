package SystemLogic.Expression.Imp;

import SystemLogic.Expression.api.AbstractExpression;
import SystemLogic.Expression.api.enums.ExpressionType;
import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.definition.property.api.PropertyDefinition;
import SystemLogic.execution.context.Context;

public class AnotherExpression extends AbstractExpression {

    private final PropertyDefinition anotherInstance;
    private String ExpressionName;


    public AnotherExpression(ExpressionType expressionType, EntityDefinition entityDefinition,
                             PropertyDefinition anotherInstance,String ExpressionName) {
        super(expressionType, entityDefinition,ExpressionName);
        this.anotherInstance = anotherInstance;
        this.ExpressionName=ExpressionName;
    }


    public String getExpressionName(){
        return ExpressionName;
    }
    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.ANOTHER;
    }


    @Override
    public Object getExpressionValue(Context context) {
        return anotherInstance.generateValue();
    }

    public PropertyDefinition getAnotherInstance(){
        return anotherInstance;
    }
}
