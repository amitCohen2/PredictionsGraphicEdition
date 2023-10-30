package design.body;

import SystemLogic.execution.manager.imp.SimulationExecutionManagerImp;
import SystemLogic.execution.runner.imp.SimulationRunner;
import SystemLogic.execution.status.ExecutionStatus;
import SystemLogic.history.imp.SimulationHistory;
import SystemLogic.worldInstance.imp.WorldInstance;
import XmlLoader.XmlLoader;
import XmlLoader.exceptions.FileFormatException;
import XmlLoader.schema.*;
import design.app.AppController;
import SystemLogic.execution.details.imp.ExecutionDetails;
import design.body.execution.xmlSimulationDetails.imp.SimulationDetails;
import SystemLogic.execution.results.imp.ExecutionResult;
import design.body.execution.pageManager.imp.SimulationExecutionPageManager;
import SystemLogic.execution.results.imp.PropertyResultRow;
import design.body.execution.treeViewDetails.imp.TreeViewDetails;
import design.body.execution.business.BusinessLogic;
import design.body.execution.business.HistogramsUtils;
import design.body.execution.business.UIAdapter;
import design.header.QueueManagement;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import XmlRunner.XmlRunner;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.*;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.util.Duration;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static SystemLogic.execution.status.ExecutionStatus.DONE;
import static SystemLogic.execution.status.ExecutionStatus.IN_PROGRESS;

public class BodyController {

    @FXML private TabPane tabPane;
    /*     details tab components    */
    @FXML private Tab detailsTab;
    @FXML private TreeView<String> treeViewEnvironments;
    @FXML private TreeView<String> treeViewEntities;
    @FXML private TreeView<String> treeViewRules;
    @FXML private TreeView<String> treeViewOther;
    /*     new executions tab components    */
    @FXML private Tab newExecutionTab;
    @FXML private Button startButton;
    @FXML private Button clearButton;
    @FXML private Button randomFillButton;
    @FXML private Button checkButton;
    @FXML private GridPane gridEntitiesPopulation;
    @FXML private GridPane gridEnvironmentsUserInput;
    @FXML private Label populationAmountLeftLabel;
    /*     Results tab components    */
    @FXML private Label executionResultTitle;
    @FXML private Label ExecutionResulatsStatusLabel;
    @FXML private Label tickCounterResultsLabel;
    @FXML private Label SecondsCounterResultsLabel;
    @FXML private Label simulationResultsLabel;
    @FXML private Button stopBResultsButton;
    @FXML private Button pauseResultsButton;
    @FXML private Button resumeResultsButton;
    @FXML private Button tickBeforeResultsButton;
    @FXML private Button tickAfterResultsButton;
    @FXML private Button reloadSameSimulationDetailsButton;
    @FXML private Button entitiesPopulationHistoryButton;
    @FXML private TableView<String> resultsEntityTableView;
    @FXML private TableView<PropertyResultRow> resultsPropertyTableView;
    @FXML private TableColumn<String, String> entityColumn;
    @FXML private TableColumn<PropertyResultRow, String> propertyColumn;
    @FXML private TableColumn<PropertyResultRow, String> consistencyColumn;
    @FXML private TableColumn<PropertyResultRow, String> averageColumn;
    @FXML private GridPane gridButtonsResultSimulation;
    @FXML private GridPane ExecResultGridPane;
    @FXML private GridPane simulationProgressGrid;
    @FXML private GridPane simulationGrid;
    @FXML private GridPane StyleGridPane;
    @FXML private GridPane StyleGridPaneIn;
    @FXML private Label simulationFileResultLabel;
    @FXML private ListView<String> listExecutionsView;
    @FXML private Tab resultsTab;
    @FXML Slider slider1;
    @FXML Slider slider2;
    @FXML Slider slider3;
    @FXML Slider slider4;
    @FXML Slider slider5;
    @FXML private TextField textField1;
    @FXML private TextField textField2;
    @FXML private TextField textField3;
    @FXML private TextField textField4;
    @FXML private TextField textField5;
    @FXML private Tab StyleTab;
    @FXML private AnchorPane detailsAnchorPane;
    @FXML private GridPane detailsGridPane;
    @FXML private ColumnConstraints detailsColumnConstraints1;
    @FXML private ColumnConstraints detailsColumnConstraints2;
    @FXML private ColumnConstraints detailsColumnConstraints3;
    @FXML private ColumnConstraints detailsColumnConstraints4;
    @FXML private Label redLabel;
    @FXML private Label greenLabel;
    @FXML private Label blueLabel;
    @FXML private Label alphaLabel;
    @FXML private Label textSizeLabel;

    private Boolean isStyleInit;
    private final String stepper = "Stepper";
    private final String predictions = "Predictions";
    private AppController mainController;
    private final StringProperty titleValue;
    private IntegerProperty listViewExecutionIdChoice;
    private SimulationExecutionPageManager simulationExecutionManager;
    private XmlRunner xmlRunner;
    private boolean isActive = false;

