package SystemLogic.rule.api;

import SystemLogic.action.api.Action;

import java.io.Serializable;
import java.util.List;

public interface Rule extends Serializable {
    String getName();
    //Activation getActivation();
    boolean isActive(int tickNumber,Double probability);



    void setActivation(Integer tickes, Double probability);
    List<Action> getActionsToPerform();
    void addAction(Action action);
}
