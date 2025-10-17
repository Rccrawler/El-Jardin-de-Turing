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
        CreadorPropiedades creador = CreadorPropiedades.getInstance(true, 20, "programador", 100, 0, 0, 0);
        sectorWorld1.PostEspawnCreador();

        sectorWorld2.llenarCubo();

        // 2. Ejecutar la creación de la GUI en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            CubeVisualizer visualizer = new CubeVisualizer(sectorWorld1.getCuboData(), CAPA_A_MOSTRAR);
            visualizer.setVisible(true);
        });
    }
}


/*
// Crear un nuevo personaje
PersonajesPropiedades personaje1 = new PersonajesPropiedades(1.0); // Por ejemplo, 1.0 para masculino

// Crear dos objetos
ObjetoPropiedades espada = new ObjetoPropiedades(0, 0, 0, "espada", 10, true);
ObjetoPropiedades escudo = new ObjetoPropiedades(0, 0, 0, "escudo", 15, true);

// Asignar los objetos al personaje
personaje1.agregarObjeto(espada);
personaje1.agregarObjeto(escudo);

// Para identificar al personaje, puedes usar:
int idDelPersonaje = personaje1.getPersonajeId(); // Obtendrá 1

// Para ver los objetos que tiene el personaje:
List<ObjetoPropiedades> objetosDelPersonaje = personaje1.getObjetos();
* */