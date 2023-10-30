package SystemLogic.rule;

import java.io.Serializable;

public interface Activation extends Serializable {
    boolean isActive(int tickNumber);
}
