package design.body.execution.simulation;

import design.body.execution.execImp.Execution;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationResult {

    private final Execution execution;
    private LineChart<Number, Number> lineChart;
    private TableView entityPropertyResultTableView;
    private Map<String, List<String>> entityProperty;
    private final int id;
    public SimulationResult(int id, Execution execution) {
        this.id = id;
        this.execution = execution;
        entityProperty = initEntityProperty();

    }

    public Map<String, List<String>> getEntityProperty() {
        return entityProperty;
    }

    public Map<String, List<String>> initEntityProperty() {
        Map<String, List<String>> result = new HashMap<>();

        execution.getWorld().getPRDEntities().getPRDEntity().forEach(entity -> {
            List<String> properties = new ArrayList<>();
            entity.getPRDProperties().getPRDProperty().forEach(prdProperty -> properties.add(prdProperty.getPRDName()));
        result.put(entity.getName(), properties);
        });

        return result;
    }

    TableView getEntityPropertyResultTableView() {
        return entityPropertyResultTableView;
    }

    private void initLineChart( ) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Ticks");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Population");

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Entities Population History");

        execution.getSimulationHistory().getEntityPopulationByTicks().forEach((entity, populations) -> {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(entity);
            final int[] counter = {1};
            populations.forEach(populationAmount -> {
                series.getData().add(new XYChart.Data<>(counter[0], populationAmount));
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
        if (lineChart == null) {
            initLineChart();
        }

        // Create a scene for the LineChart
        Scene chartScene = new Scene(lineChart, 800, 600);

        // Set the scene to the chartStage
        chartStage.setScene(chartScene);

        // Show the LineChart popup
        chartStage.show();
    }

}
