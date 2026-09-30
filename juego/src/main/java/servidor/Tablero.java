package main.java.servidor;

import java.util.Random;

/**
 * Tablero fijo de 10x10 con minas al azar.
 * Versión simplificada: las casillas adyacentes no revelan información.
 */
public class Tablero {

    public static final int TAMANO = 10;
    public static final int MINAS = 15;

    public static final int FUERA_DE_RANGO = 0;
    public static final int REPETIDA = 1;
    public static final int SEGURA = 2;
    public static final int BOMBA = 3;
    public static final int VICTORIA = 4;

    private final boolean[][] bombas = new boolean[TAMANO][TAMANO];
    private final boolean[][] abiertas = new boolean[TAMANO][TAMANO];
    private int seguras = TAMANO * TAMANO - MINAS;

    public Tablero() {
        Random azar = new Random();
        int puestas = 0;
        while (puestas < MINAS) {
            int c = azar.nextInt(TAMANO);
            int f = azar.nextInt(TAMANO);
            if (!bombas[f][c]) {
                bombas[f][c] = true;
                puestas++;
            }
        }
    }

    public synchronized int destapar(int columna, int fila) {
        if (columna < 0 || columna >= TAMANO || fila < 0 || fila >= TAMANO) {
            return FUERA_DE_RANGO;
        }
        if (abiertas[fila][columna]) {
            return REPETIDA;
        }
        abiertas[fila][columna] = true;
        if (bombas[fila][columna]) {
            return BOMBA;
        }
        seguras--;
        return seguras == 0 ? VICTORIA : SEGURA;
    }

    /** Devuelve TAMANO líneas: '-' oculta, 'O' segura abierta, '*' bomba. */
    public synchronized String[] filas(boolean mostrarBombas) {
        String[] filas = new String[TAMANO];
        for (int f = 0; f < TAMANO; f++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < TAMANO; c++) {
                char simbolo = '-';
                if (abiertas[f][c]) {
                    simbolo = bombas[f][c] ? '*' : 'O';
                } else if (mostrarBombas && bombas[f][c]) {
                    simbolo = '*';
                }
                if (c > 0) {
                    sb.append(' ');
                }
                sb.append(simbolo);
            }
            filas[f] = sb.toString();
        }
        return filas;
    }
}
