package util;

import java.io.*;
import java.net.Socket;

public class ConexaoServidor {
    private static final String SERVER_HOST = "localhost";
    private static final int TCP_PORT = 12345;
    private static Socket socket;
    private static PrintWriter out;
    private static boolean connected=false;

    public static void conectar() {
        try {
            socket = new Socket(SERVER_HOST, TCP_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            connected=true;
            System.out.println("Conectado ao servidor de notificações");
        } catch (IOException e) {
            connected=false;
            System.err.println("Servidor de notificações não disponível");
        }
    }

    public static void enviarMensagem(String msg) {
        //tenta a conexao se nao estiver ligado
        if(!connected || out == null){
            conectar();
        }
        if (out != null) {
            out.println(msg);
            System.out.println("Mensagem enviada ao servidor: "+msg);
        } else{
            System.out.println("Falha ao enviar mensagem: servidor indisponivel.");
        }
    }

    public static void fechar() {
        try {
            if (out != null) out.close();
            if (socket != null) socket.close();
            connected= false;
        } catch (IOException e) { 
            e.printStackTrace(); 
        }
    }
}