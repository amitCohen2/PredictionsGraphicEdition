package design.app;

import XmlLoader.XmlLoader;
import XmlLoader.exceptions.FileFormatException;
import XmlRunner.XmlRunner;
import design.body.BodyController;
import design.header.HeaderController;
import design.header.QueueManagement;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class AppController {

    @FXML private HeaderController headerComponentController;
    @FXML private BodyController bodyComponentController;

    @FXML
    public void initialize() {
        if (headerComponentController != null && bodyComponentController != null) {
            headerComponentController.setMainController(this);
            bodyComponentController.setMainController(this);

            // set the bind between the Windows - results, details, new execution
            headerComponentController.getTitle().textProperty().bind(Bindings.selectString(bodyComponentController.headerValueProperty()));
        }
    }

    public void setFilePathField(String filePath) {
        headerComponentController.getFilePathField().setText(filePath);
    }

    public void setHeaderComponentController(HeaderController headerComponentController) {
        this.headerComponentController = headerComponentController;
        headerComponentController.setMainController(this);
    }

    public void setBodyDetails(String filePath) throws FileFormatException {
        if (stillSimulationRunning()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("There is execution that still running!");
            alert.setHeaderText(null);
            alert.setContentText("There is execution that still running!\n Can't load new file till all the simulations will finish..");
            alert.showAndWait();
            return;
        } else {
            headerComponentController.setQueueManagement();
        }
        setFilePathField(filePath);
        XmlLoader xmlLoader = new XmlLoader(filePath);
        bodyComponentController.setXmlInBody(xmlLoader, headerComponentController.getQueueManagement());
    }

    private boolean stillSimulationRunning() {
        return bodyComponentController.checkIfSimulationRun();
    }

    public String getFilePath() {
        return headerComponentController.getFilePath();
    }
    public void setBodyComponentController(BodyController bodyComponentController) {
        this.bodyComponentController = bodyComponentController;
        bodyComponentController.setMainController(this);
    }

}
