package SystemLogic.Expression.Imp;

import SystemLogic.Expression.api.AbstractExpression;
import SystemLogic.Expression.api.enums.ExpressionType;
import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.definition.property.api.PropertyDefinition;
import SystemLogic.execution.context.Context;

public class PropertyExpression extends AbstractExpression {
    private final PropertyDefinition propertyInstance;

    public PropertyExpression(ExpressionType expressionType, EntityDefinition entityDefinition,
                              PropertyDefinition propertyInstance, String ExpressionString) {
        super(expressionType, entityDefinition,  ExpressionString);
        this.propertyInstance = propertyInstance;
    }

    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.PROPERTY;
    }

    @Override
    public Object getExpressionValue(Context context) {
        return context.getPrimaryEntityInstance().getPropertyByName(propertyInstance.getName()).getValue();
    }
}
