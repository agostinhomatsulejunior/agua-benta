package view.admin; // ou onde estiver

import dao.TarifaDAO;
import model.Tarifa;
import util.Estilos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ListaTarifas extends JPanel implements ActionListener {
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JButton btnAtualizar;
    private JButton btnEliminar;

    public ListaTarifas() {
        setLayout(new BorderLayout());
        setBackground(Estilos.COR_FUNDO);

        // Painel superior com título e botões
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        JLabel titulo = new JLabel("Histórico de Tarifas");
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

        // Tabela
        String[] colunas = {"Código", "Valor por m³ (MT)", "Taxa mínima (MT)", "Data de Actualização"};
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
        TarifaDAO dao = new TarifaDAO();
        List<Tarifa> tarifas = dao.listarTodas();
        for (Tarifa t : tarifas) {
            Object[] linha = {
                t.getCodTar(),
                t.getValorPorMetroCubico(),
                t.getTaxaMinima(),
                t.getDataActualizacao()
            };
            modeloTabela.addRow(linha);
        }
    }

    private void eliminarTarifa() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarifa para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int codTar = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
        String data = (String) modeloTabela.getValueAt(linhaSelecionada, 3);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja eliminar a tarifa de " + data + "?",
                "Confirmar eliminação", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        TarifaDAO dao = new TarifaDAO();
        boolean sucesso = dao.eliminar(codTar);
        if (sucesso) {
            JOptionPane.showMessageDialog(this, "Tarifa eliminada com sucesso.");
            carregarDados();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao eliminar tarifa (pode estar a ser usada em leituras).", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAtualizar) {
            btnAtualizar.setEnabled(false);
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    carregarDados(); // método que preenche a tabela
                    return null;
                }
                @Override
                protected void done() {
                    btnAtualizar.setEnabled(true);
                }
            }.execute();
        }
        if (e.getSource() == btnEliminar) {
            int linha = tabela.getSelectedRow();
            if (linha == -1) {
                JOptionPane.showMessageDialog(this, "Selecione uma tarifa para eliminar.");
                return;
            }
            int codTar = (int) modeloTabela.getValueAt(linha, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Eliminar esta tarifa?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            btnEliminar.setEnabled(false);
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return new TarifaDAO().eliminar(codTar);
                }
                @Override
                protected void done() {
                    try {
                        if (get()) {
                            carregarDados();
                            JOptionPane.showMessageDialog(null, "Tarifa eliminada com sucesso.");
                        } else {
                            JOptionPane.showMessageDialog(null, "Erro ao eliminar tarifa (pode ter leituras associadas).");
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Erro: " + ex.getMessage());
                    } finally {
                        btnEliminar.setEnabled(true);
                    }
                }
            }.execute();
        }
    }
}