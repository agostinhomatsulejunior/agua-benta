// util/ReceptorUDP.java
package util;

import javax.swing.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ReceptorUDP implements Runnable {
    private static final int UDP_PORT = 12346;
    

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(UDP_PORT)) {
            byte[] buffer = new byte[1024];
            System.out.println("Cliente UDP a escutar na porta " + UDP_PORT);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String mensagem = new String(packet.getData(), 0, packet.getLength());
                System.out.println("UDP recebido: " + mensagem);  // linha de depuração
                // exibir notificacao como toast
                JOptionPane.showMessageDialog(null, mensagem, "Notificação UDP", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            System.err.println("Erro no receptor UDP: " + e.getMessage());
        }
    }
}