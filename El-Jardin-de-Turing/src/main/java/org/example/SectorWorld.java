package org.example;

public class SectorWorld {
    private final int capas;
    private final int columnas;
    private final int filas;
    private final String[][][] cuadricula3d;

    public SectorWorld(int capas, int columnas, int filas) {
        this.capas = capas;
        this.columnas = columnas;
        this.filas = filas;
        this.cuadricula3d = new String[capas][columnas][filas];
    }

    public void llenarCubo() { // en caso de que no haya uno de ese sector guardado
        for (int z = 0; z < capas; z++) {
            for (int x = 0; x < columnas; x++) {
                for (int y = 0; y < filas; y++) {
                    cuadricula3d[z][x][y] = " ";// yenamos con bacio
                }
            }
        }
        llenarSuelo();
    }

    public void llenarSuelo() {
        for (int z = 0; z < 100; z++) { // yenar todo por debajo de la capa 100
            for (int x = 0; x < columnas; x++) {
                for (int y = 0; y < filas; y++) {
                    cuadricula3d[z][x][y] = "x";// yenamos con bacio
                }
            }
        }
        hanadirMateriales();
    }

    public void hanadirMateriales(){ // hañadir materiales basicos de manera randon
        for (int x = 0; x < columnas; x++) {
            for (int y = 0; y < filas; y++) {
                cuadricula3d[100][x][y] = "";
            }
        }
        hanadirMuros();
    }

    public void hanadirMuros(){
        // hañadir muros ha los bordes con propiedad dureza irompible
        // todos los lados del cubo
    }

    public String[][][] getCuboData() {
        return cuadricula3d;
    }

    public int getCapas() {
        return capas;
    }

    public int getColumnas() {
        return columnas;
    }

    public int getFilas() {
        return filas;
    }
}

