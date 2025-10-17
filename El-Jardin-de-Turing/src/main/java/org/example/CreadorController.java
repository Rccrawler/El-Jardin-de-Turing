package org.example;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class CreadorController { // movimiento exclusibo del creador

    private final String[][][] cubeData;
    private final CubePanel cubePanel;

    public CreadorController(String[][][] cubeData, CubePanel cubePanel) {
        this.cubeData = cubeData;
        this.cubePanel = cubePanel;
    }

    public void move(int keyCode, int currentLayer) {
        // Encontrar la posición actual del creador "Ω"
        int creatorX = -1, creatorY = -1;
        for (int y = 0; y < cubeData[currentLayer].length; y++) {
            for (int x = 0; x < cubeData[currentLayer][y].length; x++) {
                if ("Ω".equals(cubeData[currentLayer][y][x])) {
                    creatorX = x;
                    creatorY = y;
                    break;
                }
            }
            if (creatorX != -1) {
                break;
            }
        }

        if (creatorX == -1) {
            return; // No se encontró el creador en la capa actual
        }

        int newX = creatorX;
        int newY = creatorY;

        switch (keyCode) {
            case KeyEvent.VK_W:
                newY--;
                break;
            case KeyEvent.VK_A:
                newX--;
                break;
            case KeyEvent.VK_S:
                newY++;
                break;
            case KeyEvent.VK_D:
                newX++;
                break;
        }

        // Verificar si la nueva posición es válida
        if (newX >= 0 && newX < cubeData[currentLayer][0].length && newY >= 0 && newY < cubeData[currentLayer].length) {
            // Mover el creador
            if(cubeData[currentLayer][newY][newX].equals(" ")){ // si no hay obstaculos deja moberse
                cubeData[currentLayer][creatorY][creatorX] = " "; // Dejar el espacio anterior
                cubeData[currentLayer][newY][newX] = "Ω"; // Mover a la nueva posición
                CreadorPropiedades creador = CreadorPropiedades.getInstance();
                creador.setZ(currentLayer);
                creador.setX(newX);
                creador.setY(newY);
                cubePanel.repaint(); // Repintar el panel para mostrar el cambio
                System.out.println();
                System.out.println("creador movido a "+ currentLayer + " " + newY + " " + newX);
                System.out.println("-----------------------------");
            }else {
                System.out.println(); // si hay estaculos no deja moberse
                System.out.println("no puede moberse colisionaria");
                System.out.println("-----------------------------");
            }
        }
    }
}

