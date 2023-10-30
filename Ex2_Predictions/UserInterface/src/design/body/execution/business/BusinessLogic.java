package design.body.execution.business;

import SystemLogic.execution.manager.imp.SimulationExecutionManagerImp;
import design.body.BodyController;
import design.header.HeaderController;
import design.header.QueueManagement;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

import java.util.Optional;
import java.util.function.Consumer;

public class BusinessLogic {

    private int totalSeconds;
    private int totalTicks;
    private Task<Boolean> currentRunningTask;
    private BodyController bodyController;
    private HeaderController headerController;
    private SimulationExecutionManagerImp simulationExecutionManagerImp;
    private SimpleIntegerProperty userExecutionsIDChoice;

    public BusinessLogic(BodyController bodyController) {
        this.bodyController = bodyController;
        this.totalSeconds = -1;
        this.totalTicks = -1;
    }

    public void collectMetadata(SimulationExecutionManagerImp simulationExecutionManagerImp,
                                IntegerProperty userExecutionsIDChoice, Consumer<Integer> totalSecondsDelegate,
                                Consumer<Integer> totalTicksDelegate, UIAdapter uiAdapter, Runnable onFinish, StringProperty simulationStatus) {
        Consumer<Integer> totalSecondsConsumer = totalSeconds -> {
            this.totalSeconds = totalSeconds;
            totalSecondsDelegate.accept(totalSeconds);
        };
        Consumer<Integer> totalTicksConsumer = totalTicks -> {
            this.totalTicks = totalTicks;
            totalTicksDelegate.accept(totalSeconds);
        };

        currentRunningTask = new CollectMetaDataTask(simulationExecutionManagerImp, userExecutionsIDChoice , totalSecondsConsumer,
                totalTicksDelegate, uiAdapter, (q) -> bodyController.onTaskFinished(Optional.ofNullable(onFinish)), simulationStatus);

        bodyController.bindTaskToUIComponents(currentRunningTask, onFinish);

        new Thread(currentRunningTask).start();
    }

    public void updateFinish() {

    }


    public void cancelCurrentTask() {
        currentRunningTask.cancel();
    }



/*    public BusinessLogic(HistogramController controller) {
        this.fileName = new SimpleStringProperty();
        this.controller = controller;
    }

    public SimpleStringProperty fileNameProperty() {
        return this.fileName;
    }

    public void collectMetadata(Consumer<Long> totalWordsDelegate, Consumer<Long> totalLinesDelegate, Runnable onFinish) {

        Consumer<Long> totalWordsConsumer = tw -> {
            this.totalWords = tw;
            totalWordsDelegate.accept(tw);
        };

        currentRunningTask = new CollectMetadataTask(fileName.get(), totalWordsConsumer, totalLinesDelegate);

        controller.bindTaskToUIComponents(currentRunningTask, onFinish);

        new Thread(currentRunningTask).start();
    }


    public void calculateHistogram(UIAdapter uiAdapter, Runnable onFinish) {
        currentRunningTask = new CalculateHistogramsTask(fileName.get(), totalWords, uiAdapter, (q) -> controller.onTaskFinished(Optional.ofNullable(onFinish)));

        controller.bindTaskToUIComponents(currentRunningTask, onFinish);

        new Thread(currentRunningTask).start();
    }

    public void cancelCurrentTask() {
        currentRunningTask.cancel();
    }*/
}
