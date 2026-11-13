import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor {
    private static final int PUERTO = 5000;
    private static char[][] tablero = {
        {'-', '-', '-'},
        {'-', '-', '-'},
        {'-', '-', '-'}
    };
    private static List<ManejadorJugador> jugadores = new ArrayList<>();
    private static int turnoActual = 0;
    private static boolean juegoTerminado = false;

    public static void main(String[] args) {
        System.out.println("Iniciando servidor de Ta-Te-Ti en el puerto " + PUERTO + "...");
        try {
            ServerSocket serverSocket = new ServerSocket(PUERTO);
            for (int i = 0; i < 2; i++) {
                Socket socket = serverSocket.accept();
                char simbolo = (i == 0) ? 'X' : 'O';
                System.out.println("Jugador " + simbolo + " conectado desde " + socket.getInetAddress());
                ManejadorJugador jugador = new ManejadorJugador(socket, simbolo, i);
                jugadores.add(jugador);
                new Thread(jugador).start();
            }
            System.out.println("Ambos jugadores conectados. Comienza la partida.");
            Thread.sleep(500);
            enviarATodos("¡Partida iniciada! Tablero actual:\n" + obtenerTableroTexto());
            notificarTurno();
        } catch (Exception e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }

    public static synchronized boolean procesarJugada(int idJugador, int fila, int col) {
        if (juegoTerminado || idJugador != turnoActual) return false;
        if (fila < 0 || fila > 2 || col < 0 || col > 2 || tablero[fila][col] != '-') return false;

        char simbolo = (idJugador == 0) ? 'X' : 'O';
        tablero[fila][col] = simbolo;

        enviarATodos("\nJugador " + simbolo + " jugó en (" + fila + "," + col + "):\n" + obtenerTableroTexto());

        if (hayGanador(simbolo)) {
            enviarATodos("¡FIN DEL JUEGO! El Jugador " + simbolo + " ha ganado.");
            juegoTerminado = true;
        } else if (tableroLleno()) {
            enviarATodos("¡FIN DEL JUEGO! Empate.");
            juegoTerminado = true;
        } else {
            turnoActual = (turnoActual == 0) ? 1 : 0;
            notificarTurno();
        }
        return true;
    }

    private static void notificarTurno() {
        jugadores.get(turnoActual).enviarMensaje("TU_TURNO: Ingresa fila y columna separadas por espacio (ej: 0 2):");
        int otro = (turnoActual == 0) ? 1 : 0;
        jugadores.get(otro).enviarMensaje("Esperando jugada del oponente...");
    }

    private static void enviarATodos(String mensaje) {
        for (ManejadorJugador j : jugadores) {
            if (j != null) j.enviarMensaje(mensaje);
        }
    }

    private static String obtenerTableroTexto() {
        StringBuilder sb = new StringBuilder("  0 1 2\n");
        for (int i = 0; i < 3; i++) {
            sb.append(i).append(" ");
            for (int j = 0; j < 3; j++) {
                sb.append(tablero[i][j]).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private static boolean hayGanador(char s) {
        for (int i = 0; i < 3; i++) {
            if (tablero[i][0] == s && tablero[i][1] == s && tablero[i][2] == s) return true;
            if (tablero[0][i] == s && tablero[1][i] == s && tablero[2][i] == s) return true;
        }
        return (tablero[0][0] == s && tablero[1][1] == s && tablero[2][2] == s) ||
               (tablero[0][2] == s && tablero[1][1] == s && tablero[2][0] == s);
    }

    private static boolean tableroLleno() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tablero[i][j] == '-') return false;
            }
        }
        return true;
    }
}
