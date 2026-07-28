package view;

import view.Principal;
import view.admin.PainelAdmin;
import view.gestor.PainelGestor;
import view.entrada.Login;
import dao.UtilizadorDAO;
import util.ReceptorUDP;


import javax.swing.*;
import java.awt.*;

public class Principal extends JFrame {
    public CardLayout card = new CardLayout();
    private Login login;

    

    public Principal() {
        setTitle("💧 Agua Benta Limitada");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        //setSize(800, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(card);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //inicia o servidor tcp
        new Thread(() -> server.ServidorNotificacoes.main(new String[]{})).start();

        //conecta ao servido
        util.ConexaoServidor.conectar();

        //inicia o receptor UDP
        new Thread(new ReceptorUDP()).start();

        // adiciona os painéis
        login = new Login();
        add(login.pnlLogin(this), "login");    

        // decide qual mostrar
        UtilizadorDAO dao = new UtilizadorDAO();
        if (!dao.existeAdmin()) {
            card.show(getContentPane(), "login");
        } else {
            card.show(getContentPane(), "login");
        }

        setVisible(true);
    }

    public void mostrarLogin() {
        card.show(getContentPane(), "login");
        login.limparCampos();
    }
    
}