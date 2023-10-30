package design.body.execution.business;

import javafx.application.Platform;

import java.util.function.Consumer;

public class UIAdapter {
    private Consumer<Integer> updateTotalSeconds;
    private Consumer<Integer> updateTotalTicks;
    private Consumer<String> updateStatus;

    public UIAdapter(Consumer<Integer> updateTotalSeconds, Consumer<Integer> updateTotalTicks, Consumer<String> updateStatus) {
        this.updateTotalTicks = updateTotalTicks;
        this.updateStatus = updateStatus;
        this.updateTotalSeconds = updateTotalSeconds;
    }

    public void updateTicks(Integer totalTicks) {
        Platform.runLater(
                () -> {
                    updateTotalTicks.accept(totalTicks);
                    //updateDistinct.run();
                }
        );
    }

    public void updateSeconds(Integer seconds) {
        Platform.runLater(
                () -> updateTotalSeconds.accept(seconds)
        );
    }

    public void updateStatus(String status) {
        Platform.runLater(
                () -> updateStatus.accept(status)
        );
    }

}
