package main;

import interfaz.loginUI;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        AppBD.inicializar();
        SwingUtilities.invokeLater(() -> new loginUI());
    }
}