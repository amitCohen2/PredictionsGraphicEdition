package SystemLogic.rule;

import SystemLogic.action.api.Action;

import java.util.ArrayList;
import java.util.List;

public class RuleImpl implements Rule { //Activation

    private final String name;
    private Activation activation;
    private final List<Action> actions;

    private Integer ticks;
    private Double probabillty;

    public RuleImpl(String name) {

        this.name = name;
        actions = new ArrayList<>();
        this.ticks = null;
        this.probabillty = null;

    }
    @Override
    public void setActivation(Integer tickes, Double probability){this.ticks =tickes;this.probabillty =probability;}

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Action> getActionsToPerform() {
        return actions;
    }

    @Override
    public void addAction(Action action) {
        actions.add(action);
    }
    @Override
    public boolean isActive(int ticks, Double probabillty) {
        if (this.probabillty == null) {
            return ticks == this.ticks;
        }
        if(this.ticks ==1){
            return  probabillty < this.probabillty;
        }else {
            return 0 == (ticks%this.ticks) && probabillty < this.probabillty;
        }
    }
}
