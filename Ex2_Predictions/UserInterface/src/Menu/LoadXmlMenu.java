/*
package Menu;

import SystemLogic.history.imp.SimulationHistory;
import XmlLoader.generated.*;


import java.io.File;
import java.util.Scanner;

import static java.lang.System.out;

public class LoadXmlMenu {

    public static String loadXmlFile(String prevFilePath) {
        String xmlPath ="";
        Scanner scanner = new Scanner(System.in);
        xmlPath = scanner.next();
        xmlPath = CheckXmlInput(xmlPath, prevFilePath);

        return xmlPath;
    }



    public static void printInputFromSimulationMenu(SimulationHistory simulationHistory)
    {
        int userInput;
        Menu.infoByEntityOrByProperty();
        userInput = Menu.getIntInput(1, 2);
        userInput = getCorrectIntInput(1,2,userInput);
        if(userInput == 1){
            System.out.println(simulationHistory.getBeforeAfterEntitiesAmount());
        }
        else if(userInput ==2) {

            simulationHistory.printEntitys();
            System.out.println("Please choose entity number uow want to get information on.");
            userInput = Menu.getIntInput(1, simulationHistory.getNumberOfEntitys());
            int userEntity = getCorrectIntInput(1, simulationHistory.getNumberOfEntitys(), userInput);

            StringBuilder propertyList = simulationHistory.getPropertiesList(userEntity);

            System.out.println("please enter the number of property you want to check: ");
            System.out.println(propertyList);

            int propertyChoiceInput = Menu.getIntInput(1, simulationHistory.getNumberOfProperty());
            propertyChoiceInput = getCorrectIntInput(1,simulationHistory.getNumberOfProperty(), propertyChoiceInput);
            System.out.println(simulationHistory.getPropertyHistory(userEntity,propertyChoiceInput));
        }
    }

    public int getInputFromMenu(Menu menu)
    {
        Menu.menuOptions();
        int StartInput = Menu.getIntInput(1, 5);
        getCorrectIntInput(1,4,StartInput);
        return StartInput;
    }
    public static int getCorrectIntInput(int from , int to , int userInput){
        while (!Menu.isInputValid(from, to, userInput)) {
            out.println("Invalid Input! input can be only number between " + from +"-" + to + " try again.");
            out.print("Your Choice: ");
            userInput = Menu.getIntInput(from, to);
        }
        return userInput;
    }
    public static String CheckXmlInput(String xmlPath, String prevFilePath) {
        boolean wantToExit = false;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            if (xmlPath.equals("7")) {
                wantToExit = true;
                break;
            }

            if (!xmlPath.endsWith(".xml")) {
                System.out.println("Only .xml files are supported! Try again, or press 7 to exit:");
                xmlPath = scanner.next();
            } else if (isXmlPathValid(xmlPath)) {
                System.out.println("File found and loaded successfully!\n");
                break;
            } else {
                System.out.print("Your file does not exist in the provided path. Try again or press 7 to exit: ");
                xmlPath = scanner.next();
            }
        }

        return wantToExit ? prevFilePath : xmlPath;
    }


    public void xmlMenuLoader() {
        out.print("Enter your xml file full path: ");
    }

    public static boolean isXmlPathValid(String xmlPath) {
        File file = new File(xmlPath);
        return file.exists() && file.isFile();
    }

    public void fileNotExist() {
        out.println("Your path to file is incorrect please try again or press 9 to go back to main menu");
        out.println("details");
    }

    */
/*public static void PrintSimulationDetails(PRDWorld prdWorld) {
        prdWorld.getPRDEntities().getPRDEntity().forEach(prdEntity -> {
            System.out.println("Entity name: " + prdEntity.getName());
            System.out.println("\tPopulation amount: " + prdEntity.getPRDPopulation());
            prdEntity.getPRDProperties().getPRDProperty().forEach(prdProperty -> {
                System.out.println("\tProperty name: " + prdProperty.getPRDName());
                System.out.println("\t\tProperty type: " + prdProperty.getType());
                if( prdProperty.getPRDRange() != null){
                    System.out.println("\t\tProperty Range is From " + prdProperty.getPRDRange().getFrom() +" to " + prdProperty.getPRDRange().getTo());
                }
                System.out.println("\t\tIs Range random initialize: " + prdProperty.getPRDValue().isRandomInitialize());

            });
        });

        prdWorld.getPRDRules().getPRDRule().forEach(prdRule -> {
            System.out.println("\tRule name:" + prdRule.getName());
            if(prdRule.getPRDActivation() != null){
                if(prdRule.getPRDActivation().getTicks() != null)
                { System.out.println("\t\tRule is active every " + prdRule.getPRDActivation().getTicks() + " ticks");}
                if(prdRule.getPRDActivation().getProbability() != null)
                {System.out.println("\t\tRule is active in probability of: " +prdRule.getPRDActivation().getProbability() );}

            }
            System.out.println("\t\t\tNumber of actions:" + prdRule.getPRDActions().getPRDAction().size());
            prdRule.getPRDActions().getPRDAction().forEach(prdAction ->
                    System.out.println("\t\t\tAction Type: "+ prdAction.getType()));
        });
        prdWorld.getPRDTermination().getPRDBySecondOrPRDByTicks().forEach(prdTermination->{
            if(prdTermination.toString().contains("Ticks"))
            {
                System.out.println("The Simulation will stop after "+ ((PRDByTicks)prdTermination).getCount() + " Ticks.");
            }
            if(prdTermination.toString().contains("Second"))
            {
                System.out.println("The Simulation will stop after "+ ((PRDBySecond)prdTermination).getCount() + " Seconds.\n");
            }

        });

    }*//*

}
*/
