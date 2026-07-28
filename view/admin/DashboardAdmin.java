package view.admin;

import dao.*;
import util.Estilos;

import javax.swing.*;
import java.awt.*;

public class DashboardAdmin extends JPanel {
    public DashboardAdmin() {
        setLayout(null);
        setBackground(Estilos.COR_FUNDO);

        // Titulo
        JLabel titulo = new JLabel("Dashboard do Administrador");
        titulo.setFont(Estilos.FONTE_TITULO);
        titulo.setForeground(Estilos.COR_TEXTO);
        titulo.setBounds(30, 20, 400, 30);
        add(titulo);

        //Estatisticas
        int totalClientes = new ClienteDAO().listarTodos().size();
        int totalGestores = new UtilizadorDAO().listarPorPerfil("Gestor").size();
        int totalTarifas = new TarifaDAO().listarTodas().size();
        int leiturasMes = new LeituraDAO().contarLeiturasMesAtual();
        int pagamentosMes = new PagamentoDAO().contarPagamentosMesAtual();
        double valorArrecadado = new PagamentoDAO().somaPagamentosMesAtual();

        // Coordenadas: cada card terá largura 200, altura 80, espaçamento 30
        int x = 30, y = 80, largura = 200, altura = 80, espacoX = 230, espacoY = 100;
        int linha = 0, coluna = 0;

        // Card 1: Clientes
        addCard(x + coluna * espacoX, y + linha * espacoY, largura, altura, " Clientes", String.valueOf(totalClientes));
        coluna++;
        // Card 2: Gestores
        addCard(x + coluna * espacoX, y + linha * espacoY, largura, altura, " Gestores", String.valueOf(totalGestores));
        coluna++;
        // Card 3: Tarifas
        addCard(x + coluna * espacoX, y + linha * espacoY, largura, altura, " Tarifas", String.valueOf(totalTarifas));
        // Nova linha
        linha++;
        coluna = 0;
        // Card 4: Leituras no mês
        addCard(x + coluna * espacoX, y + linha * espacoY, largura, altura, " Leituras (mês)", String.valueOf(leiturasMes));
        coluna++;
        // Card 5: Pagamentos no mês
        addCard(x + coluna * espacoX, y + linha * espacoY, largura, altura, " Pagamentos (mês)", String.valueOf(pagamentosMes));
        coluna++;
        // Card 6: Valor arrecadado
        addCard(x + coluna * espacoX, y + linha * espacoY, largura, altura, " Arrecadado (mês)", String.format("%.2f MT", valorArrecadado));
    }

    private void addCard(int x, int y, int largura, int altura, String titulo, String valor) {
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBounds(x, y, largura, altura);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Estilos.COR_PRIMARIA, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitulo.setForeground(Estilos.COR_TEXTO);
        lblTitulo.setBounds(10, 10, largura - 20, 20);
        card.add(lblTitulo);
        
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValor.setForeground(Estilos.COR_DESTAQUE);
        lblValor.setBounds(10, 35, largura - 20, 30);
        card.add(lblValor);
        
        add(card);
    }
}