package design.body.execution.pageManager.imp;

import XmlLoader.schema.PRDEnvProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TextField;

import java.util.Map;

public class ManagerUtils {

    /*<--------------------------- Checkers --------------------------->*/

    public static void checkIfDoubleInput(String newValue, TextField textField, BooleanProperty correctTextField, PRDEnvProperty environment, Map<String, Object> environmentsMap) {
        try {
            double inputNumber = Double.parseDouble(newValue);
            if (isInsideRange(inputNumber, environment)) {
                correctTextField.set(true);
                environmentsMap.put(environment.getPRDName(),inputNumber);
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

    public static boolean isInsideRange(double inputNumber, PRDEnvProperty environment) {
        if (environment.getPRDRange() != null) {
            double from = environment.getPRDRange().getFrom();
            double to = environment.getPRDRange().getTo();

            //my swap function
            if (from > to) {
                double tmp = from;
                from = to;
                to = tmp;
            }

            return !(inputNumber > to) && !(inputNumber < from);

        }
        return true;
    }

    public static void checkIfIntegerInput(String newValue, TextField textField, BooleanProperty correctTextField, SimpleIntegerProperty populationAmount) {
        try {
            int inputNumber = Integer.parseInt(newValue);
            if (isCorrectPopulationAmountInput(inputNumber, populationAmount)) {
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

    public static boolean isCorrectPopulationAmountInput(int inputNumber, SimpleIntegerProperty populationAmount) {
        return inputNumber >= 0 && inputNumber <= populationAmount.get();
    }

    public static boolean isTextField(Object objField) {
        return objField instanceof TextField;
    }
}
