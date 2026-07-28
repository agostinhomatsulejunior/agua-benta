package view.admin;

import dao.ClienteDAO;
import dao.NotificacaoDAO;
import dao.UtilizadorDAO;
import model.Notificacao;
import model.Utilizador;
import util.Estilos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Timestamp;
import java.util.List;

public class ListaGestores extends JPanel implements ActionListener {
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JButton btnAtualizar;
    private JButton btnEliminar;
    private Utilizador adminLogado;

    public ListaGestores(Utilizador adminLogado) {
        this.adminLogado = adminLogado;
        setLayout(new BorderLayout());
        setBackground(Estilos.COR_FUNDO);

        // Interface (igual ao que já tem)
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        JLabel titulo = new JLabel("Lista de Gestores");
        titulo.setFont(Estilos.FONTE_TITULO);
        topo.add(titulo, BorderLayout.WEST);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.setOpaque(false);
        btnAtualizar = new JButton("Atualizar");
        btnEliminar = new JButton("Eliminar");
        Estilos.estilizarBotao(btnAtualizar);
        Estilos.estilizarBotaoEliminar(btnEliminar);
        btnAtualizar.addActionListener(this);
        btnEliminar.addActionListener(this);
        botoes.add(btnAtualizar);
        botoes.add(btnEliminar);
        topo.add(botoes, BorderLayout.EAST);
        add(topo, BorderLayout.NORTH);

        String[] colunas = {"Código", "Nome", "Email", "Data Contrato"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(30);
        tabela.setFont(Estilos.FONTE_NORMAL);
        tabela.getTableHeader().setFont(Estilos.FONTE_SUBTITULO);
        tabela.getTableHeader().setBackground(Estilos.COR_SECUNDARIA);
        tabela.getTableHeader().setForeground(Estilos.COR_TEXTO_CLARO);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(Estilos.COR_PRIMARIA, 1));
        add(scroll, BorderLayout.CENTER);

        carregarDados();
    }

    private void carregarDados() {
        modeloTabela.setRowCount(0);
        UtilizadorDAO dao = new UtilizadorDAO();
        List<Utilizador> gestores = dao.listarPorPerfil("Gestor");
        for (Utilizador u : gestores) {
            Object[] linha = {
                u.getCodUtilizador(),
                u.getNome(),
                u.getEmail(),
                u.getDataContrato() != null ? u.getDataContrato() : ""
            };
            modeloTabela.addRow(linha);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAtualizar) {
            btnAtualizar.setEnabled(false);
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    carregarDados();
                    return null;
                }
                @Override
                protected void done() {
                    btnAtualizar.setEnabled(true);
                }
            }.execute();
        }
        else if (e.getSource() == btnEliminar) {
            int linha = tabela.getSelectedRow();
            if (linha == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um gestor.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int codGestor = (int) modeloTabela.getValueAt(linha, 0);
            String nomeGestor = (String) modeloTabela.getValueAt(linha, 1);

            ClienteDAO clienteDAO = new ClienteDAO();
            int qtdClientes = clienteDAO.quantidadeClientesPorGestor(codGestor);

            int novoGestorCod = -1;
            String nomeNovoGestor = null;

            if (qtdClientes > 0) {
                UtilizadorDAO dao = new UtilizadorDAO();
                List<Utilizador> outrosGestores = dao.listarGestoresExceto(codGestor);
                if (outrosGestores.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Não há outro gestor disponível para receber os clientes.\n" +
                        "Não é possível eliminar este gestor.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String[] opcoes = outrosGestores.stream()
                        .map(u -> u.getCodUtilizador() + " - " + u.getNome())
                        .toArray(String[]::new);
                int escolha = JOptionPane.showOptionDialog(
                        this,
                        "O gestor " + nomeGestor + " tem " + qtdClientes + " cliente(s).\n" +
                        "Selecione o novo gestor para receber estes clientes:",
                        "Transferir Clientes",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opcoes,
                        opcoes[0]);
                if (escolha < 0) {
                    return;
                }
                novoGestorCod = outrosGestores.get(escolha).getCodUtilizador();
                nomeNovoGestor = outrosGestores.get(escolha).getNome();
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja eliminar o gestor " + nomeGestor + "?",
                    "Confirmar eliminação",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            final int codGestorFinal = codGestor;
            final int novoGestorFinal = novoGestorCod;
            final String nomeGestorFinal = nomeGestor;
            final String nomeNovoGestorFinal = nomeNovoGestor;
            final int qtdClientesFinal = qtdClientes;

            btnEliminar.setEnabled(false);
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return new UtilizadorDAO().eliminar(codGestorFinal, novoGestorFinal);
                }
                @Override
                protected void done() {
                    try {
                        if (get()) {
                            NotificacaoDAO notifDAO = new NotificacaoDAO();
                            Timestamp agora = new Timestamp(System.currentTimeMillis());

                            // notificacao eliminacao
                            Notificacao n1 = new Notificacao();
                            n1.setTipo("Gestor");
                            n1.setMensagem("Gestor eliminado: " + nomeGestorFinal);
                            n1.setNomeFuncionario(adminLogado.getNome());
                            n1.setDataHora(agora);
                            notifDAO.inserir(n1);

                            // notificacao transferencia (se houver)
                            if (novoGestorFinal > 0 && qtdClientesFinal > 0) {
                                Notificacao n2 = new Notificacao();
                                n2.setTipo("Cliente");
                                n2.setMensagem(qtdClientesFinal + " cliente(s) transferido(s) do gestor " +
                                                nomeGestorFinal + " para " + nomeNovoGestorFinal);
                                n2.setNomeFuncionario(adminLogado.getNome());
                                n2.setDataHora(agora);
                                notifDAO.inserir(n2);
                            }

                            JOptionPane.showMessageDialog(ListaGestores.this,
                                "Gestor eliminado com sucesso.",
                                "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE);
                            carregarDados();
                        } else {
                            JOptionPane.showMessageDialog(ListaGestores.this,
                                "Erro ao eliminar gestor.",
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ListaGestores.this,
                            "Erro: " + ex.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    } finally {
                        btnEliminar.setEnabled(true);
                    }
                }
            }.execute();
        }
    }
}