import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorJuego {
    private static final int BUFFER_SIZE = 1024;
    private static final ConcurrentHashMap<String, Boolean> clientesActivos = new ConcurrentHashMap<>();
    public static final String REGISTRO_ARCHIVO = null;
    
    public static void main(String[] args) {
        int puerto = 3000;
        
        try (DatagramSocket socket = new DatagramSocket(puerto)) {
            System.out.println("Servidor UDP en ejecución, esperando conexiones...");
            
            String[][] preguntasRespuestas = {
                {"Cuál es la capital de Ecuador","Quito"},
                {"¿Cuál es la fórmula química del agua?", "H2O"},
                {"¿Quién escribió 'Cien años de soledad'?", "Gabo"},
                {"¿Cuál es el continente más grande?", "Asia"},
                {"¿Cuántos planetas hay en el sistema solar?", "8"}
            };
            
            byte[] bufferRecepcion = new byte[BUFFER_SIZE];
            
            while (true) {
                DatagramPacket paqueteRecepcion = new DatagramPacket(bufferRecepcion, bufferRecepcion.length);
                socket.receive(paqueteRecepcion);
                
                InetAddress direccionCliente = paqueteRecepcion.getAddress();
                int puertoCliente = paqueteRecepcion.getPort();
                
                // Crear una clave única para cada cliente
                String clienteKey = direccionCliente.getHostAddress() + ":" + puertoCliente;
                
                // Verificar si el cliente ya está siendo atendido
                if (!clientesActivos.containsKey(clienteKey)) {
                    System.out.println("Nuevo cliente conectado desde: " + clienteKey);
                    clientesActivos.put(clienteKey, true);
                    new HiloClienteServ(socket, direccionCliente, puertoCliente, preguntasRespuestas, clienteKey).start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

