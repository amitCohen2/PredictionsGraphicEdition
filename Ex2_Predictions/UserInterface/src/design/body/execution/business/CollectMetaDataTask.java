package design.body.execution.business;

import SystemLogic.execution.manager.imp.SimulationExecutionManagerImp;
import SystemLogic.execution.runner.imp.SimulationRunner;
import SystemLogic.execution.status.ExecutionStatus;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

import java.util.function.Consumer;

public class CollectMetaDataTask extends Task<Boolean> {
    private final int SLEEP_TIME = 1;
    private UIAdapter uiAdapter;
    private SimulationExecutionManagerImp simulationExecutionManagerImp;
    private StringProperty simulationStatus;
    private IntegerProperty userExecutionsIDChoice;
    private Consumer<Integer> totalSecondsDelegate;
    private Consumer<Integer> totalTicksDelegate;
    private Consumer<Runnable> onCancel;

    public CollectMetaDataTask(SimulationExecutionManagerImp simulationExecutionManagerImp,
                               IntegerProperty userExecutionsIDChoice, Consumer<Integer> totalSecondsDelegate,
                               Consumer<Integer> totalTicksDelegate, UIAdapter uiAdapter, Consumer<Runnable> onCancel, StringProperty simulationStatus) {
        this.simulationExecutionManagerImp = simulationExecutionManagerImp;
        this.userExecutionsIDChoice = userExecutionsIDChoice;
        this.uiAdapter = uiAdapter;
        this.totalSecondsDelegate = totalSecondsDelegate;
        this.totalTicksDelegate = totalTicksDelegate;
        this.simulationStatus = simulationStatus;
    }

    @Override
    protected Boolean call() throws Exception {
        try {
            Platform.runLater(() -> simulationStatus.set("In Progress.."));
            int beforeUserChoice = userExecutionsIDChoice.get();
            // Update the properties on the JavaFX Application Thread
            //Platform.runLater(() -> {
            SimulationRunner simulationRunner = simulationExecutionManagerImp.getSimulationsRunners().get(beforeUserChoice);
            synchronized (this) {
                while (simulationRunner.getStatusType().equals(ExecutionStatus.IN_PROGRESS) && beforeUserChoice == userExecutionsIDChoice.get()) {
                    updateData(simulationRunner);
                }
                updateData(simulationRunner);
                Platform.runLater(() -> simulationStatus.set(simulationRunner.getStatus()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Boolean.TRUE;
    }

    private void updateData(SimulationRunner simulationRunner) {
        int totalSeconds = (int) Math.round(simulationRunner.getTimer().getTotalTime());
        int totalTicks = simulationRunner.getTicks();

        Platform.runLater(() -> {
            totalSecondsDelegate.accept(totalSeconds);
            totalTicksDelegate.accept(totalTicks);
        });

        HistogramsUtils.sleepForAWhile(SLEEP_TIME);
    }


}
