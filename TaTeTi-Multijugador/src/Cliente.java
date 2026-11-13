import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        String ipServidor = "127.0.0.1";
        int puerto = 5000;

        try (Socket socket = new Socket(ipServidor, puerto);
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            Thread hiloEscucha = new Thread(() -> {
                try {
                    String mensajeServidor;
                    while ((mensajeServidor = entrada.readLine()) != null) {
                        System.out.println(mensajeServidor);
                    }
                } catch (IOException e) {
                    System.out.println("Conexión cerrada con el servidor.");
                }
            });
            hiloEscucha.start();

            while (scanner.hasNextLine()) {
                String jugada = scanner.nextLine();
                salida.println(jugada);
            }

        } catch (IOException e) {
            System.out.println("No se pudo conectar al servidor: " + e.getMessage());
        }
    }
}
