package view.comum;

import dao.ClienteDAO;
import dao.LeituraDAO;
import dao.ConexaoBD;
import model.Cliente;
import model.Utilizador;
import util.Estilos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.sql.*;

public class ClientesDebito extends JPanel implements ActionListener {
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JTextField txtFiltroGestor;
    private JButton btnFiltrar;
    private JButton btnAtualizar;
    private Utilizador utilizadorLogado;

    public ClientesDebito(Utilizador utilizadorLogado) {
        this.utilizadorLogado = utilizadorLogado;
        setLayout(null);
        setBackground(Estilos.COR_FUNDO);

        JLabel titulo = new JLabel("Clientes com Leituras em Atraso");
        titulo.setFont(Estilos.FONTE_TITULO);
        titulo.setBounds(20, 20, 400, 30);
        add(titulo);

        JLabel lblGestor = new JLabel("Filtrar por gestor (nome):");
        lblGestor.setFont(Estilos.FONTE_NORMAL);
        lblGestor.setBounds(20, 60, 180, 25);
        add(lblGestor);

        txtFiltroGestor = new JTextField();
        txtFiltroGestor.setFont(Estilos.FONTE_NORMAL);
        txtFiltroGestor.setBounds(200, 60, 200, 25);
        add(txtFiltroGestor);

        btnFiltrar = new JButton("Filtrar");
        Estilos.estilizarBotao(btnFiltrar);
        btnFiltrar.setBounds(410, 58, 100, 28);
        btnFiltrar.addActionListener(this);
        add(btnFiltrar);

        btnAtualizar = new JButton("Atualizar");
        Estilos.estilizarBotao(btnAtualizar);
        btnAtualizar.setBounds(520, 58, 120, 28);
        btnAtualizar.addActionListener(this);
        add(btnAtualizar);

        String[] colunas = {"Codigo", "Nome", "Endereco", "Gestor", "Leituras em Debito"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(30);
        tabela.setFont(Estilos.FONTE_NORMAL);
        tabela.getTableHeader().setFont(Estilos.FONTE_SUBTITULO);
        tabela.getTableHeader().setBackground(Estilos.COR_SECUNDARIA);
        tabela.getTableHeader().setForeground(Estilos.COR_TEXTO_CLARO);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBounds(20, 110, 700, 350);
        scroll.setBorder(BorderFactory.createLineBorder(Estilos.COR_PRIMARIA, 1));
        add(scroll);

        carregarDados();
    }

    private void carregarDados() {
        modeloTabela.setRowCount(0);
        ClienteDAO daoCliente = new ClienteDAO();
        LeituraDAO daoLeitura = new LeituraDAO();

        // Gestor ve todos os clientes em debito (sem filtro por codFunc)
        List<Cliente> clientes = daoCliente.listarClientesEmAtraso(null);

        String nomeGestorFiltro = txtFiltroGestor.getText().trim();

        for (Cliente c : clientes) {
            String nomeGestor = buscarNomeGestor(c.getCodFunc());
            // se houver filtro por nome de gestor, aplicar
            if (!nomeGestorFiltro.isEmpty()) {
                if (!nomeGestor.toLowerCase().contains(nomeGestorFiltro.toLowerCase())) {
                    continue;
                }
            }
            int emDebito = daoLeitura.listarPorCliente(c.getCodCliente(), false).size();
            if (emDebito > 0) {
                Object[] row = {c.getCodCliente(), c.getNome(), c.getEndereco(), nomeGestor, emDebito};
                modeloTabela.addRow(row);
            }
        }
    }

    private String buscarNomeGestor(int codFunc) {
        String sql = "SELECT nome FROM utilizador WHERE codUtilizador = ?";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codFunc);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("nome");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Desconhecido";
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnFiltrar || e.getSource() == btnAtualizar) {
            carregarDados();
        }
    }
}