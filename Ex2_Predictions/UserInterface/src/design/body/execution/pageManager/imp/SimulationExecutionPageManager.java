package design.body.execution.pageManager.imp;

import SystemLogic.definition.value.generator.random.impl.bool.RandomBooleanValueGenerator;
import SystemLogic.definition.value.generator.random.impl.numeric.RandomFloatGenerator;
import SystemLogic.definition.value.generator.random.impl.numeric.RandomIntegerGenerator;
import SystemLogic.definition.value.generator.random.impl.string.StringRandomGenerator;
import SystemLogic.execution.manager.imp.SimulationExecutionManagerImp;
import XmlLoader.schema.PRDEnvProperty;
import XmlLoader.schema.PRDWorld;
import design.body.execution.pageManager.api.ExecManager;
import design.body.execution.xmlSimulationDetails.imp.SimulationDetails;
import javafx.application.Platform;
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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SimulationExecutionPageManager implements ExecManager {
    private final String filePath;
    private final SimulationExecutionManagerImp simulationExecutionManagerImp;
    private final Map<String, Object> environmentsMap; // this is for the result execution

    /* <------------- data members Scheme -----------> */
    private final PRDWorld world;
    private final SimulationDetails simulationDetails;


    /* <------------- data members with pointers to UI components -----------> */
    private final GridPane gridEntitiesPopulation;
    private final GridPane gridEnvironmentsUserInput;
    private final Button startButton;
    private Map<String, Pair<TextField, CheckBox>> entityToTextFieldCheckBox;
    private Map<String, Pair<Object, CheckBox>> environmentsToTextFieldCheckBox;
    private final Map<String, Map<String, Boolean>> isFloatEntityPropertyName;
    private final SimpleIntegerProperty populationAmount;



    public SimulationExecutionPageManager(String filePath, PRDWorld world, Label populationAmountLeftLabel, GridPane gridEntitiesPopulation, GridPane gridEnvironmentsUserInput, Button startButton) {
        this.world = world;
        this.gridEntitiesPopulation = gridEntitiesPopulation;
        this.gridEnvironmentsUserInput = gridEnvironmentsUserInput;
        this.startButton = startButton;
        this.simulationDetails = new SimulationDetails();
        this.filePath = filePath;
        this.environmentsMap = new HashMap<>();
        this.simulationExecutionManagerImp = new SimulationExecutionManagerImp();
        populationAmount = new SimpleIntegerProperty(0);
        isFloatEntityPropertyName = initIsFloatPropertyMap();
        populationAmountLeftLabel.textProperty().bind(Bindings.concat("Population input left: ", Bindings.format("%,d", getPopulationAmount())));
    }

    /*<--------------------------- Getters --------------------------->*/
    public Map<String, Object> getEnvironmentsMap() {
        return environmentsMap;
    }
    public SimpleIntegerProperty getPopulationAmount() {
        return populationAmount;
    }

    public Map<String, Map<String, Boolean>> getIsFloatEntityPropertyName() {
        return isFloatEntityPropertyName;
    }

    public String getFilePath() {
        return filePath;
    }

    public PRDWorld getWorld() {
        return world;
    }

    public SimulationExecutionManagerImp getSimulationExecutionManagerImp() {
        return simulationExecutionManagerImp;
    }

    public SimulationDetails getSimulationDetails() {
        return simulationDetails;
    }
    public int getPopulationNumber(PRDWorld world) {
        return world.getPRDGrid().getColumns() * world.getPRDGrid().getRows();
    }

    private ComboBox<String> getNewBooleanComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("True", "False"); // Add your options here
        comboBox.setMaxWidth(Double.MAX_VALUE);

        VBox vbox = new VBox(10);
        vbox.getChildren().add(comboBox);

        return comboBox;
    }


    public Map<String, Integer> getEntityPopulationFromUserInput() {
        Map<String, Integer> result = new HashMap<>();
        entityToTextFieldCheckBox.forEach((entity, pair) ->
                result.put(entity, Integer.parseInt(pair.getKey().getText())));
        return result;
    }

    public Map<String, String> getEnvironmentsValuesFromUserInput() {
        Map<String, String> result = new HashMap<>();

        environmentsToTextFieldCheckBox.forEach((environment, pair) -> {
            Object objField = pair.getKey();
            if (objField instanceof TextField) {
                result.put(environment, ((TextField) objField).getText());
            } else {
                result.put(environment, (String) ((ComboBox) objField).getValue());
            }
        });

        return result;
    }

    public String getSimulationFile() {
        Path path = Paths.get(filePath);
        return path.getFileName().toString();
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


    /*<--------------------------- Setters --------------------------->*/

    public void setExecutionDetails(Map<String, Integer> entityPopulation, Map<String, String> environmentValues) {
        entityToTextFieldCheckBox.forEach((entity, pair) -> {
            pair.getKey().setText(entityPopulation.get(entity).toString());
            pair.getValue().setSelected(true);
        });

        environmentsToTextFieldCheckBox.forEach((environment, pair) -> {
            Object objField = pair.getKey();
            if (objField instanceof TextField) {
                ((TextField)pair.getKey()).setText(environmentValues.get(environment));
            } else {
                ((ComboBox)pair.getKey()).setValue(environmentValues.get(environment));
            }
            pair.getValue().setSelected(true);
        });
    }

    public void setEnvironmentsToTextFieldCheckBox(Map<String, Pair<Object, CheckBox>> environmentsToTextFieldCheckBox) {
        this.environmentsToTextFieldCheckBox.forEach((environment, pair) -> {
            Object objField = pair.getKey();
            if (objField instanceof TextField) {
                ((TextField) objField).setText(((TextField) environmentsToTextFieldCheckBox.get(environment).getKey()).getText());
            } else if (objField instanceof ComboBox){
                ((ComboBox) objField).setValue(((ComboBox) environmentsToTextFieldCheckBox.get(environment).getKey()).getValue());
            }
            pair.getValue().setSelected(environmentsToTextFieldCheckBox.get(environment).getValue().isSelected());
        });
    }


    /*<--------------------------- Initialize --------------------------->*/

    public void initSimulationEnvironments(PRDWorld world) {
        environmentsToTextFieldCheckBox = new HashMap<>();

        Platform.runLater(() -> {
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
        });

    }

    public void initEntityToTextFieldCheckBoxMap() {
        entityToTextFieldCheckBox.forEach((entity, pair) -> {
            TextField currTextField = pair.getKey();
            CheckBox currCheckBox = pair.getValue();

            if (currCheckBox.isSelected()) {
                currCheckBox.setSelected(false);
                Platform.runLater(() -> {
                    currTextField.setText("");
                    currTextField.setStyle("-fx-background-color: white;");
                });
            }
            else {
                Platform.runLater(() -> {
                    currTextField.setText("");
                    currTextField.setStyle("-fx-background-color: white;");
                });
            }
        });
    }

    public void initEnvironmentsToTextFieldCheckBox() {
        environmentsToTextFieldCheckBox.forEach((environment, pair) -> {
            Object objField = pair.getKey();
            CheckBox currCheckBox = pair.getValue();
            boolean isTextField = ManagerUtils.isTextField(objField);

            if (currCheckBox.isSelected()) {
                currCheckBox.setSelected(false);
                if (isTextField) {
                    Platform.runLater(() -> ((TextField) objField).setText(""));
                } else {
                    Platform.runLater(() ->  ((ComboBox) objField).setValue(null));
                }
            }
            else {
                if (isTextField) {
                    Platform.runLater(() -> ((TextField) objField).setText(""));
                } else {
                    Platform.runLater(() ->  ((ComboBox) objField).setValue(null));
                }
            }
        });
    }

    public void initSimulationEntitiesPopulationNewExecutionPage(PRDWorld world) {
        entityToTextFieldCheckBox = new HashMap<>();

        final int[] rowIndex = {1}, colIndex = {0};
        int population = getPopulationNumber(world);
        //set the population amount

        Platform.runLater(() -> {
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

        });

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

    /*<--------------------------- Update --------------------------->*/

    public void updateStartButtonState() {
        Platform.runLater(() -> {
            // Check if all entity CheckBoxes are selected
            boolean allEntitySelected = entityToTextFieldCheckBox.values().stream()
                    .allMatch(pair -> pair.getValue().isSelected());

            // Check if all environments CheckBoxes are selected
            boolean allEnvironmentsSelected = environmentsToTextFieldCheckBox.values().stream()
                    .allMatch(pair -> pair.getValue().isSelected());

            // Enable the start button if all CheckBoxes in both maps are selected
            startButton.setDisable(!(allEntitySelected && allEnvironmentsSelected));
        });
    }


    /*<------------------ random fill methods ------------------------->*/
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
                boolean isTextField = ManagerUtils.isTextField(objField);

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
                            Platform.runLater(() -> ((TextField) objField).setText(text));
                        } else {
                            StringRandomGenerator randomGenerator = new StringRandomGenerator();
                            String text = randomGenerator.generateValue();
                            Platform.runLater(() -> ((TextField) objField).setText(text));
                        }
                    } else {
                        RandomBooleanValueGenerator randomGenerator = new RandomBooleanValueGenerator();
                        if (randomGenerator.generateValue()) {
                            Platform.runLater(() -> ((ComboBox) objField).setValue("True"));
                        } else {
                            Platform.runLater(() -> ((ComboBox) objField).setValue("False"));
                        }
                    }
                    currCheckBox.setSelected(true);
                }
            });
        }
    }


    /*<------------------ binds And Listeners ------------------------->*/

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
                    environmentsMap.put(environment.getPRDName(),false);
                }else{
                    environmentsMap.put(environment.getPRDName(),true);
                }
                Platform.runLater(() -> {
                    comboBox.setStyle("-fx-background-color: #8ecae6;");
                    correctComboBoxChoose.set(true); // Enable the CheckBox
                });
            } else {
                Platform.runLater(() -> {
                    comboBox.setStyle("-fx-background-color: white;");
                    correctComboBoxChoose.set(false); // Enable the CheckBox
                });
            }
        });

        //bind between check ComboBox to CheckBox
        checkBox.setOnAction(e -> {
            if (checkBox.isSelected()) {
                Platform.runLater(() -> {
                    comboBox.setStyle("-fx-background-color: #8ecae6;");
                    correctComboBoxChoose.set(false); // Enable the CheckBox
                });
            } else {
                Platform.runLater(() -> {
                    comboBox.setStyle("-fx-background-color: white;");
                    correctComboBoxChoose.set(true); // Enable the CheckBox
                });
            }
        });
    }

    private void setConnectionBetweenCheckBoxAndTextField(CheckBox checkBox, TextField textField, BooleanProperty checkBoxDisabled) {
        //bind between check Checkbox to editable TextField
        checkBox.setOnAction(e -> {
            if (checkBox.isSelected()) {
                Platform.runLater(() -> {
                    checkBoxDisabled.set(false);
                    textField.setEditable(false);
                    textField.setStyle("-fx-background-color: #8ecae6;");
                });
            } else {
                if (checkBox.isSelected()) {
                    Platform.runLater(() -> {
                        checkBoxDisabled.set(true);
                        textField.setEditable(true);
                        textField.setStyle("-fx-background-color: white;");
                    });
                }
            }
        });
    }

    private void setListenerForTextField(BooleanProperty correctTextField, CheckBox checkBox, TextField textField) {
        // Listener to handle ENTER key press
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (correctTextField.get()) {
                    checkBox.setSelected(true);
                }
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
        textField.editableProperty().bind(checkBox.selectedProperty().not());

        if (environment == null) { // Binds for entities!
            // Listener to update correctTextField based on the text field's text
            textField.textProperty().addListener((observable, oldValue, newValue) ->
                    ManagerUtils.checkIfIntegerInput(newValue, textField, correctTextField, populationAmount));
            setListenerForTextField(correctTextField, checkBox, textField);
            AddListenerBetweenPopulationAmountToTextField(textField, correctTextField, checkBox);
            checkBox.setOnAction(event ->  {
                updateStartButtonState();
                setConnectionBetweenCheckBoxAndTextField(checkBox, textField, checkBoxDisabled);
            });
        } else  { // set binds for environment variables
            if (environment.getType().equals("float")) {
                textField.textProperty().addListener((observable, oldValue, newValue) ->
                        ManagerUtils.checkIfDoubleInput(newValue, textField, correctTextField, environment, environmentsMap));
                setListenerForTextField(correctTextField, checkBox, textField);
                setConnectionBetweenCheckBoxAndTextField(checkBox, textField, checkBoxDisabled);
            } else if (environment.getType().equals("string")) {
                Platform.runLater(() -> correctTextField.set(true));
                environmentsMap.put(environment.getPRDName(),textField.getText());
                textField.textProperty().addListener((observable, oldValue, newValue) ->
                        textField.setStyle("-fx-background-color: #8ecae6;"));
                setListenerForTextField(correctTextField, checkBox, textField);
                setConnectionBetweenCheckBoxAndTextField(checkBox, textField, checkBoxDisabled);

            } else {
                throw new IllegalArgumentException("This Environment Type '" + environment.getType() + "' does not supported!");
            }
        }
    }

    public void checkAllCorrectTextFields() {
        entityToTextFieldCheckBox.forEach((entity, pair) -> {
            if (!pair.getValue().isDisable() &&  !pair.getValue().isSelected()) {
                pair.getValue().setSelected(true);
            }
        });

        environmentsToTextFieldCheckBox.forEach((entity, pair) -> {
            if (!pair.getValue().isDisable() &&  !pair.getValue().isSelected()) {
                pair.getValue().setSelected(true);
            }
        });

        updateStartButtonState();
    }

    public boolean isValuesAreValid() {
        checkAllCorrectTextFields();
        boolean res = true; // Initialize res to true
        Integer totalPupolation = 0;

        for (Map.Entry<String, Pair<TextField, CheckBox>> entry : entityToTextFieldCheckBox.entrySet()) {
            Pair<TextField, CheckBox> pair = entry.getValue();
            TextField textField = pair.getKey();

            try {
                int population = Integer.parseInt(textField.getText());
                totalPupolation += population;
            } catch (NumberFormatException e) {
                // Handle the case where the text cannot be parsed as an integer
                System.err.println("Invalid population value for entity " + entry.getKey());
            }
        }
        return (totalPupolation != 0 && totalPupolation< getPopulationNumber(world));
    }


}
