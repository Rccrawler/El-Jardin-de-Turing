package org.example;

import javax.swing.*;

public class Main {

    private static final int CAPAS = 200; // la 100 es la principal las otras son superiores o inferiores
    private static final int COLUMNAS = 50, FILAS = 50, CAPA_A_MOSTRAR = 100;

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