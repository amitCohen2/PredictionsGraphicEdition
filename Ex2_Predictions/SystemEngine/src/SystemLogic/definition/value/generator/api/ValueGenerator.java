package SystemLogic.definition.value.generator.api;

import java.io.Serializable;

public interface ValueGenerator<T> extends Serializable {
    T generateValue();
}
