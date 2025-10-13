package org.example;

import javax.swing.*;
import java.awt.*;

public class CubePanel extends JPanel {
    private final String[][][] data;
    private int currentLayer;

    // Fuente monoespaciada consistente para todo el panel
    private final Font gridFont = new Font(Font.MONOSPACED, Font.PLAIN, 18);
    // Padding alrededor del contenido
    private final int padding = 0;
    private final int titlePadding = 12;

    public CubePanel(String[][][] data, int layer) {
        this.data = data;
        this.currentLayer = layer;
        setBackground(Color.WHITE);
        setDoubleBuffered(true);
    }

    public void setCurrentLayer(int newLayer) {
        if (newLayer >= 0 && newLayer < data.length) {
            this.currentLayer = newLayer;
            repaint();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (data == null || currentLayer < 0 || currentLayer >= data.length) {
            return new Dimension(200, 200);
        }
        String[][] layer = data[currentLayer];

        // Obtener métricas para ambas fuentes
        FontMetrics titleFm = getFontMetrics(gridFont.deriveFont(Font.BOLD, 14f));
        FontMetrics gridFm = getFontMetrics(gridFont);

        // Calcular altura del título y de la cuadrícula por separado
        int titleHeight = titlePadding + titleFm.getHeight();
        int yStep = gridFm.getAscent() - 8; // Coincide con el yStep de paintComponent
        int gridHeight = layer.length * yStep + 30; // Usa el yStep para el cálculo

        // Calcular dimensiones totales
        int cols = layer[0].length;
        int width = padding + cols * gridFm.charWidth('M') + padding;
        int height = titleHeight + gridHeight + padding;

        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Usar Graphics2D y ajustar hints para texto nítido
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            // 1. Dibujar el título primero
            Font titleFont = gridFont.deriveFont(Font.BOLD, 14f);
            g2.setFont(titleFont);
            FontMetrics titleFm = g2.getFontMetrics();
            int titleBaseline = titlePadding + titleFm.getAscent();
            g2.drawString("--- Capa " + currentLayer + " ---", titlePadding, titleBaseline);

            // 2. Configurar para la cuadrícula
            g2.setFont(gridFont);
            FontMetrics gridFm = g2.getFontMetrics();
            int xStep = gridFm.charWidth('M');
            int yStep = gridFm.getAscent() - 8; // Usar solo el ascenso para juntar las líneas

            // 3. Posición de inicio de la cuadrícula (debajo del título)
            int startX = padding;
            int startY = titlePadding + titleFm.getHeight();

            // 4. Validación de datos
            if (data == null || currentLayer >= data.length || currentLayer < 0) {
                g2.drawString("Capa no válida.", startX, startY + gridFm.getAscent());
                return;
            }

            String[][] layer = data[currentLayer];

            // 5. Dibujar la cuadrícula
            for (int x = 0; x < layer.length; x++) {
                for (int y = 0; y < layer[0].length; y++) {
                    int drawX = startX + (y * xStep);
                    // La posición Y es la base de la línea + el ascenso de la fuente
                    int drawY = startY + (x * yStep) + gridFm.getAscent();
                    g2.drawString(layer[x][y], drawX, drawY);
                }
            }
        } finally {
            g2.dispose();
        }
    }
}
