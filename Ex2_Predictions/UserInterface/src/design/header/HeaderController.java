package design.header;

import XmlLoader.exceptions.FileFormatException;
import design.app.AppController;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;

public class HeaderController {
    private AppController mainController;
    @FXML public Label headerTitle;
    @FXML private TextField filePathField;
    @FXML private Button loadFileButton;
    @FXML private Button queueManagementButton;
    @FXML private Label queueManagementLabel;
    @FXML private Label SimulationInProgressLabel;
    @FXML private Label waitingSimulationsLabel;
    @FXML private Label finishedSimulationsLabel;
    @FXML private Label simulationsInProgressCounterLabel;
    @FXML private Label waitingSimulationsCounterLabel;
    @FXML private Label FinishedSimulationsCounterLabel;

    @FXML
    void loadFileButtonClickListener(MouseEvent event) {
        checkAndLoadFilePathInput();
    }
    public void setFilePath(String filePath) {
         filePathField.setText(filePath);
    }
    public String getFilePath() {
        return filePathField.getText();
    }

    public Label getTitle() {
        return headerTitle;
    }
    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
    private QueueManagement queueManagement;
    public TextField getFilePathField () {
        return filePathField;
    }
    private void checkAndLoadFilePathInput() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select XML File");

        // Add a filter to show only .xml files
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml"));

        File selectedFile = fileChooser.showOpenDialog(loadFileButton.getScene().getWindow());

        // if the user didn't choose a file and exit from the red x
        if (selectedFile == null) {
            return;
        }

        try {
            mainController.setBodyDetails(selectedFile.getAbsolutePath());
        } catch (IllegalArgumentException e) {
            // Create and show an error alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File Format Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (FileFormatException e) {
            // Create and show an error alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File Format Error");
            alert.setHeaderText(null);
            alert.setContentText("Error loading the XML file.");
            alert.showAndWait();
        }
        catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File Format Error");
            alert.setHeaderText(null);
            alert.setContentText("The file that provided is not a valid file, try again with another file.");
            alert.showAndWait();
        }

    }

    private void setQueueBinds() {
        simulationsInProgressCounterLabel.textProperty().bind(Bindings.format("%,d",queueManagement.getSimulationsInProgress()));
        waitingSimulationsCounterLabel.textProperty().bind(Bindings.format("%,d",queueManagement.getSimulationsWaiting()));
        FinishedSimulationsCounterLabel.textProperty().bind(Bindings.format("%,d",queueManagement.getSimulationsFinished()));
    }

    public void setQueueManagement() {
        queueManagement = new QueueManagement();
        setQueueBinds();
    }
    public QueueManagement getQueueManagement() {
        return queueManagement;
    }
}