    private boolean simulationBeforeIsActive =false;
    private BusinessLogic businessLogic;
    private final IntegerProperty totalSeconds;
    private final IntegerProperty totalTicks;
    private final StringProperty currentStatus;
    private final BooleanProperty isSimulationDone;
    private final BooleanProperty isSimulationPaused;
    private QueueManagement queueManagement;
    private boolean resume;
    private boolean isNewPropertyResult;
    private Map<String,List<PropertyResultRow> >propertyResultRows;


    public BodyController() {
        isStyleInit=false;
        titleValue =  new SimpleStringProperty();
        totalSeconds = new SimpleIntegerProperty(0);
        totalTicks = new SimpleIntegerProperty(0);
        currentStatus = new SimpleStringProperty("");
        listViewExecutionIdChoice = new SimpleIntegerProperty(0);
        isSimulationDone = new SimpleBooleanProperty(false);
        isSimulationPaused = new SimpleBooleanProperty(false);
        isNewPropertyResult = true;
        propertyResultRows = new HashMap<>();
    }
    public boolean checkIfSimulationRun() {
        if (xmlRunner == null) {
            return false;
        }
        SimulationExecutionManagerImp simulationManager = simulationExecutionManager.getSimulationExecutionManagerImp();
        return simulationManager.getExecutions().size() != simulationManager.getExecutionsResults().size();
    }


    @FXML
    private void initialize() {
        // task message
        simulationResultsLabel.textProperty().bind(Bindings.concat("Simulation: ", listViewExecutionIdChoice));
        ExecutionResulatsStatusLabel.textProperty().bind(Bindings.concat("Execution Status: ", currentStatus));
        tickCounterResultsLabel.textProperty().bind(Bindings.concat("Ticks Counter: ", Bindings.format("%,d", totalTicks)));
        SecondsCounterResultsLabel.textProperty().bind(Bindings.concat("Seconds Counter: ", Bindings.format("%,d", totalSeconds)));
        tickAfterResultsButton.disableProperty().bind(isSimulationDone.not());
        tickBeforeResultsButton.disableProperty().bind(isSimulationDone.not());
        //stopBResultsButton.disableProperty().bind(isSimulationDone);
    }


    public void bindTaskToUIComponents(Task<Boolean> aTask, Runnable onFinish) {
        // task message
        simulationResultsLabel.textProperty().bind(Bindings.concat("Simulation: ", listViewExecutionIdChoice));
        ExecutionResulatsStatusLabel.textProperty().bind(Bindings.concat("Execution Status: ", currentStatus));
        tickCounterResultsLabel.textProperty().bind(Bindings.concat("Ticks Counter: ", Bindings.format("%,d", totalTicks)));
        SecondsCounterResultsLabel.textProperty().bind(Bindings.concat("Seconds Counter: ", Bindings.format("%,d", totalSeconds)));
        tickAfterResultsButton.disableProperty().bind(isSimulationDone.not());
        tickBeforeResultsButton.disableProperty().bind(isSimulationDone.not());
        stopBResultsButton.disableProperty().bind(isSimulationDone);
        pauseResultsButton.disableProperty().bind(Bindings.or(isSimulationDone, isSimulationPaused));
        resumeResultsButton.disableProperty().bind(isSimulationDone);
        ExecResultGridPane.disableProperty().bind(isSimulationDone.not());
        resumeResultsButton.disableProperty().bind(isSimulationPaused.not());

        aTask.valueProperty().addListener((observable, oldValue, newValue) -> {
            onTaskFinished(Optional.ofNullable(onFinish));
        });
    }

    public StringProperty headerValueProperty() {
        return titleValue;
    }

    @FXML
    void OnRandomFillButtonMouseClicked(MouseEvent event) throws FileFormatException {
        simulationExecutionManager.fillEntitiesRandomly();
        simulationExecutionManager.fillEnvironmentsRandomly();
        simulationExecutionManager.updateStartButtonState();

    }

    @FXML
    void clearMouseClickListener(MouseEvent event) throws FileFormatException {
        simulationExecutionManager.initEntityToTextFieldCheckBoxMap();;
        simulationExecutionManager.initEnvironmentsToTextFieldCheckBox();;
        simulationExecutionManager.updateStartButtonState();
    }

    @FXML
    void onMouseClickedListExecutionsView(MouseEvent event) {
        isNewPropertyResult = true;
        String selectedItem = listExecutionsView.getSelectionModel().getSelectedItem();

        int userListExecutionsChoice = findSimulationNumberInString(selectedItem);
        if (userListExecutionsChoice != -1) {
            if (simulationExecutionManager.getSimulationExecutionManagerImp().getSimulationsRunners().get(userListExecutionsChoice).getStatusType().equals(IN_PROGRESS)) {
                isSimulationDone.set(false);
            }

            listViewExecutionIdChoice.set(userListExecutionsChoice);
            updateSimulationsResultsData();
            if (simulationExecutionManager.getSimulationExecutionManagerImp().getExecutionsResults().containsKey(listViewExecutionIdChoice)) {
                ExecResultGridPane.setDisable(false);
                initTableView(resultsPropertyTableView);
                if (selectedItem != null) {
                    // Perform your action here
                    showExecutionResults();
                }
            }
        }
    }

