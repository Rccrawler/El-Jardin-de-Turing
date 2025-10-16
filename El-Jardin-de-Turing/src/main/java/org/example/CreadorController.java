package org.example;

import java.awt.event.KeyEvent;

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
            cubeData[currentLayer][creatorY][creatorX] = "·"; // Dejar el espacio anterior
            cubeData[currentLayer][newY][newX] = "Ω"; // Mover a la nueva posición
            cubePanel.repaint(); // Repintar el panel para mostrar el cambio
        }
    }
}

