package com.eventsecurityapp;

import com.eventsecurityapp.vista.LoginForm;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        System.out.println("¡EventSecurityApp iniciada con éxito!");

        // Iniciar la interfaz gráfica en el hilo de despacho de eventos (EDT) de Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            }
        });
    }
}
