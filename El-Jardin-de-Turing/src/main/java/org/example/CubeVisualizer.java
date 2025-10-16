package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CubeVisualizer extends JFrame implements KeyListener {

    private final CubePanel cubePanel;
    private int currentLayer;
    private final int totalLayers;
    private final JTextField layerTextField;
    private final String[][][] cubeData;

    public CubeVisualizer(String[][][] cubeData, int layerToShow) {
        super("Visualización del Cubo - Capa " + layerToShow);
        this.cubeData = cubeData;
        this.currentLayer = layerToShow;
        this.totalLayers = cubeData.length;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear el panel que dibujará el cubo
        cubePanel = new CubePanel(cubeData, layerToShow);
        add(cubePanel, BorderLayout.CENTER);

        // Crear y configurar el menú
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Navegación");
        menuBar.add(menu);

        JMenuItem prevItem = new JMenuItem("Capa Anterior");
        prevItem.addActionListener(e -> changeLayer(currentLayer - 1));
        menu.add(prevItem);

        JMenuItem nextItem = new JMenuItem("Capa Siguiente");
        nextItem.addActionListener(e -> changeLayer(currentLayer + 1));
        menu.add(nextItem);

        menu.addSeparator();

        // Panel para ir a una capa específica
        JPanel goToPanel = new JPanel();
        goToPanel.add(new JLabel("Ir a capa:"));
        layerTextField = new JTextField(String.valueOf(currentLayer), 5);
        layerTextField.addActionListener(this::goToLayerFromTextField);
        goToPanel.add(layerTextField);
        menuBar.add(goToPanel);

        setJMenuBar(menuBar);

        // Ajustar tamaño y mostrar
        pack();
        setLocationRelativeTo(null);
        setResizable(false);

        // Listener para el teclado
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
    }

    private void changeLayer(int newLayer) {
        if (newLayer >= 0 && newLayer < totalLayers) {
            currentLayer = newLayer;
            setTitle("Visualización del Cubo - Capa " + currentLayer);
            cubePanel.setCurrentLayer(currentLayer);
            layerTextField.setText(String.valueOf(currentLayer));
        }
    }

    private void goToLayerFromTextField(ActionEvent e) {
        try {
            int layer = Integer.parseInt(layerTextField.getText());
            changeLayer(layer);
        } catch (NumberFormatException ex) {
            // Si el texto no es un número válido, lo restauramos al valor actual
            layerTextField.setText(String.valueOf(currentLayer));
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No es necesario implementar esto
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        moveCreator(keyCode);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No es necesario implementar esto
    }

    private void moveCreator(int keyCode) {
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
