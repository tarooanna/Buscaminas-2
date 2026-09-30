package main.java.servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Servidor TCP del buscaminas. Todos los jugadores comparten un único tablero y cada
 * jugada se difunde a todos los clientes conectados.
 */
public class Servidor {

    public static final int PUERTO = 5000;

    private static final List<ManejadorCliente> clientes = new ArrayList<ManejadorCliente>();
    private static Tablero tablero = new Tablero();
    private static int contador = 0;

    public static void main(String[] args) {
        int puerto = args.length > 0 ? Integer.parseInt(args[0]) : PUERTO;
        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("Servidor de buscaminas iniciado en el puerto " + puerto);
            while (true) {
                Socket socket = servidor.accept();
                new ManejadorCliente(socket).start();
            }
        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }

    static synchronized int registrar(ManejadorCliente cliente) {
        clientes.add(cliente);
        return ++contador;
    }

    static synchronized void retirar(ManejadorCliente cliente) {
        clientes.remove(cliente);
    }

    static synchronized void enviarTableroA(ManejadorCliente cliente) {
        enviarTablero(cliente, false);
    }

    static synchronized void jugar(ManejadorCliente jugador, int columna, int fila) {
        int resultado = tablero.destapar(columna, fila);
        String quien = "Jugador " + jugador.getNumero();

        if (resultado == Tablero.FUERA_DE_RANGO) {
            jugador.enviar("Coordenadas fuera del tablero (usa valores de 0 a " + (Tablero.TAMANO - 1) + ")");
        } else if (resultado == Tablero.REPETIDA) {
            jugador.enviar("Esa casilla ya está destapada, elige otra");
        } else if (resultado == Tablero.SEGURA) {
            difundir(quien + " destapó (" + columna + ", " + fila + ") y está libre");
            difundirTablero(false);
        } else if (resultado == Tablero.BOMBA) {
            difundir(quien + " pisó una bomba en (" + columna + ", " + fila + ")");
            difundirTablero(true);
            difundir("BOMBA");
            terminarPartida();
        } else if (resultado == Tablero.VICTORIA) {
            difundir(quien + " destapó la última casilla segura");
            difundirTablero(true);
            difundir("VICTORIA");
            terminarPartida();
        }
    }

    private static void difundir(String mensaje) {
        for (ManejadorCliente c : clientes) {
            c.enviar(mensaje);
        }
    }

    private static void difundirTablero(boolean mostrarBombas) {
        for (ManejadorCliente c : clientes) {
            enviarTablero(c, mostrarBombas);
        }
    }

    private static void enviarTablero(ManejadorCliente destino, boolean mostrarBombas) {
        destino.enviar("TABLERO");
        for (String fila : tablero.filas(mostrarBombas)) {
            destino.enviar(fila);
        }
    }

    /** Cierra a todos los jugadores y prepara un tablero nuevo para la siguiente ronda. */
    private static void terminarPartida() {
        for (ManejadorCliente c : new ArrayList<ManejadorCliente>(clientes)) {
            c.cerrar();
        }
        clientes.clear();
        tablero = new Tablero();
        contador = 0;
    }
}
