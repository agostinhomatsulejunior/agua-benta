package view.comum;

import dao.NotificacaoDAO;
import model.Notificacao;
import model.Utilizador;
import util.Estilos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ListaNotificacoes extends JPanel implements ActionListener {
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JButton btnAtualizar;
    private JButton btnMostrarTodas;
    private JComboBox<String> cbFiltroTipo;
    private JTextField txtFiltroFuncionario;
    private JButton btnFiltrarFuncionario;
    private boolean mostrarTodas = false;

    public ListaNotificacoes() {
        this(null);
    }

    public ListaNotificacoes(Utilizador utilizadorLogado) {
        setLayout(null);
        setBackground(Estilos.COR_FUNDO);

        JLabel titulo = new JLabel("Historico de Notificacoes");
        titulo.setFont(Estilos.FONTE_TITULO);
        titulo.setBounds(20, 20, 300, 30);
        add(titulo);

        JLabel lblTipo = new JLabel("Tipo:");
        lblTipo.setFont(Estilos.FONTE_NORMAL);
        lblTipo.setBounds(20, 60, 60, 25);
        add(lblTipo);

        cbFiltroTipo = new JComboBox<>(new String[]{"Todos", "Pagamento", "Leitura", "Cliente", "Tarifa", "Gestor"});
        cbFiltroTipo.setFont(Estilos.FONTE_NORMAL);
        cbFiltroTipo.setBounds(80, 60, 150, 25);
        cbFiltroTipo.addActionListener(this);
        add(cbFiltroTipo);

        JLabel lblFunc = new JLabel("Funcionario:");
        lblFunc.setFont(Estilos.FONTE_NORMAL);
        lblFunc.setBounds(250, 60, 100, 25);
        add(lblFunc);

        txtFiltroFuncionario = new JTextField();
        txtFiltroFuncionario.setFont(Estilos.FONTE_NORMAL);
        txtFiltroFuncionario.setBounds(350, 60, 180, 25);
        add(txtFiltroFuncionario);

        btnFiltrarFuncionario = new JButton("Filtrar");
        Estilos.estilizarBotao(btnFiltrarFuncionario);
        btnFiltrarFuncionario.setBounds(540, 58, 100, 28);
        btnFiltrarFuncionario.addActionListener(this);
        add(btnFiltrarFuncionario);

        btnAtualizar = new JButton("Atualizar");
        btnMostrarTodas = new JButton("Mostrar todas");
        Estilos.estilizarBotao(btnAtualizar);
        Estilos.estilizarBotao(btnMostrarTodas);
        btnAtualizar.setBounds(20, 100, 150, 30);
        btnMostrarTodas.setBounds(180, 100, 150, 30);
        btnAtualizar.addActionListener(this);
        btnMostrarTodas.addActionListener(this);
        add(btnAtualizar);
        add(btnMostrarTodas);

        String[] colunas = {"Data/Hora", "Tipo", "Mensagem", "Funcionario"};
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
        tabela.setToolTipText("Duplo clique para ver detalhes");

        // cria scroll pane na tabela
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBounds(20, 150, 750, 350);
        scroll.setBorder(BorderFactory.createLineBorder(Estilos.COR_PRIMARIA, 1));
        add(scroll);

        // para usar o mouse listener
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("mouseClicked detetado"); // debug
                if (e.getClickCount() == 2) {
                    System.out.println("Duplo clique detetado!"); // debug
                    int row = tabela.getSelectedRow();
                    if (row >= 0) {
                        System.out.println("Linha selecionada: " + row);
                        mostrarDetalhesNotificacao(row);
                    } else {
                        System.out.println("Nenhuma linha selecionada");
                    }
                }
            }
        });

        carregarDados();
    }

    private void mostrarDetalhesNotificacao(int row) {
        try {
            // Obter os valores das colunas
            Object dataObj = modeloTabela.getValueAt(row, 0);
            String tipo = (String) modeloTabela.getValueAt(row, 1);
            String mensagem = (String) modeloTabela.getValueAt(row, 2);
            String funcionario = (String) modeloTabela.getValueAt(row, 3);
    
            // Converter data para String (Timestamp ou Date)
            String dataStr;
            if (dataObj instanceof java.sql.Timestamp) {
                java.sql.Timestamp ts = (java.sql.Timestamp) dataObj;
                dataStr = ts.toString(); 
            } else if (dataObj instanceof java.util.Date) {
                java.util.Date d = (java.util.Date) dataObj;
                dataStr = d.toString();
            } else {
                dataStr = dataObj != null ? dataObj.toString() : "";
            }
    
            String detalhes = "Data/Hora: " + dataStr + "\n" +
                              "Tipo: " + tipo + "\n" +
                              "Funcionario: " + funcionario + "\n\n" +
                              "Mensagem:\n" + mensagem;
    
            JOptionPane.showMessageDialog(
                this,
                detalhes,
                "Detalhes da Notificacao",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            System.err.println("Erro ao mostrar detalhes: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao mostrar detalhes da notificação.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void carregarDados() {
        modeloTabela.setRowCount(0);
        NotificacaoDAO dao = new NotificacaoDAO();
        String tipo = (String) cbFiltroTipo.getSelectedItem();
        String funcionario = txtFiltroFuncionario.getText().trim();
        int limite = mostrarTodas ? 0 : 10;

        List<Notificacao> lista = dao.listarComFiltro(
                "Todos".equals(tipo) ? null : tipo,
                funcionario.isEmpty() ? null : funcionario,
                limite
        );

        for (Notificacao n : lista) {
            Object[] row = {
                n.getDataHora(),
                n.getTipo(),
                n.getMensagem(),
                n.getNomeFuncionario()
            };
            modeloTabela.addRow(row);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAtualizar || e.getSource() == cbFiltroTipo || e.getSource() == btnFiltrarFuncionario) {
            carregarDados();
        } else if (e.getSource() == btnMostrarTodas) {
            mostrarTodas = !mostrarTodas;
            btnMostrarTodas.setText(mostrarTodas ? "Mostrar apenas ultimas 10" : "Mostrar todas");
            carregarDados();
        }
    }
}