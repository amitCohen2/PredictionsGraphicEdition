package SystemLogic.execution.results.imp;

import SystemLogic.history.imp.SimulationHistory;
import SystemLogic.execution.details.imp.ExecutionDetails;
import SystemLogic.execution.results.api.ExeResult;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class ExecutionResult implements ExeResult {

    private final ExecutionDetails executionDetails;
    private final Label simulationFileResultLabel;
    private LineChart<Number, Number> lineChart;
    private Map<String, List<String>> entityProperty;
    private final String simulationFile;
    private final int id;
    private final SimulationHistory simulationHistory;
    public ExecutionResult(ExecutionDetails executionDetails, String simulationFile, Label simulationFileResultLabel, SimulationHistory simulationHistory, int id) {
        this.executionDetails = executionDetails;
        this.simulationFile = simulationFile;
        this.simulationHistory = simulationHistory;
        this.simulationFileResultLabel = simulationFileResultLabel;
        this.simulationFileResultLabel.textProperty().bind(Bindings.concat("Simulation File: ", this.simulationFile));
        this.id = id;
    }

    public SimulationHistory getSimulationHistory() {
        return simulationHistory;
    }


    private void initLineChart( ) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Ticks");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Population");

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Entities Population History");

        simulationHistory.getEntityPopulationByTicks().forEach((entity, populations) -> {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(entity);
            final int[] counter = {0};
            populations.forEach(populationAmount -> {
                if( counter[0]<= simulationHistory.getCurrentTick() && populationAmount!=null) {
                    series.getData().add(new XYChart.Data<>(counter[0], populationAmount));
                }
                counter[0]++;
            });
            lineChart.getData().add(series);
        });
    }

    public void showLineChart() {
        // Create a new Stage for the LineChart popup
        Stage chartStage = new Stage();
        chartStage.setTitle("Line Chart Popup");

        // Initialize the LineChart
        initLineChart();


        // Create a scene for the LineChart
        Scene chartScene = new Scene(lineChart, 800, 600);

        // Set the scene to the chartStage
        chartStage.setScene(chartScene);

        // Show the LineChart popup
        chartStage.show();
    }

}
