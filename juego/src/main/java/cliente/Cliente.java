package main.java.cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Cliente TCP del buscaminas.
 * Argumentos opcionales: host y puerto (por defecto localhost:5000).
 */
public class Cliente {

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int puerto = args.length > 1 ? Integer.parseInt(args[1]) : 5000;

        try (Socket socket = new Socket(host, puerto)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

            new pendienteDeLosTableros(in).start();

            System.out.println("Conectado a " + host + ":" + puerto);
            System.out.println("Ingresa las coordenadas (columna fila), ejemplo: 1 2. Escribe 'salir' para terminar.");

            Scanner teclado = new Scanner(System.in);
            while (teclado.hasNextLine()) {
                String linea = teclado.nextLine().trim();
                if (linea.equalsIgnoreCase("salir")) {
                    break;
                }
                if (!linea.isEmpty()) {
                    out.println(linea);
                }
            }
            System.out.println("Hasta luego.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("No se pudo conectar con el servidor: " + e.getMessage());
        }
    }
}
