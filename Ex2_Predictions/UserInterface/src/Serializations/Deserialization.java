package Serializations;

import Menu.MenuData;
import java.io.*;
public class Deserialization {
    public static MenuData deserializeMenuData() {
        MenuData menuData = null;
        try {
            FileInputStream fileIn = new FileInputStream("menu_data.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            menuData = (MenuData) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return menuData;
    }
}
