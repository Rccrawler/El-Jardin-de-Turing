package org.example;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;

public class CubeVisualizer extends JFrame implements KeyListener {

    private final CubePanel cubePanel;
    private int currentLayer;
    private final int totalLayers;
    private final JFormattedTextField layerTextField;
    private final String[][][] cubeData;
    private final CreadorController creadorController;

    public CubeVisualizer(String[][][] cubeData, int layerToShow) {
        super("Visualización del Cubo - Capa " + layerToShow);
        this.cubeData = cubeData;
        this.currentLayer = layerToShow;
        this.totalLayers = cubeData.length;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear el panel que dibujará el cubo
        cubePanel = new CubePanel(cubeData, layerToShow);
        add(cubePanel, BorderLayout.CENTER);

        creadorController = new CreadorController(cubeData, cubePanel);

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

        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setAllowsInvalid(false); // No permite caracteres no válidos
        formatter.setMinimum(0);
        formatter.setMaximum(totalLayers - 1);

        layerTextField = new JFormattedTextField(formatter);
        layerTextField.setValue(currentLayer);
        layerTextField.setColumns(5);

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
            layerTextField.setValue(currentLayer);
            requestFocusInWindow(); // Devolver el foco a la ventana principal
        }
    }

    private void goToLayerFromTextField(ActionEvent e) {
        try {
            int layer = (int) layerTextField.getValue();
            changeLayer(layer);
        } catch (Exception ex) {
            // Si hay un error, restauramos al valor actual
            layerTextField.setValue(currentLayer);
        }
        requestFocusInWindow(); // Devolver el foco a la ventana principal
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No es necesario implementar esto
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        creadorController.move(keyCode, currentLayer);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No es necesario implementar esto
    }
}