    private void initButtonsAndTableResults() {


    }

    private void updateSimulationsResultsData() {
        businessLogic.collectMetadata(simulationExecutionManager.getSimulationExecutionManagerImp(),
                listViewExecutionIdChoice,
                totalSeconds::set,
                totalTicks::set,
                createUIAdapter(),
                () -> {
                    updateFinishedSimulation();
                }, currentStatus);
    }

    private void updateFinishedSimulation() {
        Platform.runLater(() -> {
            SimulationRunner currentSimulationRunner = simulationExecutionManager.getSimulationExecutionManagerImp().getSimulationsRunners().get(listViewExecutionIdChoice.get());
            if(currentSimulationRunner.getIsFaild()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("simulation Failure");
                alert.setHeaderText(currentSimulationRunner.getReasonForFailure());
                alert.setContentText("There was a problem with the simulation try again or try another file.");
                alert.showAndWait();
            }

            if (currentSimulationRunner.getStatusType().equals(ExecutionStatus.DONE)) {
                isSimulationDone.set(true);
            }

            simulationResultsLabel.textProperty().unbind();
            ExecutionResulatsStatusLabel.textProperty().unbind();
            tickCounterResultsLabel.textProperty().unbind();
            SecondsCounterResultsLabel.textProperty().unbind();
        });

        initTableView(resultsPropertyTableView);
        showExecutionResults();
    }

    private UIAdapter createUIAdapter() {
        return new UIAdapter(
                (seconds) -> {
                    //HistogramsUtils.log("EDT: CREATE new tile for [" + histogramData.toString() + "]");
                    totalSeconds.set(totalTicks.get() + seconds);
                },
                (ticks) -> {
                    //HistogramsUtils.log("EDT: UPDATE tile for [" + histogramData.toString() + "]");
                    totalTicks.set(totalTicks.get() + ticks);
                },
                (status) -> {
                    HistogramsUtils.log("EDT: INCREASE total distinct words");
                    currentStatus.set(status);
                }
        );
    }


    private void bindSimulationInProgressComponents() {
         Platform.runLater(() -> {
            SimulationRunner currentSimulationRunner = simulationExecutionManager.getSimulationExecutionManagerImp().getSimulationsRunners().get(listViewExecutionIdChoice.get());
            simulationResultsLabel.textProperty().bind(Bindings.concat("Simulation: ", listViewExecutionIdChoice.get()));
            ExecutionResulatsStatusLabel.textProperty().bind(Bindings.concat("Execution Status: ", currentSimulationRunner.getStatus()));
            tickCounterResultsLabel.textProperty().bind(Bindings.concat("Ticks Counter: ", Bindings.format("%,d", currentSimulationRunner.getTicks())));
            SecondsCounterResultsLabel.textProperty().bind(Bindings.concat("Seconds Counter: ", Bindings.format("%,d", (int) currentSimulationRunner.getTimer().getTotalTime())));
        });
    }

    @FXML
    void onMouseClickedEntitiesPopulationHistory(MouseEvent event) {
        simulationExecutionManager.getSimulationExecutionManagerImp().getExecutionsResults().get(listViewExecutionIdChoice.get()).showLineChart();
    }

