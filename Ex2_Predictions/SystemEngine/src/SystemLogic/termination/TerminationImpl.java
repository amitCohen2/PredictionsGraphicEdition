package SystemLogic.termination;

import java.io.Serializable;

public class TerminationImpl implements Serializable {
    Integer TerminationBySec;
    Integer TerminationByTicks;

    boolean TerminationByUser;

    public TerminationImpl(Integer TerminationBySec,Integer   TerminationByTicks,Boolean terminationByUser )
    {
        this.TerminationBySec =TerminationBySec;
        this.TerminationByTicks = TerminationByTicks;
        this.TerminationByUser = terminationByUser;
    }
    public  boolean getTerminationByUser(){
        return TerminationByUser;
    }
    public void  setTerminationByUser(Boolean termination){
        TerminationByUser = termination;
    }
    public Integer getNumOfSec() {return TerminationBySec;}

    public Integer getNumOfTicks() {return TerminationByTicks;}
    public void setTerminationBySec(Integer TerminationBySec){
        this.TerminationBySec= TerminationBySec;
    }
     public void setTerminationByTicks(Integer TerminationByTicks){
        this.TerminationByTicks= TerminationByTicks;
    }
}
