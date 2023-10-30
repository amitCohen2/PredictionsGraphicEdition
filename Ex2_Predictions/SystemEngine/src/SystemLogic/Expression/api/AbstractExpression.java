package SystemLogic.Expression.api;

import SystemLogic.Expression.api.enums.ExpressionType;
import SystemLogic.definition.entity.EntityDefinition;

public abstract class AbstractExpression implements Expression {
    private final ExpressionType expressionType;
    private final EntityDefinition entityDefinition;

    private  String ExpressionString;

    protected AbstractExpression(ExpressionType expressionType, EntityDefinition entityDefinition,String ExpressionString) {
        this.expressionType = expressionType;
        this.entityDefinition = entityDefinition;
        this.ExpressionString =ExpressionString;
    }
    @Override
    public ExpressionType getExpressionType() {
        return expressionType;
    }

    @Override
    public String  getExpressionString(){
        return ExpressionString;
    }

}
