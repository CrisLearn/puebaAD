import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class HiloClienteServ extends Thread {
    private static final int BUFFER_SIZE = 1024;
    private DatagramSocket socket;
    private InetAddress direccionCliente;
    private int puertoCliente;
    private String[][] preguntasRespuestas;
    private String clienteKey;
    private static int contadorRespuestas = 1;

    public HiloClienteServ(DatagramSocket socket, InetAddress direccionCliente, int puertoCliente, 
                          String[][] preguntasRespuestas, String clienteKey) {
        this.socket = socket;
        this.direccionCliente = direccionCliente;
        this.puertoCliente = puertoCliente;
        this.preguntasRespuestas = preguntasRespuestas;
        this.clienteKey = clienteKey;
    }
    
    private void enviarMensaje(String mensaje) throws Exception {
        byte[] bufferEnvio = mensaje.getBytes();
        DatagramPacket paqueteEnvio = new DatagramPacket(
            bufferEnvio, 
            bufferEnvio.length, 
            direccionCliente, 
            puertoCliente
        );
        socket.send(paqueteEnvio);
    }
    
    private String recibirMensaje() throws Exception {
        byte[] bufferRecepcion = new byte[BUFFER_SIZE];
        DatagramPacket paqueteRecepcion = new DatagramPacket(bufferRecepcion, bufferRecepcion.length);
        socket.receive(paqueteRecepcion);
        
        // Verificar que el mensaje es del cliente esperado
        if (!direccionCliente.equals(paqueteRecepcion.getAddress()) || 
            puertoCliente != paqueteRecepcion.getPort()) {
            return null;
        }
        
        return new String(paqueteRecepcion.getData(), 0, paqueteRecepcion.getLength());
    }

    private synchronized static void registrarRespuesta(String respuesta, String ip) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("respuestas.txt", true))) {
            String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String entrada = String.format("Respuesta #%d | Fecha y Hora: %s | IP: %s | Respuesta: %s",
                                          contadorRespuestas++, fechaHora, ip, respuesta);
            writer.println(entrada);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            int puntaje = 0;
            int preguntaActual = 0;
            
            // Enviar mensaje de bienvenida
            enviarMensaje("¡Bienvenido al juego de preguntas! Comenzamos...");
            
            while (preguntaActual < preguntasRespuestas.length) {
                String pregunta = preguntasRespuestas[preguntaActual][0];
                String respuestaCorrecta = preguntasRespuestas[preguntaActual][1];
                
                // Enviar pregunta
                enviarMensaje(pregunta);
                
                // Recibir respuesta
                String respuestaCliente = recibirMensaje();
                if (respuestaCliente == null) continue;
                
                System.out.println("Respuesta del cliente " + clienteKey + ": " + respuestaCliente);
                
                // Registrar respuesta en el archivo
                registrarRespuesta(respuestaCliente, direccionCliente.getHostAddress());
                
                if (respuestaCliente.equalsIgnoreCase("exit")) {
                    System.out.println("Cliente " + clienteKey + " ha terminado la conexión.");
                    break;
                }
                
                // Verificar respuesta y avanzar a la siguiente pregunta
                if (respuestaCliente.equalsIgnoreCase(respuestaCorrecta)) {
                    enviarMensaje("¡Correcto!");
                    puntaje = puntaje + 4;
                } else {
                    enviarMensaje("Incorrecto. La respuesta correcta es: " + respuestaCorrecta);
                }
                
                preguntaActual++;
            }
            
            // Enviar puntaje final
            enviarMensaje("Juego terminado. Tu puntaje final es: " + puntaje + " de 20");
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
