package org.example;

import java.util.ArrayList;
import java.util.List;

public class PersonajesPropiedades { // los objetos no se guardan en objetos propiedades los que son del personaje
    private static int contadorPersonajes = 0;
    private int personajeId;
    private double genero;
    private List<ObjetoPropiedades> objetos;
    private int edad;
    private String descripcion;
    private int vida;
    private int z;
    private int x;
    private int y;

    public PersonajesPropiedades(double genero, int edad, String descripcion, int z, int x, int y) {
        this.personajeId = ++contadorPersonajes;
        this.genero = genero;
        this.objetos = new ArrayList<>();
        this.edad = edad;
        this.descripcion = descripcion;
        this.vida = vida;
        this.z = z;
        this.x = x;
        this.y = y;
    }

    public boolean agregarObjeto(ObjetoPropiedades objeto) {
        if (objetos.size() < 2) {// dos lo que les cabe en la mano 1 en cada mano
            objetos.add(objeto);
            return true;
        }
        return false; // No se pudo agregar, inventario lleno
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public void setObjetos(List<ObjetoPropiedades> objetos) {
        this.objetos = objetos;
    }

    public double getGenero() {
        return genero;
    }

    public void setGenero(double genero) {
        this.genero = genero;
    }

    public int getPersonajeId() {
        return personajeId;
    }

    public void setPersonajeId(int personajeId) {
        this.personajeId = personajeId;
    }

    public static int getContadorPersonajes() {
        return contadorPersonajes;
    }

    public static void setContadorPersonajes(int contadorPersonajes) {
        PersonajesPropiedades.contadorPersonajes = contadorPersonajes;
    }

    public List<ObjetoPropiedades> getObjetos() {
        return objetos;
    }
}
