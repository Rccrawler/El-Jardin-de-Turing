package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CubeVisualizer extends JFrame {

    private final CubePanel cubePanel;
    private int currentLayer;
    private final int totalLayers;
    private final JTextField layerTextField;

    public CubeVisualizer(String[][][] cubeData, int layerToShow) {
        super("Visualización del Cubo - Capa " + layerToShow);
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
}