    @FXML
    void onMouseClickCheck(MouseEvent event) {
        simulationExecutionManager.checkAllCorrectTextFields();
    }
    @FXML
    void onMouseClickedReloadSameSimulationDetails(MouseEvent event) throws FileFormatException {
       xmlRunner.setIsFirstRun(false);
        setSameSimulationDetails();
    }
    @FXML
    void onMouseClickedPropertyChartResult(MouseEvent event) {
        if(resultsEntityTableView.getSelectionModel().getSelectedItem() != null) {
            if (resultsPropertyTableView.getSelectionModel().getSelectedItem() != null) {
                showPropertyChart(resultsEntityTableView.getSelectionModel().getSelectedItem(), resultsPropertyTableView.getSelectionModel().getSelectedItem().getPropertyName().get());
            }
        }
    }
    private void setSameSimulationDetails() throws FileFormatException {
        Map<Integer, ExecutionDetails> executionsDetails = simulationExecutionManager.getSimulationExecutionManagerImp().getExecutions();
        ExecutionDetails pastExecutionDetails = executionsDetails.get(listViewExecutionIdChoice.get());
        simulationExecutionManager.setExecutionDetails(pastExecutionDetails.getEntityPopulation(), pastExecutionDetails.getEnvironmentsToStringValues());
        simulationExecutionManager.updateStartButtonState();
        tabPane.getSelectionModel().select(newExecutionTab);
    }
    private void showExecutionResults() {
        if (gridButtonsResultSimulation.isDisable()) {
            gridButtonsResultSimulation.setDisable(false);
        }
        executionResultTitle.setText("Execution " + listViewExecutionIdChoice.get() + " Results");
        updateEntityTableView();
    }
    private void updateEntityTableView() {
        initTableView(resultsEntityTableView);

        if (resultsEntityTableView.isDisable()) {
            resultsEntityTableView.setDisable(false);
        }

        // Set cell value factories for each column
        propertyColumn.setCellValueFactory(cellData -> cellData.getValue().getPropertyName());
        consistencyColumn.setCellValueFactory(cellData -> cellData.getValue().getConsistency());
        averageColumn.setCellValueFactory(cellData -> cellData.getValue().getAverage());

        Map<String, List<String>> entityProperty = simulationExecutionManager.getSimulationDetails().getEntityPropertiesSimulationMap();
        ObservableList<String> data = FXCollections.observableArrayList(entityProperty.keySet());
        entityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
        resultsEntityTableView.setItems(data);

        resultsEntityTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                initPropertyTableView(newSelection, entityProperty);
            }
        });
    }
    private void initPropertyTableView(String entityName, Map<String, List<String>> entityProperty) {
        initTableView(resultsPropertyTableView);

        if (resultsPropertyTableView.isDisable()) {
            resultsPropertyTableView.setDisable(false);
        }
        ObservableList<PropertyResultRow> data = FXCollections.observableArrayList(getPropertyResultsRows(entityName));
        resultsPropertyTableView.setItems(data);
    }

    private void showPropertyChart(String entityName, String propertyName) {
        // Create a new Stage for the LineChart popup
        Stage chartStage = new Stage();
        chartStage.setTitle("Bar Chart - Entity: " + entityName + " Property: " + propertyName);

        // Create a bar chart
        BarChart<String, Number> barChart = getBarChart(entityName, propertyName);//new BarChart<>(xAxis, yAxis);

        // Create a scene for the LineChart
        Scene chartScene = new Scene(barChart, 800, 600);

        // Set the scene to the chartStage
        chartStage.setScene(chartScene);

        // Show the LineChart popup
        chartStage.show();
    }

    private BarChart<String,Number> getBarChart(String entityName, String propertyName) {
        // Create axes for the bar chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("values");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Population");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Property " + propertyName + " Statistics");

        Map<Object, Integer> propertyAfterSimulationHistory = simulationExecutionManager.getSimulationExecutionManagerImp().getExecutionsResults()
                .get(listViewExecutionIdChoice.get()).getSimulationHistory().getPropertyHistoryMap(entityName, propertyName);

        SortedMap<Object, Integer> sortedPropertyHistory = new TreeMap<>(new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                // Implement your custom comparison logic here
                if (o1 instanceof Number && o2 instanceof Number) {
                    // Compare as numbers
                    double num1 = ((Number) o1).doubleValue();
                    double num2 = ((Number) o2).doubleValue();
                    return Double.compare(num1, num2);
                } else {
                    // Compare as strings
                    return o1.toString().compareTo(o2.toString());
                }
            }
        });


        sortedPropertyHistory.putAll(propertyAfterSimulationHistory);

        sortedPropertyHistory.forEach((value, amount) -> {

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(value.toString());
            series.getData().add(new XYChart.Data<>(value.toString(), amount)); // TODO: add option if its float to round the vakues
            barChart.getData().add(series);
        });

        return barChart;
    }
    public void setIsNewPropertyResult(boolean flag){
        this.isNewPropertyResult=flag;
    }

    private List<PropertyResultRow> getPropertyResultsRows(String selectionEntity) {

        if(isNewPropertyResult || propertyResultRows.get(selectionEntity) == null) {

            List<PropertyResultRow> propertyResult = new ArrayList<>();
            Map<String, Map<String, Boolean>> isFloatEntityPropertyName = simulationExecutionManager.getIsFloatEntityPropertyName();
            isFloatEntityPropertyName.get(selectionEntity).forEach((property, isFloat) -> {
                SimulationHistory simulationHistory = simulationExecutionManager.getSimulationExecutionManagerImp().getExecutionsResults()
                        .get(listViewExecutionIdChoice.get()).getSimulationHistory();
                if (simulationHistory.getPropertyConsistencyHistory(selectionEntity) != null) {
                    double consistency = simulationHistory.getPropertyConsistencyHistory(selectionEntity).get(property);
                    if (isFloat) {
                        Map<Object, Integer> propertyAfterSimulationHistory = simulationHistory.getPropertyHistoryMap(selectionEntity, property);
                        double avg = calculatePropertyAverage(propertyAfterSimulationHistory);
                        propertyResult.add(new PropertyResultRow(property, String.valueOf(consistency), String.valueOf(avg)));
                    } else {
                        propertyResult.add(new PropertyResultRow(property, String.valueOf(consistency), "-"));
                    }
                } else {
                    propertyResult.add(new PropertyResultRow(property, "none", "no population"));
                }

            });
            propertyResultRows.put(selectionEntity,propertyResult);
            isNewPropertyResult = false;
            return propertyResult;

        }
        else {
            isNewPropertyResult = false;
            return propertyResultRows.get(selectionEntity);
        }
    }

    private double calculatePropertyAverage(Map<Object, Integer> propertyAfterSimulationHistory) {
        final int[] population = {0};
        final double[] sumValues = {0};

        if (propertyAfterSimulationHistory.size() == 0) {
            return 0;
        }

        propertyAfterSimulationHistory.forEach((value, amount) -> {
            sumValues[0] += Double.parseDouble(value.toString());
            population[0] += amount;
        });

        if (population[0] != 0) {
            return sumValues[0] / population[0];
        }

        return 0;
    }

    private void initTableView(TableView tableView) {
        ObservableList<String> dataEntity = tableView.getItems();
        dataEntity.clear();
        ObservableList<String> dataProperty = tableView.getItems();
        dataProperty.clear();
    }

    private int findSimulationNumberInString(String input) {
        // Define a regular expression pattern to match the number
        Pattern pattern = Pattern.compile("Execution (\\d+)");

        // Use a Matcher to find the number in the string
        Matcher matcher = pattern.matcher(input);

        // Check if a match is found
        if (matcher.find()) {
            // Extract and parse the number from the matched group
            String numberString = matcher.group(1);
            int number = Integer.parseInt(numberString);
            //System.out.println("Found number: " + number);
            return number;
        } else {
            return -1;
        }
    }
    public void DrawAnimation(){
        // Circle animation
        Circle circle = new Circle(100);
        circle.setFill(javafx.scene.paint.Color.RED);
        FillTransition circleFillTransition = new FillTransition(Duration.seconds(2), circle);
        circleFillTransition.setCycleCount(4);
        circleFillTransition.setAutoReverse(true);
        circleFillTransition.setFromValue(javafx.scene.paint.Color.RED);
        circleFillTransition.setToValue(javafx.scene.paint.Color.GREEN);
        // Triangle animation
        Polygon triangle = createTriangle(120);
        triangle.setFill(javafx.scene.paint.Color.BLUE);
        RotateTransition triangleRotateTransition = new RotateTransition(Duration.seconds(2), triangle);
        triangleRotateTransition.setByAngle(360); // Rotate 360 degrees (1 full rotation)
        triangleRotateTransition.setCycleCount(4);
        triangleRotateTransition.setAutoReverse(true);
        // Create an HBox to hold both shapes
        HBox hbox = new HBox(circle, triangle);
        hbox.setSpacing(40); // Adjust spacing as needed
        // Create a Scene with the HBox
        Scene scene = new Scene(hbox, 400, 200);
        // Create a Stage for the scene
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Loading...");
        stage.setResizable(false);
        stage.setScene(scene);
        ParallelTransition parallelTransition = new ParallelTransition(circleFillTransition, triangleRotateTransition);
        // Play the animations
        stage.show();
        parallelTransition.play();
        parallelTransition.setOnFinished(event1 -> {
            stage.close(); // Close the window after animation finishes
        });
    }

    @FXML
    void startMouseClickListener(MouseEvent event) throws FileFormatException {
        if(!simulationExecutionManager.isValuesAreValid()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Values! ");
            alert.setContentText("Please enter valid values: the population you entered is bigger then the limit or 0\n try again! ");
            alert.showAndWait();
            return;
        }
        if(isActive){//javafx.scene.paint.Color.
            Platform.runLater(() -> {
                DrawAnimation();
            });
        }

        int currentSimulationCounter = simulationExecutionManager.getSimulationExecutionManagerImp().getSimulationCounter();

        // add to executions Map
        simulationExecutionManager.getSimulationExecutionManagerImp().getExecutions().put(currentSimulationCounter,
                new ExecutionDetails(simulationExecutionManager.getEntityPopulationFromUserInput(), simulationExecutionManager.getEnvironmentsValuesFromUserInput(),
                        simulationExecutionManager.getEnvironmentsMap()));

        // add the Execution to the executions list
        listExecutionsView.getItems().add("Execution " + currentSimulationCounter);

        // open the result tab
        if (resultsTab.isDisable()) {
            resultsTab.setDisable(false);
        }

        tabPane.getSelectionModel().select(resultsTab);

        // run the Simulation
        SimulationExecutionManagerImp simulationExecutionManagerImp = simulationExecutionManager.getSimulationExecutionManagerImp();
        xmlRunner.initRunSimulation(currentSimulationCounter, simulationExecutionManagerImp.getHistoryArchive(),
                simulationExecutionManager.getWorld(), simulationExecutionManagerImp.getExecutions().get(currentSimulationCounter).getEntityPopulation(),
                simulationExecutionManager.getSimulationExecutionManagerImp().getSimulationsRunners(),simulationBeforeIsActive);

        xmlRunner.Run(simulationExecutionManager, simulationExecutionManagerImp.getSimulationCounter(), queueManagement);
        simulationExecutionManager.getSimulationExecutionManagerImp().updateSimulationCounter();
        // update the UI component for the current simulation after finish
        simulationExecutionManagerImp.getSimulationsRunners().get(currentSimulationCounter).setCompletionListener(simulationID -> {
            ExecutionDetails currentExecutionDetails = simulationExecutionManagerImp.getExecutions().get(currentSimulationCounter);
            simulationExecutionManagerImp.getExecutionsResults().put(currentSimulationCounter,
                    new ExecutionResult(currentExecutionDetails,
                            simulationExecutionManager.getSimulationFile(), simulationFileResultLabel,
                            simulationExecutionManagerImp.getHistoryArchive().getSimulationHistory(currentSimulationCounter),
                    currentSimulationCounter));
            showPopupFinishedSimulation(currentSimulationCounter);
            Platform.runLater(() -> {
                queueManagement.getSimulationsInProgress().set(queueManagement.getSimulationsInProgress().get() - 1);
                queueManagement.getSimulationsFinished().set(queueManagement.getSimulationsFinished().get() + 1);
            });
            //xmlRunner.getWorldInstance().getExecutorService().shutdown(); // shutdown worker threads
        });
    }

    @FXML
    void onMouseClickedPauseButton(MouseEvent event) throws  InterruptedException {
        if (listViewExecutionIdChoice.get() > 0) {
            isSimulationPaused.set(true);
            simulationExecutionManager.getSimulationExecutionManagerImp().getSimulationsRunners().get(listViewExecutionIdChoice.get()).setPaused(true);
        }
    }

    @FXML
    void onMouseClickedCheckAnimation(MouseEvent event){
        if(!isActive) {
            //simulationExecutionManager.getSimulationExecutionManagerImp().getSimulationsRunners().get(listViewExecutionIdChoice.get()).setAnimation(true);
            isActive= true;
        }
        else if(isActive){
            //simulationExecutionManager.getSimulationExecutionManagerImp().getSimulationsRunners().get(listViewExecutionIdChoice.get()).setAnimation(false);
            isActive= false;
        }
    }

    @FXML
    void onMouseClickedChecksimulationBefore(MouseEvent event){
        if(!simulationBeforeIsActive) {

            simulationBeforeIsActive= true;
            //simulationExecutionManager.getSimulationExecutionManagerImp().getSimulationsRunners().get(listViewExecutionIdChoice.get()).SetSimulationBefore(true);
        }
        else if(simulationBeforeIsActive){

            simulationBeforeIsActive= false;
           // simulationExecutionManager.getSimulationExecutionManagerImp().getSimulationsRunners().get(listViewExecutionIdChoice.get()).SetSimulationBefore(false);
        }
    }

    private Polygon createTriangle(double size) {
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(
                size / 2, 0.0,
                0.0, size,
                size, size
        );
        return triangle;
    }

    @FXML
    void onMouseClickedResumeButton(MouseEvent event) {
        if (listViewExecutionIdChoice.get() > 0) {
            isSimulationPaused.set(false);
            simulationExecutionManager.getSimulationExecutionManagerImp().getSimulationsRunners().get(listViewExecutionIdChoice.get()).setPaused(false);
        }

    }

    @FXML
    synchronized void onMouseClickedStopButton(MouseEvent event) {
        if (listViewExecutionIdChoice.get() > 0) {
            simulationExecutionManager.getSimulationExecutionManagerImp().getSimulationsRunners().get(listViewExecutionIdChoice.get()).setSimulationStatus(DONE);
            simulationExecutionManager.getSimulationExecutionManagerImp().getSimulationsRunners().get(listViewExecutionIdChoice.get()).setStopped(true);
        }

    }

    @FXML
    void onMouseClickedTickAfterButton(MouseEvent event) {
        isNewPropertyResult = true;
        propertyResultRows.clear();
        try {
            simulationExecutionManager.getSimulationExecutionManagerImp().getSimulationsRunners().get(listViewExecutionIdChoice.get()).runTickAfter(true);
            updateSimulationsResultsData();
            Platform.runLater(() -> {
                BooleanProperty disableProperty = new SimpleBooleanProperty(false);
                tickBeforeResultsButton.disableProperty().bind(disableProperty);
            });
        }
        catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Simulation Faild");
            alert.setContentText("the problem is." +e.getMessage() + " try again with another one.");
            alert.showAndWait();
        }

    }

    @FXML
    void onMouseClickedTickBeforeButton(MouseEvent event) {
        isNewPropertyResult = true;
        propertyResultRows.clear();
        simulationExecutionManager.getSimulationExecutionManagerImp().getSimulationsRunners().get(listViewExecutionIdChoice.get()).runTickBefore(true);
        if(simulationExecutionManager.getSimulationExecutionManagerImp().getSimulationsRunners().get(listViewExecutionIdChoice.get()).isHistoryOver()){
            Platform.runLater(() -> {
                BooleanProperty disableProperty = new SimpleBooleanProperty(true);
                tickBeforeResultsButton.disableProperty().bind(disableProperty);
            });
        }
        updateSimulationsResultsData();
    }

    private void showPopupFinishedSimulation(int counter) {
        Stage popupStage = new Stage();
        popupStage.initStyle(StageStyle.UTILITY);
        popupStage.initModality(Modality.APPLICATION_MODAL); // Prevent interactions with the main window

        StackPane popupLayout = new StackPane();
        popupLayout.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8);"); // Semi-transparent background

        popupLayout.getChildren().add(new Button("Execution number " + counter + " was ended!"));

        Scene popupScene = new Scene(popupLayout, 300, 200);

        popupStage.setScene(popupScene);
        popupStage.showAndWait(); // Show the popup and wait for it to close
    }

    private void initGridPane(GridPane grid) {
        // Save the first row and remove the rest
        int idxRowToSave = 0;

        // Create a list to store nodes to remove
        List<Node> nodesToRemove = new ArrayList<>();

        for (Node node : grid.getChildren()) {
            if (GridPane.getRowIndex(node) != null) {
                if (GridPane.getRowIndex(node) != idxRowToSave) {
                    nodesToRemove.add(node);
                }
            }
        }

        // Remove the nodes outside the loop
        grid.getChildren().removeAll(nodesToRemove);
    }

    @FXML
    void detailsTabSelectionChangedListener(Event event) {
        titleValue.set(predictions);
    }

    @FXML
    void newExecutionTabSelectionChangedListener(Event event) {
        titleValue.set(stepper);
    }

    @FXML
    void resultsTabSelectionChangedListener(Event event) {
        titleValue.set(stepper);
    }

    @FXML
    void StyleTabSelectionChangedListener(Event event) {
        if (StyleTab.isSelected() && !isStyleInit) {
            // Call your initialization method when the tab is selected
            initStylePage();
            isStyleInit =true;
        }
       /* String bgColor = String.format("-fx-background-color: rgba(%d, %d, %d, %.2f);",
                (int) slider1.getValue(),
                (int) slider2.getValue(),
                (int) slider3.getValue(),
                slider4.getValue());
        tabPane.setStyle(bgColor);*/
    }
    private void bindSliderValueToTextField(Slider slider, TextField textField) {
        IntegerProperty sliderValue = new SimpleIntegerProperty();
        sliderValue.bindBidirectional(slider.valueProperty());

        // Bind the text field to the slider's value as an integer
        if(slider.equals(slider4)) {
            textField.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf((float) Math.ceil(sliderValue.get())/100), sliderValue));

        }
        if(slider.equals(slider5)) {
            textField.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf((float) Math.ceil(sliderValue.get())), sliderValue));

        }
        else{
            textField.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf((int) Math.ceil(sliderValue.get() * 2.55)), sliderValue));
        }
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void setXmlInBody(XmlLoader xmlLoader, QueueManagement queueManagement) throws FileFormatException, IllegalArgumentException {
        PRDWorld world = xmlLoader.loadXmlData();
        this.queueManagement = queueManagement;
        if (xmlRunner != null && xmlRunner.getWorldInstance() != null) {
            xmlRunner.getWorldInstance().getExecutorService().shutdown();
        }
        xmlRunner = new XmlRunner();
        simulationExecutionManager = new SimulationExecutionPageManager(mainController.getFilePath(), world, populationAmountLeftLabel, gridEntitiesPopulation, gridEnvironmentsUserInput, startButton);
        initAndSetTreeView();
        initNewExecutionPage();
        initResultsPage();
        this.businessLogic = new BusinessLogic(this);

        //go to the Details Tab
        tabPane.getSelectionModel().select(detailsTab);
        simulationExecutionManager.updateStartButtonState();
    }



    public void onTaskFinished(Optional<Runnable> onFinish) {
        this.ExecutionResulatsStatusLabel.textProperty().unbind();
        this.tickCounterResultsLabel.textProperty().unbind();
        this.SecondsCounterResultsLabel.textProperty().unbind();
        if (simulationExecutionManager.getSimulationExecutionManagerImp().getSimulationsRunners().get(listViewExecutionIdChoice.get()).equals(DONE)) {
            isSimulationDone.set(true);
        }
        onFinish.ifPresent(Runnable::run);
    }
    private void initResultsPage() {
        resultsTab.setDisable(false);
        resultsEntityTableView.setDisable(false);
        resultsPropertyTableView.setDisable(false);


        //stopBResultsButton.setDisable(false);

        // Create an empty ObservableList and set it to the ListView
        ObservableList<String> items = FXCollections.observableArrayList();
        listExecutionsView.setItems(items);
    }
    @FXML
    private void  initStylePage(){
        slider1.setValue(0);
        slider2.setValue(0);
        slider3.setValue(0);
        slider4.setValue(0);
        slider5.setValue(0);
        // Bind sliders to text fields
        bindSliderValueToTextField(slider1, textField1);
        bindSliderValueToTextField(slider2, textField2);
        bindSliderValueToTextField(slider3, textField3);
        bindSliderValueToTextField(slider4, textField4);
        bindSliderValueToTextField(slider5, textField5);
        slider1.valueProperty().addListener((obs, oldVal, newVal) -> updateBackgroundColor());
        slider2.valueProperty().addListener((obs, oldVal, newVal) -> updateBackgroundColor());
        slider3.valueProperty().addListener((obs, oldVal, newVal) -> updateBackgroundColor());
        slider4.valueProperty().addListener((obs, oldVal, newVal) -> updateBackgroundColor());
        slider5.valueProperty().addListener((obs, oldVal, newVal) -> updateFontSize());
    }
    private void updateFontSize() {
        if (StyleTab.isSelected()) {
            int fontSize =(int)slider5.getValue();
            Font newFont = Font.font(fontSize);
            populationAmountLeftLabel.setFont(newFont);
            simulationFileResultLabel.setFont(newFont);
            simulationResultsLabel.setFont(newFont);
            tickCounterResultsLabel.setFont(newFont);
            executionResultTitle.setFont(newFont);
            redLabel.setFont(newFont);
            blueLabel.setFont(newFont);
            greenLabel.setFont(newFont);
            alphaLabel.setFont(newFont);
            textSizeLabel.setFont(newFont);
            detailsTab.getContent().setStyle("-fx-font-size: " + fontSize + "px;");
            resultsTab.getContent().setStyle("-fx-font-size: " + fontSize + "px;");
            newExecutionTab.getContent().setStyle("-fx-font-size: " + fontSize + "px;");
        }
        }
    private void updateBackgroundColor() {
        if (StyleTab.isSelected()) {
            String bgColor = String.format("-fx-background-color: rgba(%d, %d, %d, %.2f);",
                    (int) slider1.getValue(),
                    (int) slider2.getValue(),
                    (int) slider3.getValue(),
                    slider4.getValue()/100);
            tabPane.setStyle(bgColor);
            randomFillButton.setStyle(bgColor);
            resultsTab.setStyle(bgColor);
            newExecutionTab.setStyle(bgColor);
            resultsPropertyTableView.setStyle(bgColor);
            StyleTab.setStyle(bgColor);
            gridEntitiesPopulation.setStyle(bgColor);
            gridButtonsResultSimulation.setStyle(bgColor);
            detailsTab.setStyle(bgColor);
            gridEnvironmentsUserInput.setStyle(bgColor);
            ExecResultGridPane.setStyle(bgColor);
            detailsAnchorPane.setStyle(bgColor);
            detailsGridPane.setStyle(bgColor);
            checkButton.setStyle(bgColor);
            clearButton.setStyle(bgColor);
            simulationProgressGrid.setStyle(bgColor);
            treeViewEnvironments.setStyle(bgColor);
            treeViewEntities.setStyle(bgColor);
            treeViewOther.setStyle(bgColor);
            treeViewRules.setStyle(bgColor);
            resultsEntityTableView.setStyle(bgColor);
            startButton.setStyle(bgColor);
            simulationGrid.setStyle(bgColor);
            StyleGridPane.setStyle(bgColor);
            StyleGridPaneIn.setStyle(bgColor);
        }}

    void initNewExecutionPage() {
        // open this Tab
        if (newExecutionTab.isDisable()) {
            newExecutionTab.setDisable(false);
        } else { // init the grids and the maps
            initGridPane(gridEntitiesPopulation);
            initGridPane(gridEnvironmentsUserInput);
        }
        PRDWorld world = simulationExecutionManager.getWorld();
        simulationExecutionManager.initSimulationEntitiesPopulationNewExecutionPage(world);
        simulationExecutionManager.initSimulationEnvironments(world);
    }
    private void initAndSetTreeView() throws FileFormatException {
        PRDWorld world = simulationExecutionManager.getWorld();
        SimulationDetails simulationDetails = simulationExecutionManager.getSimulationDetails();
        //init
        TreeItem<String> environments = TreeViewDetails.setEnvironmentsDetails(world, simulationDetails);
        TreeItem<String> entities = TreeViewDetails.setEntitiesDetails(world, simulationDetails);
        TreeItem<String> rules = TreeViewDetails.setRulesDetails(world, simulationDetails);
        TreeItem<String> others = TreeViewDetails.setOtherDetails(world, simulationDetails);
        // set tree views
        treeViewEntities.setRoot(entities);
        treeViewEnvironments.setRoot(environments);
        treeViewRules.setRoot(rules);
        treeViewOther.setRoot(others);
    }

}
