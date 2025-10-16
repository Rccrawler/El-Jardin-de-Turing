package org.example;

public class ObjetoPropiedades { // guardar ha qui las propiededes de los objetos
    private int z;
    private int x;
    private int y;
    private String tipo;
    private int dureza;
    private boolean irronpibilidad = false;
    private boolean trasportable; // si se puede cojer y mober o yebar en la mano o inventario
    // Constructor principal
    public ObjetoPropiedades(int z, int x, int y, String tipo, int dureza, boolean trasportable) {
        this.z = z;
        this.x = x;
        this.y = y;
        this.tipo = tipo;
        this.dureza = dureza;
        this.trasportable = trasportable;
    }

    // Constructor alternativo (dureza por defecto 0)
    public ObjetoPropiedades(int z, int x, int y, String tipo, boolean irronpibilidad, boolean trasportable) {
        this.z = z;
        this.x = x;
        this.y = y;
        this.tipo = tipo;
        this.dureza = 0;
        this.irronpibilidad = irronpibilidad;
        this.trasportable = trasportable;
    }

    // Getters
    public int getZ() { return z; }
    public int getX() { return x; }
    public int getY() { return y; }
    public String getTipo() { return tipo; }
    public int getDureza() { return dureza; }
    public boolean getirronpibilidad() {return irronpibilidad; }
    public boolean gettrasportable() {return trasportable; }
}
