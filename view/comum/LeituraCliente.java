package view.comum;

import dao.ClienteDAO;
import dao.LeituraDAO;
import model.Cliente;
import model.Leitura;
import util.Estilos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class LeituraCliente extends JPanel implements ActionListener {
    private JTextField txtPesquisa;
    private JButton btnPesquisar;
    private JTable tabelaPagas;
    private JTable tabelaNaoPagas;
    private DefaultTableModel modelPagas;
    private DefaultTableModel modelNaoPagas;
    private JLabel lblClienteInfo;
    private int codClienteSelecionado = -1;

    public LeituraCliente() {
        setLayout(new BorderLayout());
        Estilos.estilizarFundo(this);

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.setOpaque(false);
        topo.add(new JLabel("Cliente (codigo ou nome):"));
        txtPesquisa = new JTextField(20);
        topo.add(txtPesquisa);
        btnPesquisar = new JButton("Pesquisar");
        Estilos.estilizarBotao(btnPesquisar);
        btnPesquisar.addActionListener(this);
        topo.add(btnPesquisar);
        lblClienteInfo = new JLabel(" Nenhum cliente selecionado");
        lblClienteInfo.setFont(Estilos.FONTE_NORMAL);
        topo.add(lblClienteInfo);
        add(topo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(2, 1, 10, 10));
        centro.setOpaque(false);

        modelNaoPagas = new DefaultTableModel(new String[]{"Data", "Leitura Anterior", "Leitura Actual", "Valor a Pagar"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabelaNaoPagas = new JTable(modelNaoPagas);
        tabelaNaoPagas.setFont(Estilos.FONTE_NORMAL);
        JScrollPane scrollNaoPagas = new JScrollPane(tabelaNaoPagas);
        scrollNaoPagas.setBorder(BorderFactory.createTitledBorder("Leituras Nao Pagas"));
        centro.add(scrollNaoPagas);

        modelPagas = new DefaultTableModel(new String[]{"Data", "Leitura Anterior", "Leitura Actual", "Valor Pago", "Metodo Pagamento"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabelaPagas = new JTable(modelPagas);
        tabelaPagas.setFont(Estilos.FONTE_NORMAL);
        JScrollPane scrollPagas = new JScrollPane(tabelaPagas);
        scrollPagas.setBorder(BorderFactory.createTitledBorder("Leituras Pagas"));
        centro.add(scrollPagas);

        add(centro, BorderLayout.CENTER);
    }

    // metodo auxiliar para selecionar cliente quando ha multiplos
    private Cliente selecionarCliente(List<Cliente> clientes) {
        if (clientes.isEmpty()) return null;
        if (clientes.size() == 1) return clientes.get(0);
        String[] opcoes = clientes.stream()
                .map(c -> c.getCodCliente() + " - " + c.getNome() + " (" + c.getEndereco() + ")")
                .toArray(String[]::new);
        int escolha = JOptionPane.showOptionDialog(
                this,
                "Varios clientes encontrados. Selecione um:",
                "Selecionar Cliente",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]);
        if (escolha >= 0 && escolha < clientes.size()) {
            return clientes.get(escolha);
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnPesquisar) {
            String texto = txtPesquisa.getText().trim();
            if (texto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Digite o codigo ou nome do cliente.");
                return;
            }
            btnPesquisar.setEnabled(false);
            lblClienteInfo.setText("A pesquisar...");
            new SwingWorker<Cliente, Void>() {
                @Override
                protected Cliente doInBackground() throws Exception {
                    ClienteDAO dao = new ClienteDAO();
                    try {
                        int cod = Integer.parseInt(texto);
                        return dao.buscarPorCodigo(cod);
                    } catch (NumberFormatException ex) {
                        List<Cliente> lista = dao.buscarPorNome(texto);
                        if (lista.isEmpty()) return null;
                        if (lista.size() == 1) return lista.get(0);
                        final Cliente[] selecionado = new Cliente[1];
                        try {
                            SwingUtilities.invokeAndWait(() -> {
                                selecionado[0] = selecionarCliente(lista);
                            });
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                        return selecionado[0];
                    }
                }
                @Override
                protected void done() {
                    try {
                        Cliente c = get();
                        if (c != null) {
                            codClienteSelecionado = c.getCodCliente();
                            lblClienteInfo.setText(" Cliente: " + c.getNome() + " - " + c.getEndereco());
                            carregarLeituras(codClienteSelecionado);
                        } else {
                            lblClienteInfo.setText(" Cliente nao encontrado.");
                            codClienteSelecionado = -1;
                            modelPagas.setRowCount(0);
                            modelNaoPagas.setRowCount(0);
                        }
                    } catch (Exception ex) {
                        lblClienteInfo.setText(" Erro na pesquisa.");
                        ex.printStackTrace();
                    } finally {
                        btnPesquisar.setEnabled(true);
                    }
                }
            }.execute();
        }
    }

    private void carregarLeituras(int codCliente) {
        modelPagas.setRowCount(0);
        modelNaoPagas.setRowCount(0);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                LeituraDAO leituraDAO = new LeituraDAO();
                List<Leitura> pagas = leituraDAO.listarPorCliente(codCliente, true);
                List<Leitura> naoPagas = leituraDAO.listarPorCliente(codCliente, false);
                for (Leitura l : pagas) {
                    Object[] row = {l.getDataLeitura(), l.getLeituraAnterior(), l.getLeituraActual(), l.getValorPagar(), "Pago"};
                    modelPagas.addRow(row);
                }
                for (Leitura l : naoPagas) {
                    Object[] row = {l.getDataLeitura(), l.getLeituraAnterior(), l.getLeituraActual(), l.getValorPagar()};
                    modelNaoPagas.addRow(row);
                }
                return null;
            }
            @Override
            protected void done() {
                if (modelPagas.getRowCount() == 0 && modelNaoPagas.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(LeituraCliente.this, "Este cliente nao tem leituras registadas.");
                }
            }
        }.execute();
    }
}