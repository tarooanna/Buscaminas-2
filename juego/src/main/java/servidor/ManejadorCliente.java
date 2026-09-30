package main.java.servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/** Un hilo por cada jugador conectado: lee sus coordenadas y se las pasa al servidor. */
class ManejadorCliente extends Thread {

    private final Socket socket;
    private PrintWriter out;
    private int numero;

    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }

    public int getNumero() {
        return numero;
    }

    public void enviar(String mensaje) {
        PrintWriter salida = out;
        if (salida != null) {
            salida.println(mensaje);
        }
    }

    public void cerrar() {
        try {
            socket.close();
        } catch (IOException e) {
            // ya estaba cerrado
        }
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new PrintWriter(new java.io.OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

            numero = Servidor.registrar(this);
            System.out.println("Jugador " + numero + " conectado: " + socket.getRemoteSocketAddress());
            enviar("Bienvenido, eres el jugador " + numero + ". Ingresa columna y fila separadas por un espacio (ejemplo: 1 2).");
            Servidor.enviarTableroA(this);

            String linea;
            while ((linea = in.readLine()) != null) {
                procesar(linea.trim());
            }
        } catch (IOException e) {
            // el cliente se desconectó o se cerró la partida
        } finally {
            Servidor.retirar(this);
            cerrar();
            System.out.println("Jugador " + numero + " desconectado");
        }
    }

    private void procesar(String linea) {
        String[] partes = linea.split("\\s+");
        if (partes.length != 2) {
            enviar("Formato inválido. Usa: columna fila (ejemplo: 1 2)");
            return;
        }
        try {
            int columna = Integer.parseInt(partes[0]);
            int fila = Integer.parseInt(partes[1]);
            Servidor.jugar(this, columna, fila);
        } catch (NumberFormatException e) {
            enviar("Solo se aceptan números enteros entre 0 y " + (Tablero.TAMANO - 1));
        }
    }
}
