package Menu;
import SystemLogic.history.imp.HistoryArchive;
import XmlRunner.XmlRunner;

import java.io.Serializable;

public class MenuData implements Serializable {
    private String pathXmlFile;
    private int simulationsCounter;
    private HistoryArchive historyArchive;


    public MenuData(String pathXmlFile, HistoryArchive historyArchive) {
        this.pathXmlFile = pathXmlFile;
        this.historyArchive = historyArchive;
    }

    public String getPathXmlFile() {
        return pathXmlFile;
    }

    public void setPathXmlFile(String pathXmlFile) {
        this.pathXmlFile = pathXmlFile;
    }

    public int getSimulationsCounter() {
        return simulationsCounter;
    }

    public void setSimulationsCounter(int simulationsCounter) {
        this.simulationsCounter = simulationsCounter;
    }

    public HistoryArchive getHistoryArchive() {
        return historyArchive;
    }

    public void setHistoryArchive(HistoryArchive historyArchive) {
        this.historyArchive = historyArchive;
    }




}