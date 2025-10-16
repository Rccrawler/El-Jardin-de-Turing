/*
* @author Rccrawler
* @ https://github.com/Rccrawler/El-Jardin-de-Turing.git
*/

package org.example;

import javax.swing.*;

public class Main {

    // crear metodo para defenir en un solo sitio todos los iconos y asignaciones
    private static final int CAPAS = 201; // la 100 es la principal las otras son superiores o inferiores
    private static final int COLUMNAS = 50, FILAS = 50, CAPA_A_MOSTRAR = 100; // 50x50=2500 posiciones ha usar

    public static void main(String[] args) {
        // 1. Crear el modelo de datos
        SectorWorld sectorWorld1 = new SectorWorld(CAPAS, COLUMNAS, FILAS);
        SectorWorld sectorWorld2 = new SectorWorld(CAPAS, COLUMNAS, FILAS);

        sectorWorld1.llenarCubo();
        sectorWorld2.llenarCubo();

        // 2. Ejecutar la creación de la GUI en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            CubeVisualizer visualizer = new CubeVisualizer(sectorWorld1.getCuboData(), CAPA_A_MOSTRAR);
            visualizer.setVisible(true);
        });
    }
}