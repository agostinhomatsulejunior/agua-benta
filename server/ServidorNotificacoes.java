package server;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorNotificacoes {
    private static final int TCP_PORT = 12345;
    private static final int UDP_PORT = 12346;
    // usar localhost para testes; para rede real, usar "255.255.255.255" ou o broadcast da sub-rede
    private static final String BROADCAST_ADDRESS = "127.0.0.1";  

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newCachedThreadPool();

        // TCP server
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(TCP_PORT)) {
                System.out.println("Servidor TCP a escutar na porta " + TCP_PORT);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    threadPool.submit(() -> handleClient(clientSocket));
                }
            } catch (IOException e) {
                System.err.println("Erro no servidor TCP: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();

        System.out.println("Servidor UDP pronto para enviar broadcasts na porta " + UDP_PORT);
    }

    private static void handleClient(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            String msg = in.readLine();
            if (msg != null && !msg.trim().isEmpty()) {
                System.out.println("[Servidor] Recebido: " + msg);
                // envia broadcast UDP
                enviarBroadcastUDP(msg);
                out.println("OK");
            } else {
                out.println("Mensagem vazia");
            }
        } catch (IOException e) {
            System.err.println("Erro ao processar cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void enviarBroadcastUDP(String mensagem) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            byte[] buffer = mensagem.getBytes();
            InetAddress address = InetAddress.getByName(BROADCAST_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, UDP_PORT);
            socket.send(packet);
            System.out.println("[Servidor] Broadcast UDP enviado para " + BROADCAST_ADDRESS + ":" + UDP_PORT + " - " + mensagem);
        } catch (IOException e) {
            System.err.println("[Servidor] Erro ao enviar broadcast UDP: " + e.getMessage());
            e.printStackTrace();
        }
    }
}