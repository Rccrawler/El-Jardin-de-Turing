package org.example;

import java.util.ArrayList;
import java.util.List;

public class CreadorPropiedades {

    // 1. La única instancia estática de la clase
    private static CreadorPropiedades instance;

    // 2. Las propiedades ahora no son estáticas, pertenecen al objeto
    private int personajeId;
    private boolean genero;
    private List<ObjetoPropiedades> objetos;
    private int edad;
    private String descripcion;
    private int vida;
    private int z;
    private int x;
    private int y;

    // 3. El constructor es privado para que solo se pueda llamar desde esta clase
    private CreadorPropiedades(boolean genero, int edad, String descripcion, int vida, int z, int x, int y) {
        this.personajeId = 1; // Como solo hay uno, el ID puede ser fijo
        this.genero = genero;
        this.objetos = new ArrayList<>();
        this.edad = edad;
        this.descripcion = descripcion;
        this.vida = vida;
        this.z = z;
        this.x = x;
        this.y = y;
    }

    // 4. Método estático para obtener (y crear si es necesario) la única instancia
    public static CreadorPropiedades getInstance(boolean genero, int edad, String descripcion, int vida, int z, int x, int y) {
        if (instance == null) {
            instance = new CreadorPropiedades(genero, edad, descripcion, vida, z, x, y);
        }
        return instance;
    }

    // Sobrecarga para obtener la instancia una vez que ya ha sido creada
    public static CreadorPropiedades getInstance() {
        if (instance == null) {
            throw new IllegalStateException("La instancia de CreadorPropiedades no ha sido creada todavía.");
        }
        return instance;
    }

    // 5. Todos los getters y setters ya no son estáticos
    public boolean agregarObjeto(ObjetoPropiedades objeto) {
        if (objetos.size() < 2) { // dos lo que les cabe en la mano 1 en cada mano
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

    public List<ObjetoPropiedades> getObjetos() {
        return objetos;
    }

    public void setObjetos(List<ObjetoPropiedades> objetos) {
        this.objetos = objetos;
    }

    public boolean getGenero() {
        return genero;
    }

    public void setGenero(boolean genero) {
        this.genero = genero;
    }

    public int getPersonajeId() {
        return personajeId;
    }

    public void setPersonajeId(int personajeId) {
        this.personajeId = personajeId;
    }
}
