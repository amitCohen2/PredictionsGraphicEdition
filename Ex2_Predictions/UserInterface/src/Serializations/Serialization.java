package Serializations;

import Menu.MenuData;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.*;
public class Serialization {
    public static void serializeMenuData(MenuData menuData) {
        try {
            FileOutputStream fileOut = new FileOutputStream("menu_data.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(menuData);
            out.close();
            fileOut.close();
            System.out.println("Menu data saved to menu_data.ser\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MenuData deserializeMenuData() throws FileNotFoundException {
        MenuData menuData = null;
        try {
            FileInputStream fileIn = new FileInputStream("menu_data.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            menuData = (MenuData) in.readObject();
            in.close();
            fileIn.close();
        } catch (FileNotFoundException e) {
            throw e;  // Rethrow the exception
        } catch (Exception e) {
            throw new RuntimeException("Error while deserializing menu data.", e);
        }
        return menuData;
    }
}
