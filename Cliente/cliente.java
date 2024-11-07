package Prueba;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.util.Scanner;

public class cliente {
    public static void main(String[] args) {
        try {
            // Crear socket UDP
            DatagramSocket socketCliente = new DatagramSocket();

            // Dirección y puerto del servidor al que se enviará el mensaje
            InetAddress direccionServidor = InetAddress.getByName("172.29.60.25");
            int puertoServidor = 3000;

            // Crear Scanner para leer el input del usuario
            Scanner scanner = new Scanner(System.in);
            String mensaje;

            // Bucle para seguir pidiendo mensajes hasta que se escriba "exit"
            while (true) {
                System.out.print("Introduce un mensaje (o 'exit' o 'continuar' para seguir): ");
                mensaje = scanner.nextLine(); // Captura el input del usuario
                
                // Si el usuario ingresa "exit", salimos del bucle
                if (mensaje.equalsIgnoreCase("exit")) {
                    break;
                }

                // Convertir el mensaje a bytes
                byte[] bufferSalida = mensaje.getBytes();
        
                // Crear paquete para enviar los datos al servidor
                DatagramPacket paqueteSalida = new DatagramPacket(bufferSalida, bufferSalida.length, direccionServidor, puertoServidor);
                socketCliente.send(paqueteSalida);
                System.out.println("Mensaje enviado: " + mensaje);

                // Crear paquete para recibir la respuesta del servidor
                byte[] bufferEntrada = new byte[1024];
                DatagramPacket paqueteEntrada = new DatagramPacket(bufferEntrada, bufferEntrada.length);

                // Recibir el mensaje del servidor
                socketCliente.receive(paqueteEntrada);
                String mensajeRecibido = new String(paqueteEntrada.getData(), 0, paqueteEntrada.getLength());
                System.out.println("Mensaje recibido del servidor: " + mensajeRecibido);
            }

            // Cerrar el scanner y el socket
            scanner.close();
            socketCliente.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
