import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ManejadorJugador implements Runnable {
    private Socket socket;
    private char simbolo;
    private int idJugador;
    private PrintWriter salida;
    private BufferedReader entrada;

    public ManejadorJugador(Socket socket, char simbolo, int idJugador) {
        this.socket = socket;
        this.simbolo = simbolo;
        this.idJugador = idJugador;
        try {
            salida = new PrintWriter(socket.getOutputStream(), true);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Error creando flujos para jugador " + simbolo);
        }
    }

    public void enviarMensaje(String mensaje) {
        salida.println(mensaje);
    }

    @Override
    public void run() {
        enviarMensaje("Bienvenido. Eres el jugador '" + simbolo + "'. Esperando al otro jugador...");
        try {
            String linea;
            while ((linea = entrada.readLine()) != null) {
                String[] partes = linea.trim().split(" ");
                if (partes.length == 2) {
                    try {
                        int fila = Integer.parseInt(partes[0]);
                        int col = Integer.parseInt(partes[1]);
                        boolean valida = Servidor.procesarJugada(idJugador, fila, col);
                        if (!valida) {
                            enviarMensaje("Movimiento inválido o no es tu turno. Intenta de nuevo (ej: 1 1):");
                        }
                    } catch (NumberFormatException e) {
                        enviarMensaje("Formato incorrecto. Usa dos números separados por espacio (ej: 0 1):");
                    }
                } else {
                    enviarMensaje("Debes ingresar fila y columna (ej: 2 0):");
                }
            }
        } catch (IOException e) {
            System.out.println("Jugador " + simbolo + " se desconectó.");
        }
    }
}
