package Prueba;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class hiloCliente extends Thread {
    private DatagramSocket socket;
    private DatagramPacket paqueteEntrada;

    public hiloCliente(DatagramSocket socket, DatagramPacket paqueteEntrada) {
        this.socket = socket;
        this.paqueteEntrada = paqueteEntrada;
    }

    @Override
    public void run() {
        try {
            // Obtener dirección del cliente
            InetAddress ipCliente = paqueteEntrada.getAddress();
            int puertoCliente = paqueteEntrada.getPort();

            // Obtener el mensaje recibido del paquete
            String mensajeRecibido = new String(paqueteEntrada.getData(), 0, paqueteEntrada.getLength());
            System.out.println("Mensaje recibido: " + mensajeRecibido);

            // Solicitar al usuario que ingrese una respuesta
            Scanner entrada = new Scanner(System.in);
            System.out.print("Ingresa tu mensaje: ");
            String respuesta = entrada.nextLine();

            byte[] bufferSalida = respuesta.getBytes();

            // Crear un paquete para enviar la respuesta al cliente
            DatagramPacket paqueteSalida = new DatagramPacket(bufferSalida, bufferSalida.length, ipCliente, puertoCliente);

            // Enviar el paquete de respuesta al cliente
            socket.send(paqueteSalida);
            System.out.println("Mensaje enviado: " + respuesta);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Este código es solo para probar el hilo, sería ejecutado en el servidor que maneja los paquetes entrantes.
        try {
            DatagramSocket socket = new DatagramSocket(3000);  // Puerto donde el servidor escucha
            byte[] bufferEntrada = new byte[1024];

            // Preparar el paquete para recibir datos
            DatagramPacket paqueteEntrada = new DatagramPacket(bufferEntrada, bufferEntrada.length);

            // Esperar recibir datos en el puerto 3000
            socket.receive(paqueteEntrada);
            System.out.println("Paquete recibido del cliente");

            // Crear e iniciar el hilo para manejar la comunicación con el cliente
            hiloCliente hilo = new hiloCliente(socket, paqueteEntrada);
            hilo.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
