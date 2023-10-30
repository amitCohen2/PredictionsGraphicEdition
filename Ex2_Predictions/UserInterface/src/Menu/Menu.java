/*
package Menu;
import SystemLogic.Expression.exceptions.SimulationHistoryNotFoundException;
import SystemLogic.history.imp.HistoryArchive;
import SystemLogic.history.imp.SimulationHistory;
import XmlRunner.XmlRunner;
import Serializations.*;

import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;

import static Menu.LoadXmlMenu.getCorrectIntInput;
import static java.lang.System.out;
public class Menu {

    public static void MenuRunner() throws SimulationHistoryNotFoundException {
        HistoryArchive historyArchive = new HistoryArchive();
        int simulationsCounter = historyArchive.getSimulationsNumber() + 1;

        boolean isUserWantToExit = false;
        String pathXmlFile = "";
        XmlRunner xmlRunner = new XmlRunner();


        welcome();

        while (!isUserWantToExit) {
            menuOptions();

            int StartInput = getIntInput(1, 7);
            switch (StartInput) {
                case 1:
                    System.out.println("You chose option 1");
                    out.print("Enter your xml file full path: ");
                    pathXmlFile = LoadXmlMenu.loadXmlFile(pathXmlFile);
                    if (!Objects.equals(pathXmlFile, "")) {
                        try {
                            xmlRunner.initRunSimulation(pathXmlFile, simulationsCounter, historyArchive);

                        } catch (IllegalArgumentException e) {
                            out.println(e.getMessage());
                            pathXmlFile = "";
                            xmlRunner.setIsInit(false);
                        }
                    }
                    break;
                case 2:
                    System.out.println("You chose option 2");
                    showSimulationDetailsOption(pathXmlFile, xmlRunner);
                    break;
                case 3:
                    System.out.println("You chose option 3");
                    runSimulationOption(xmlRunner);
                    simulationsCounter++;
                  break;
                case 4:
                    System.out.println("You chose option 4\n");
                    try {
                        showSimulationArchiveOptions(xmlRunner, historyArchive.getSimulationsNumber());
                    } catch (SimulationHistoryNotFoundException | NullPointerException e) {
                        out.println("There is no history of simulations to show");
                    }
                    break;
                case 5:
                    System.out.println("You chose option 5 - Load.");
                    try {
                        MenuData loadedData = Serialization.deserializeMenuData();
                        if (loadedData != null) {
                            pathXmlFile = loadedData.getPathXmlFile();
                            historyArchive = loadedData.getHistoryArchive();
                            simulationsCounter = historyArchive.getSimulationsNumber();
                            xmlRunner.initRunSimulation(pathXmlFile,simulationsCounter,historyArchive);
                            System.out.println("Previews system state was loaded successfully\n");

                        } else {
                            System.out.println("No saved data found.");
                        }
                    } catch (Exception e) {
                        System.out.println("There is no previous System State to load.");
                    }
                    break;
                case 6:
                    System.out.println("You chose option 6 - Save.");
                    MenuData dataToSave = new MenuData(pathXmlFile, historyArchive);
                    Serialization.serializeMenuData(dataToSave);
                    break;
                case 7:
                    System.out.println("You chose option 7 - Exit.");
                    System.out.println("bye bye...");
                    isUserWantToExit = true;
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private static void runSimulationOption(XmlRunner xmlRunner) {
        if (xmlRunner.getIsInit()) {
            xmlRunner.Run();
        }
        else {
            out.println("You have to load an xml file first!");
        }
    }


    public static void showSimulationArchiveOptions(XmlRunner xmlRunner, int simulationsCounter) throws SimulationHistoryNotFoundException {

        xmlRunner.printPreviousSimulationHistory();
        System.out.println("Choose an simulation number from this list.");
        System.out.print("You choose: ");

        Scanner scanner = new Scanner(System.in);
        int usetinput = scanner.nextInt();
        usetinput = getCorrectIntInput(1, simulationsCounter, usetinput);
        try {
            SimulationHistory simulationHistory = xmlRunner.getSimulationById(usetinput);
            LoadXmlMenu.printInputFromSimulationMenu(simulationHistory);
        } catch (IndexOutOfBoundsException e) {
            out.println(e.getMessage());
        }
    }

    public static void showSimulationDetailsOption(String pathXmlFile, XmlRunner xmlRunner) {
        if (Objects.equals(pathXmlFile, "")) {
            out.println("You have to load an xml file first!");
        }
        else if (xmlRunner.getSimulationDetails().getWorld() != null){
            //LoadXmlMenu.PrintSimulationDetails(xmlRunner.getWorld());
        }
        else {
            out.println("Some excepted error occur!");
        }
    }
    public static int getIntInput(int from, int to)
    {
        int StartInput =0;
        boolean isValid =false;
        while(!isValid) {
            Scanner scanner = new Scanner(System.in);

            try {
                StartInput = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Error: Input is not a valid number.");
                isValid = false;
            }

            if (isInputValid(from,to, StartInput)) {
                isValid = true;
            }
            else {
                System.out.println("Invalid Input! input can be only number between " + from + " - " + to + " Try again.");
                System.out.print("Your Choice again: ");
            }
        }
        return StartInput;
    }

    public static boolean getBooleanInput()
    {
        boolean StartInput = false;
        boolean isValid =false;
        while(!isValid){
            Scanner scanner = new Scanner(System.in);
            try {
                StartInput = scanner.nextBoolean();
                isValid = true;
            } catch (InputMismatchException e) {
                System.out.println("Error: Input can be true or false only");
            }
        }
        return StartInput;
    }
    public static String getStringInput()
    {
        Scanner scanner = new Scanner(System.in);
        return scanner.next();
    }
    public static double getFloatInput(double from, double to)
    {
        double StartInput =0;
        boolean isValid = false;
        while(!isValid) {
            Scanner scanner = new Scanner(System.in);

            try {
                StartInput = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Error: Input is not a valid number.");
                isValid = false;
            }

            if (isInputValid(from,to, StartInput)) {
                isValid = true;
            }
            else {
                System.out.println("Invalid Input! input can be only number between " + from + " - " + to + " Try again.");
                System.out.print("Your Choice: ");
            }
        }
        return StartInput;
    }
    public static void infoByEntityOrByProperty(){
        out.println("choose the option that you want by pressing the number of the option and the enter.");
        out.println("1. Show me information on a certain Entity");
        out.println("2. Show me information on a certain Property");
    }

    public static void welcome() {
        System.out.println("Welcome to AVIV & AMIT JAVA - World Prediction!");
    }

    public static void menuOptions() {
        System.out.println("choose the option that you want be pressing the number of the option and the enter: ");
        System.out.println("1. load xml file into the system.");
        System.out.println("2. Show Simulation Details.");
        System.out.println("3. Run Simulation.");
        System.out.println("4. Show Past Simulations details.");
        System.out.println("5. Load previous System state.");
        System.out.println("6. Save current System State.");
        System.out.println("7. Exit.");
        System.out.print("Your Choice: ");
    }
        public static boolean isInputValid(double from, double to, double userInput) {
        return (from <= userInput && userInput <= to);
    }


}*/
