package SystemLogic.history.imp;

import SystemLogic.execution.context.Context;
import SystemLogic.execution.instance.enitty.manager.EntityInstanceManager;
import SystemLogic.history.api.Archive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistoryArchive implements Archive , Serializable {

    List<SimulationHistory> simulations;
    private int simulationsNumber;

    public HistoryArchive() {
        this.simulations = new ArrayList<>();
        this.simulationsNumber = 0;
    }

    public List<SimulationHistory> getSimulations() {
        return simulations;
    }

    public int getSimulationsNumber(){
        return simulationsNumber;
    }

    @Override
    public void addSimulation(Map<String, List<Context>> entities, EntityInstanceManager entityInstanceManager) {
        simulationsNumber++;
        SimulationHistory simulation = new SimulationHistory(entities ,entityInstanceManager);
        simulations.add(simulation);
    }

    @Override
    public StringBuilder getSimulationsListByDates() {
        StringBuilder result = new StringBuilder("Here all the simulations list by dates: \n");
        int count = 1;
        for (SimulationHistory simulation : simulations) {
            result.append(count).append(". ").append(simulation.getDate()).append("\n");
            count++;
        }

        return result;
    }

    @Override
    public SimulationHistory getSimulationHistory(int number) {
        if (number < 1 || number > simulations.size()) {
            throw new IndexOutOfBoundsException("Please enter a number between 1 to " + simulations.size());
        }
        return simulations.get(number - 1);
    }


}
