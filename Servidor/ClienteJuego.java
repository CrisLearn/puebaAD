import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteJuego {
    public static void main(String[] args) {
        String direccionServidor = "localhost"; // Cambia si es necesario
        int puerto = 12345;

        try (Socket socket_cliente = new Socket(direccionServidor, puerto);
             PrintWriter buffer_salida = new PrintWriter(socket_cliente.getOutputStream(), true);
             BufferedReader buffer_entrada = new BufferedReader(new InputStreamReader(socket_cliente.getInputStream()))) {

            BufferedReader entradaUsuario = new BufferedReader(new InputStreamReader(System.in));
            String respuesta;

            while (true) {
                // Esperar a recibir la pregunta del servidor
                String pregunta = buffer_entrada.readLine();
                if (pregunta == null) break; // Verificar si la conexión se ha cerrado

                System.out.println(pregunta);
                respuesta = entradaUsuario.readLine();

                // Enviar respuesta al servidor
                buffer_salida.println(respuesta);

                // Leer respuesta del servidor
                String respuestaServidor = buffer_entrada.readLine();
                System.out.println(respuestaServidor);

                // Salir si el usuario lo desea
                if (respuesta.equalsIgnoreCase("exit")) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();}}}