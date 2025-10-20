package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InventoryPanel extends JPanel {

    private static final int INVENTORY_SLOTS = 2;
    private static final int SLOT_SIZE = 50;

    public InventoryPanel() {
        setPreferredSize(new Dimension(SLOT_SIZE * INVENTORY_SLOTS, SLOT_SIZE));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        CreadorPropiedades creador = CreadorPropiedades.getInstance();
        if (creador != null) {
            List<ObjetoPropiedades> objetos = creador.getObjetos();
            for (int i = 0; i < INVENTORY_SLOTS; i++) {
                g.drawRect(i * SLOT_SIZE, 0, SLOT_SIZE, SLOT_SIZE);
                if (i < objetos.size()) {
                    // Aquí puedes dibujar el objeto. Por ahora, solo mostraremos el nombre.
                    //g.drawString(objetos.get(i).getNombre(), i * SLOT_SIZE + 5, 20);
                }
            }
        }
    }
}

