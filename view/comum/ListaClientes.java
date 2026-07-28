package view.comum;

import dao.ClienteDAO;
import dao.LeituraDAO;
import model.Cliente;
import util.Estilos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.function.Consumer;

public class ListaClientes extends JPanel implements ActionListener {
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JButton btnAtualizar;
    private JButton btnEliminar;
    private JButton btnEditar;
    private Consumer<Cliente> onEditListener;

    public ListaClientes() {
        this(null);
    }

    public ListaClientes(Consumer<Cliente> onEditListener) {
        this.onEditListener = onEditListener;
        setLayout(new BorderLayout());
        setBackground(Estilos.COR_FUNDO);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        JLabel titulo = new JLabel("Clientes Cadastrados");
        titulo.setFont(Estilos.FONTE_TITULO);
        topo.add(titulo, BorderLayout.WEST);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.setOpaque(false);
        btnAtualizar = new JButton("Atualizar");
        btnEliminar = new JButton("Eliminar");
        btnEditar = new JButton("Editar");
        Estilos.estilizarBotao(btnAtualizar);
        Estilos.estilizarBotaoEliminar(btnEliminar);
        Estilos.estilizarBotao(btnEditar);
        btnAtualizar.addActionListener(this);
        btnEliminar.addActionListener(this);
        btnEditar.addActionListener(this);
        botoes.add(btnAtualizar);
        botoes.add(btnEditar);
        botoes.add(btnEliminar);
        topo.add(botoes, BorderLayout.EAST);
        add(topo, BorderLayout.NORTH);

        String[] colunas = {"Codigo", "Nome", "Endereco", "Data Contrato", "Leituras Nao Pagas"};
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

    public void setOnEditListener(Consumer<Cliente> listener) {
        this.onEditListener = listener;
    }

    public void carregarDados() {
        modeloTabela.setRowCount(0);
        ClienteDAO dao = new ClienteDAO();
        List<Cliente> clientes = dao.listarTodos();
        for (Cliente c : clientes) {
            int naoPagas = dao.quantidadeLeiturasNaoPagas(c.getCodCliente());
            Object[] linha = {
                c.getCodCliente(),
                c.getNome(),
                c.getEndereco(),
                c.getDataContrato(),
                naoPagas
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
                JOptionPane.showMessageDialog(this, 
                    "Selecione um cliente para eliminar.", 
                    "Aviso", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            int codCli = (int) modeloTabela.getValueAt(linha, 0);
            String nome = (String) modeloTabela.getValueAt(linha, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Eliminar este cliente? Todas as leituras e pagamentos serao removidos.",
                    "Confirmar eliminacao",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            btnEliminar.setEnabled(false);
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return new ClienteDAO().eliminar(codCli);
                }
                @Override
                protected void done() {
                    try {
                        if (get()) {
                            carregarDados();
                            JOptionPane.showMessageDialog(null, 
                                "Cliente eliminado com sucesso.", 
                                "Sucesso", 
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, 
                                "Erro ao eliminar cliente.", 
                                "Erro", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, 
                            "Erro: " + ex.getMessage(), 
                            "Erro", 
                            JOptionPane.ERROR_MESSAGE);
                    } finally {
                        btnEliminar.setEnabled(true);
                    }
                }
            }.execute();
        }
        else if (e.getSource() == btnEditar) {
            int linha = tabela.getSelectedRow();
            if (linha == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Selecione um cliente para editar.", 
                    "Aviso", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            int codCliente = (int) modeloTabela.getValueAt(linha, 0);
            ClienteDAO dao = new ClienteDAO();
            Cliente c = dao.buscarPorCodigo(codCliente);
            if (c == null) {
                JOptionPane.showMessageDialog(this, 
                    "Cliente nao encontrado.", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (onEditListener != null) {
                onEditListener.accept(c);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Funcionalidade de edicao nao disponivel.", 
                    "Aviso", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}