package org.example;

import java.util.Random;

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
                    cuadricula3d[z][x][y] = "x";// yenamos con suelo
                }
            }
        }
        hanadirMateriales();
    }

    public void hanadirMateriales(){ // hañadir materiales basicos de manera randon

        Random random = new Random();

        String[] almacen_iconos = {"\uD83E\uDEBE", "\uD83D\uDD29", "\uD83D\uDEE2\uFE0F", "\uD83D\uDCA1", "\uD83E\uDDF1"};

        for (String icono : almacen_iconos) {
            for (int i = 0; i < random.nextInt(50); i++) { // funcion para poner materiales en un mundo inicial de manera random
                int x = random.nextInt(this.columnas);
                int y = random.nextInt(this.filas);
                cuadricula3d[this.capas / 2][x][y] = icono;// hojo con los decimales en la operacion this.capas / 2
            }
        }
        hanadirMuros();
    }

    public void hanadirMuros(){
        // hañadir muros ha los bordes con propiedad dureza irompible
        // todos los lados del cubo
        String muro = "M"; // Representación de un muro irrompible

        // Caras superior e inferior (planos z)
        for (int x = 0; x < columnas; x++) {
            for (int y = 0; y < filas; y++) {
                cuadricula3d[0][x][y] = muro; // Cara inferior
                cuadricula3d[capas - 1][x][y] = muro; // Cara superior
            }
        }

        // Caras frontal y trasera (planos x)
        for (int z = 1; z < capas - 1; z++) {
            for (int y = 0; y < filas; y++) {
                cuadricula3d[z][0][y] = muro; // Cara frontal
                cuadricula3d[z][columnas - 1][y] = muro; // Cara trasera
            }
        }

        // Caras izquierda y derecha (planos y)
        for (int z = 1; z < capas - 1; z++) {
            for (int x = 1; x < columnas - 1; x++) {
                cuadricula3d[z][x][0] = muro; // Cara izquierda
                cuadricula3d[z][x][filas - 1] = muro; // Cara derecha
            }
        }
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
