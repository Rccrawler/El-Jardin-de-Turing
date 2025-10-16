package org.example;

import java.util.ArrayList;
import java.util.List;

public class PersonajesPropiedades { // los objetos no se guardan en objetos propiedades los que son del personaje
    private static int contadorPersonajes = 0;
    private int personajeId;
    private double genero;
    private List<ObjetoPropiedades> objetos;

    public PersonajesPropiedades(double genero) {
        this.personajeId = ++contadorPersonajes;
        this.genero = genero;
        this.objetos = new ArrayList<>();
    }

    public boolean agregarObjeto(ObjetoPropiedades objeto) {
        if (objetos.size() < 2) {// dos lo que les cabe en la mano 1 en cada mano
            objetos.add(objeto);
            return true;
        }
        return false; // No se pudo agregar, inventario lleno
    }

    public int getPersonajeId() {
        return personajeId;
    }

    public double getGenero() {
        return genero;
    }

    public List<ObjetoPropiedades> getObjetos() {
        return objetos;
    }
}
