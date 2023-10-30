package SystemLogic.termination.imp;

import java.io.Serializable;

public class TerminationImpl implements Serializable {
    Integer TerminationBySec;
    Integer TerminationByTicks;
    boolean TerminationByUser;

    public TerminationImpl(Integer TerminationBySec,Integer   TerminationByTicks, boolean TerminationByUser )
    {
        this.TerminationBySec =TerminationBySec;
        this.TerminationByTicks = TerminationByTicks;
        this.TerminationByUser = TerminationByUser;
    }
    public Integer getNumOfSec() {return TerminationBySec;}

    public Integer getNumOfTicks() {return TerminationByTicks;}
    public void setTerminationBySec(Integer TerminationBySec){
        this.TerminationBySec= TerminationBySec;
    }
     public void setTerminationByTicks(Integer TerminationByTicks){
        this.TerminationByTicks= TerminationByTicks;
    }
    public void setTerminationByUser(Boolean ter){
        this.TerminationByUser = ter;
    }
    public boolean getTerminationByUser(){
        return TerminationByUser;
    }
}
