package design.body.execution.execImp;

import SystemLogic.definition.value.generator.random.impl.bool.RandomBooleanValueGenerator;
import SystemLogic.definition.value.generator.random.impl.numeric.RandomFloatGenerator;
import SystemLogic.definition.value.generator.random.impl.numeric.RandomIntegerGenerator;
import SystemLogic.definition.value.generator.random.impl.string.StringRandomGenerator;
import SystemLogic.history.imp.SimulationHistory;
import XmlLoader.schema.PRDEnvProperty;
import XmlLoader.schema.PRDWorld;
import convertor.EnvironmentValuesFromUser;
import design.body.execution.simulation.SimulationDetails;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class Execution {

    private final GridPane gridEntitiesPopulation;
    private final GridPane gridEnvironmentsUserInput;
    private final Button startButton;
    private Map<String, Pair<TextField, CheckBox>> entityToTextFieldCheckBox;
    private Map<String, Pair<Object, CheckBox>> environmentsToTextFieldCheckBox;
    private SimulationDetails simulationDetails;
    private SimpleIntegerProperty populationAmount;
    private Map<String, Object> EnvMap;
    private PRDWorld world;
    private Map<String, EnvironmentValuesFromUser> environmentsFromUser;
    private Map<String, Integer> entityPopulationMap;
    private int id;
    private Label populationAmountLeftLabel;
    private SimulationHistory simulationHistory;

    private Map<String, Map<String, Boolean>> isFloatEntityPropertyName;


    public Execution(PRDWorld world, GridPane gridEntitiesPopulation, GridPane gridEnvironmentsUserInput, Button startButton, Label populationAmountLeftLabel) {
        this.gridEntitiesPopulation = gridEntitiesPopulation;
        this.gridEnvironmentsUserInput = gridEnvironmentsUserInput;
        this.world = world;
        this.startButton = startButton;
        this.populationAmountLeftLabel = populationAmountLeftLabel;
        EnvMap = new HashMap<>();
        environmentsFromUser = new HashMap<>();
        entityPopulationMap = new HashMap<>();
        populationAmount = new SimpleIntegerProperty(0);
        simulationDetails = new SimulationDetails();
        entityToTextFieldCheckBox = new HashMap<>();
        environmentsToTextFieldCheckBox = new HashMap<>();
        isFloatEntityPropertyName = initIsFloatPropertyMap();
        this.populationAmountLeftLabel.textProperty().bind(Bindings.concat("Population input left: ", Bindings.format("%,d", getPopulationAmount())));

    }

    public Map<String, Map<String, Boolean>> getIsFloatEntityPropertyName() {
        return isFloatEntityPropertyName;
    }

    public void setSimulationHistory(SimulationHistory simulationHistory) {
        this.simulationHistory = simulationHistory;
    }

    public SimulationHistory getSimulationHistory() {
        return simulationHistory;
    }

    public Map<String, Map<String, Boolean>> initIsFloatPropertyMap() {
        Map<String, Map<String, Boolean>> result = new HashMap<>();
        world.getPRDEntities().getPRDEntity().forEach(entity -> {
            Map<String, Boolean> properties = new HashMap<>();
            entity.getPRDProperties().getPRDProperty().forEach(property -> {
                if (property.getType().equals("float")) {
                    properties.put(property.getPRDName(), true);
                } else {
                    properties.put(property.getPRDName(), false);
                }
            });
            result.put(entity.getName(), properties);
        });

        return result;
    }



    public SimpleIntegerProperty getPopulationAmount() {
        return populationAmount;
    }
    // Getter for entityToTextFieldCheckBox
    public Map<String, Pair<TextField, CheckBox>> getEntityToTextFieldCheckBox() {
        return entityToTextFieldCheckBox;
    }

    // Getter for environmentsToTextFieldCheckBox
    public Map<String, Pair<Object, CheckBox>> getEnvironmentsToTextFieldCheckBox() {
        return environmentsToTextFieldCheckBox;
    }

    public void fillEntitiesRandomly() {
        if (entityToTextFieldCheckBox != null) {
            entityToTextFieldCheckBox.forEach((entity, pair) -> {
                TextField currTextField = pair.getKey();
                CheckBox currCheckBox = pair.getValue();

                if (!currCheckBox.isSelected()) {
                    RandomIntegerGenerator randomGenerator = new RandomIntegerGenerator(0, populationAmount.getValue());
                    currTextField.setText(Integer.toString(randomGenerator.generateValue()));
                    currCheckBox.setSelected(true);
                }
            });
        }
    }

    public void fillEnvironmentsRandomly() {
        if (environmentsToTextFieldCheckBox != null) {
            environmentsToTextFieldCheckBox.forEach((environment, pair) -> {
                Object objField = pair.getKey();
                CheckBox currCheckBox = pair.getValue();
                boolean isTextField = isTextField(objField);

                if (!currCheckBox.isSelected()) {
                    if (isTextField) {
                        PRDEnvProperty prdEnvProperty = simulationDetails.getEnvironments().get(environment);
                        if (prdEnvProperty.getType().equals("float")){
                            RandomFloatGenerator randomGenerator;
                            if (prdEnvProperty.getPRDRange() != null) {
                                randomGenerator = new RandomFloatGenerator(prdEnvProperty.getPRDRange().getFrom(),
                                        prdEnvProperty.getPRDRange().getTo());
                            } else {
                                randomGenerator = new RandomFloatGenerator(Double.MIN_VALUE, Double.MAX_VALUE);
                            }
                            String text = Double.toString(randomGenerator.generateValue());
                            ((TextField) objField).setText(text);
                        } else {
                            StringRandomGenerator randomGenerator = new StringRandomGenerator();
                            String text = randomGenerator.generateValue();
                            ((TextField) objField).setText(text);
                        }
                    } else {
                        RandomBooleanValueGenerator randomGenerator = new RandomBooleanValueGenerator();
                        if (randomGenerator.generateValue()) {
                            ((ComboBox) objField).setValue("True");
                        } else {
                            ((ComboBox) objField).setValue("False");
                        }
                    }
                    currCheckBox.setSelected(true);
                }
            });
        }
    }


    void initEnvironmentsToTextFieldCheckBox() {
        environmentsToTextFieldCheckBox.forEach((environment, pair) -> {
            Object objField = pair.getKey();
            CheckBox currCheckBox = pair.getValue();
            boolean isTextField = isTextField(objField);

            if (currCheckBox.isSelected()) {
                currCheckBox.setSelected(false);
                if (isTextField) {
                    ((TextField) objField).setText("");
                } else {
                    ((ComboBox) objField).setValue(null);
                }
            }
            else {
                if (isTextField) {
                    ((TextField) objField).setText("");
                } else {
                    ((ComboBox) objField).setValue(null);

                }
            }
        });
    }

    private boolean isTextField(Object objField) {
        if (objField instanceof TextField) {
            return true;
        }
        return false;
    }

    void initEntityToTextFieldCheckBoxMap() {
        entityToTextFieldCheckBox.forEach((entity, pair) -> {
            TextField currTextField = pair.getKey();
            CheckBox currCheckBox = pair.getValue();

            if (currCheckBox.isSelected()) {
                currCheckBox.setSelected(false);
                currTextField.setText("");
                currTextField.setStyle("-fx-background-color: white;");
            }
            else {
                currTextField.setText("");
                currTextField.setStyle("-fx-background-color: white;");
            }

        });
    }

    private void checkIfDoubleInput(String newValue, TextField textField, BooleanProperty correctTextField, PRDEnvProperty environment) {
        try {
            double inputNumber = Double.parseDouble(newValue);
            if (isInsideRange(inputNumber, environment)) {
                correctTextField.set(true);
                EnvMap.put(environment.getPRDName(),inputNumber);
                textField.setStyle("-fx-background-color: #8ecae6;");

            } else {
                textField.setStyle("-fx-background-color: pink;"); // Invalid input
                correctTextField.set(false);
            }
        } catch (NumberFormatException e) {
            textField.setStyle("-fx-background-color: pink;"); // Invalid input
            correctTextField.set(false);
        }
    }

    private boolean isInsideRange(double inputNumber, PRDEnvProperty environment) {
        if (environment.getPRDRange() != null) {
            double from = environment.getPRDRange().getFrom();
            double to = environment.getPRDRange().getTo();

            //my swap function
            if (from > to) {
                double tmp = from;
                from = to;
                to = tmp;
            }

            if (inputNumber > to || inputNumber < from) {
                return false;
            }

        }
        return true;
    }

    private void setListenerForTextField(BooleanProperty correctTextField, CheckBox checkBox, TextField textField) {
        // Listener to handle ENTER key press
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (correctTextField.get()) {
                    textField.setEditable(false);
                    checkBox.setSelected(true);
                }
            }
        });
    }

    private void setConnectionBetweenCheckBoxAndTextField(CheckBox checkBox, TextField textField, BooleanProperty checkBoxDisabled) {
        //bind between check Checkbox to editable TextField
        checkBox.setOnAction(e -> {
            if (checkBox.isSelected()) {
                checkBoxDisabled.set(false);
                textField.setEditable(false);
                textField.setStyle("-fx-background-color: #8ecae6;");
            } else {
                checkBoxDisabled.set(true);
                textField.setEditable(true);
                textField.setStyle("-fx-background-color: white;");
            }
        });
    }

    private boolean isCorrectPopulationAmountInput(int inputNumber) {
        return inputNumber >= 0 && inputNumber <= populationAmount.get();
    }

    void initSimulationEnvironments(PRDWorld world) {
        environmentsToTextFieldCheckBox = new HashMap<>();

        final int[] rowIndex = {1}, colIndex = {0};
        world.getPRDEnvironment().getPRDEnvProperty().forEach(environment -> {
            String environmentName = environment.getPRDName();
            //set environment variable
            Label environmentLabel = new Label(environmentName);
            environmentLabel.setPadding(new Insets(0, 0, 0, 10));
            gridEnvironmentsUserInput.add(environmentLabel, colIndex[0], rowIndex[0]);
            gridEnvironmentsUserInput.setHalignment(environmentLabel, HPos.CENTER);

            //set environment instructions ( type and range )
            Label environmentInputInstructions = getEnvironmentInputInstructions(environment);
            gridEnvironmentsUserInput.add(environmentInputInstructions, colIndex[0] + 1, rowIndex[0]);
            gridEnvironmentsUserInput.setHalignment(environmentInputInstructions, HPos.CENTER);

            //set the checkbox
            CheckBox checkBox = new CheckBox();
            gridEnvironmentsUserInput.add(checkBox, colIndex[0] + 3, rowIndex[0]);
            environmentLabel.setPadding(new Insets(0, 10, 0, 10));
            if (environment.getType().equals("boolean")) {
                ComboBox<String> comboBox = getNewBooleanComboBox();
                gridEnvironmentsUserInput.add(comboBox, colIndex[0] + 2, rowIndex[0]);
                setBindsBetweenCheckBoxesAndComboBox(comboBox, checkBox, environment);
                environmentsToTextFieldCheckBox.put(environmentName, new Pair<>(comboBox, checkBox));
            }
            else {
                // set text field and checkboxes
                TextField textField = new TextField();
                gridEnvironmentsUserInput.add(textField, colIndex[0] + 2, rowIndex[0]);
                setBindsBetweenCheckBoxesAndTextField(textField, checkBox, environment);
                environmentsToTextFieldCheckBox.put(environmentName, new Pair<>(textField, checkBox));
            }

            rowIndex[0]++;


        });

        //set the checkboxes Actions
        environmentsToTextFieldCheckBox.values().forEach(pair -> {
            CheckBox checkBox = pair.getValue();
            checkBox.setOnAction(event -> updateStartButtonState());
        });


    }

    private void setBindsBetweenCheckBoxesAndComboBox(ComboBox<String> comboBox, CheckBox checkBox, PRDEnvProperty environment) {
        BooleanProperty correctComboBoxChoose = new SimpleBooleanProperty(false);
        BooleanProperty checkBoxDisabled = new SimpleBooleanProperty(true);

        // Bind the intermediate property to the correctTextField property
        checkBoxDisabled.bind(correctComboBoxChoose.not());
        // Bind the checkboxes disable property to the intermediate property
        checkBox.disableProperty().bind(checkBoxDisabled);

        comboBox.setOnAction(event -> {
            String selectedValue = comboBox.getValue();
            if ("True".equals(selectedValue) || "False".equals(selectedValue)) {
                if("False".equals(selectedValue)){
                    EnvMap.put(environment.getPRDName(),false);
                }else{
                    EnvMap.put(environment.getPRDName(),true);
                    comboBox.setStyle("-fx-background-color: #8ecae6;");
                }
                correctComboBoxChoose.set(true); // Enable the CheckBox
            } else {
                comboBox.setStyle("-fx-background-color: white;");
            }
        });

        //bind between check ComboBox to CheckBox
        checkBox.setOnAction(e -> {
            if (checkBox.isSelected()) {
                checkBoxDisabled.set(false);
                comboBox.setStyle("-fx-background-color: #8ecae6;");
            } else {
                checkBoxDisabled.set(true);
                comboBox.setStyle("-fx-background-color: white;");
            }
        });
    }

    private void AddListenerBetweenPopulationAmountToTextField(TextField textField, BooleanProperty correctTextField, CheckBox checkBox) {
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int inputNumber = Integer.parseInt(textField.getText());
                if (checkBox.isSelected()) {
                    textField.setStyle("-fx-background-color: #8ecae6;");
                    populationAmount.set(populationAmount.get() - inputNumber);
                }
                else {
                    populationAmount.set(populationAmount.get() + inputNumber);
                }
            } catch (NumberFormatException e) {
                // continue the program - this is only for clear button while set to false the checkbox
            }

        });
    }
    private void setBindsBetweenCheckBoxesAndTextField(TextField textField, CheckBox checkBox, PRDEnvProperty environment) {
        BooleanProperty correctTextField = new SimpleBooleanProperty(false);
        BooleanProperty checkBoxDisabled = new SimpleBooleanProperty(true);

        // Bind the intermediate property to the correctTextField property
        checkBoxDisabled.bind(correctTextField.not());
        // Bind the checkboxes disable property to the intermediate property
        checkBox.disableProperty().bind(checkBoxDisabled);

        if (environment == null) { // Binds for entities!
            // Listener to update correctTextField based on the text field's text
            textField.textProperty().addListener((observable, oldValue, newValue) ->
                    checkIfIntegerInput(newValue, textField, correctTextField));
            setListenerForTextField(correctTextField, checkBox, textField);
            AddListenerBetweenPopulationAmountToTextField(textField, correctTextField, checkBox);
            checkBox.setOnAction(event ->  {
                updateStartButtonState();
                setConnectionBetweenCheckBoxAndTextField(checkBox, textField, checkBoxDisabled);
            });
        } else  { // set binds for environment variables
            if (environment.getType().equals("float")) {
                textField.textProperty().addListener((observable, oldValue, newValue) ->
                        checkIfDoubleInput(newValue, textField, correctTextField, environment));
                setListenerForTextField(correctTextField, checkBox, textField);
                setConnectionBetweenCheckBoxAndTextField(checkBox, textField, checkBoxDisabled);
            } else if (environment.getType().equals("string")) {
                correctTextField.set(true);
                EnvMap.put(environment.getPRDName(),textField.getText());
                textField.textProperty().addListener((observable, oldValue, newValue) ->
                        textField.setStyle("-fx-background-color: #8ecae6;"));
                //textField.setStyle("-fx-background-color: #8ecae6;");
                setListenerForTextField(correctTextField, checkBox, textField);
                setConnectionBetweenCheckBoxAndTextField(checkBox, textField, checkBoxDisabled);

            } else {
                throw new IllegalArgumentException("This Environment Type '" + environment.getType() + "' does not supported!");
            }
        }
    }


    private Label getEnvironmentInputInstructions(PRDEnvProperty environment) {
        Label result = new Label();
        StringBuilder instructions = new StringBuilder();
        instructions.append("Type: ").append(environment.getType()).append("  |  ").append("Range: ");
        if (environment.getPRDRange() != null) {
            instructions.append("[").append(environment.getPRDRange().getFrom()).append(" ,").append(environment.getPRDRange().getTo()).append("]");
        } else {
            instructions.append("no range");
        }

        result.setText(instructions.toString());
        return result;
    }

    private void checkIfIntegerInput(String newValue, TextField textField, BooleanProperty correctTextField) {
        try {
            int inputNumber = Integer.parseInt(newValue);
            if (isCorrectPopulationAmountInput(inputNumber)) {
                correctTextField.set(true);
                textField.setStyle("-fx-background-color: #8ecae6;");
            } else {
                textField.setStyle("-fx-background-color: pink;"); // Invalid input
                correctTextField.set(false);
            }
        } catch (NumberFormatException e) {
            textField.setStyle("-fx-background-color: pink;"); // Invalid input
            correctTextField.set(false);
        }
    }
    private ComboBox<String> getNewBooleanComboBox() {

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("True", "False"); // Add your options here
        comboBox.setMaxWidth(Double.MAX_VALUE);

        VBox vbox = new VBox(10);
        vbox.getChildren().add(comboBox);

        return comboBox;
    }

    private int getPopulationNumber(PRDWorld world) {
        return world.getPRDGrid().getColumns() * world.getPRDGrid().getRows();
    }

    public void setPopulationAmount(int population) {
        this.populationAmount.set(population);
    }

    void initSimulationEntitiesPopulationNewExecutionPage(PRDWorld world) {
        entityToTextFieldCheckBox = new HashMap<>();

        final int[] rowIndex = {1}, colIndex = {0};
        int population = getPopulationNumber(world);
        //set the population amount
        populationAmount.set(population);

        world.getPRDEntities().getPRDEntity().forEach(entity -> {
            String entityName = entity.getName();
            //set entity label
            Label currEntityLabel = new Label(entityName);
            gridEntitiesPopulation.add(currEntityLabel, colIndex[0], rowIndex[0]);
            gridEnvironmentsUserInput.setHalignment(currEntityLabel, HPos.CENTER);

            // set text field and checkboxes
            TextField textField = new TextField();
            gridEntitiesPopulation.add( textField, colIndex[0] + 1, rowIndex[0]);
            CheckBox checkBox = new CheckBox();
            entityToTextFieldCheckBox.put(entityName, new Pair<>(textField, checkBox));
            gridEntitiesPopulation.add(checkBox, colIndex[0] + 2, rowIndex[0]);
            gridEnvironmentsUserInput.setHalignment(checkBox, HPos.CENTER);
            // set binds and listeners between checkboxes and text fields
            setBindsBetweenCheckBoxesAndTextField(textField, checkBox, null);

            rowIndex[0]++;
        });

        entityToTextFieldCheckBox.values().forEach(pair -> {
            CheckBox checkBox = pair.getValue();
            TextField textField = pair.getKey();
            checkBox.setOnAction(event ->  {
                updateStartButtonState();
            });
        });
    }
    void updateStartButtonState() {
        // Check if all entity CheckBoxes are selected
        boolean allEntitySelected = entityToTextFieldCheckBox.values().stream()
                .allMatch(pair -> pair.getValue().isSelected());

        // Check if all environments CheckBoxes are selected
        boolean allEnvironmentsSelected = environmentsToTextFieldCheckBox.values().stream()
                .allMatch(pair -> pair.getValue().isSelected());

        // Enable the start button if all CheckBoxes in both maps are selected
        startButton.setDisable(!(allEntitySelected && allEnvironmentsSelected));
    }

    public Map<String, EnvironmentValuesFromUser> getEnvironmentsFromUserInputMap() {
        return environmentsFromUser;
    }

    public void initEnvironmentsFromUser() {
        for (Map.Entry<String, Object> entry : EnvMap.entrySet()) {
            String envName = entry.getKey();
            EnvironmentValuesFromUser envValues = new EnvironmentValuesFromUser();
            envValues.setName(envName);
            envValues.setValue(entry.getValue());
            envValues.setType(entry.getValue().getClass().getName());
            envValues.setValueFromUser(true);
            // Store the user values in a map
            environmentsFromUser.put(envName, envValues);
        }
    }


    public Map<String, Object> getEnvMap() {
        return EnvMap;
    }

    public SimulationDetails getSimulationDetails() {
        return simulationDetails;
    }

    public PRDWorld getWorld() {
        return world;
    }

    public void initEntityPopulationFromUser() {
        entityToTextFieldCheckBox.forEach((entity, pair) ->
                entityPopulationMap.put(entity, Integer.parseInt(pair.getKey().getText())));
    }

    public Map<String, Integer> getEntityPopulationMap() {
        return entityPopulationMap;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
