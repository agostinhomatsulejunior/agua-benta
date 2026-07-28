package view.comum;

import dao.ClienteDAO;
import dao.LeituraDAO;
import dao.NotificacaoDAO;
import dao.PagamentoDAO;
import model.Cliente;
import model.Leitura;
import model.Notificacao;
import model.Pagamento;
import model.Utilizador;
import util.ConexaoServidor;
import util.Estilos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Timestamp;
import java.util.List;

public class RegistoPagamento extends JPanel implements ActionListener {
    private JTextField txtPesquisaCliente;
    private JButton btnPesquisar;
    private JLabel lblClienteInfo;
    private int codClienteSelecionado = -1;
    private JComboBox<String> cbLeituras;
    private JComboBox<String> cbMetodoPagamento;
    private JButton btnPagar;
    private JButton btnLimpar;
    private JLabel lblValor;
    private JLabel lblMensagem;
    private List<Leitura> leiturasNaoPagas;
    private Utilizador utilizadorLogado;

    public RegistoPagamento(Utilizador utilizadorLogado) {
        this.utilizadorLogado = utilizadorLogado;
        setLayout(null);
        Estilos.estilizarFundo(this);

        JLabel titulo = new JLabel("Registo de Pagamento");
        titulo.setFont(Estilos.FONTE_TITULO);
        titulo.setBounds(150, 30, 300, 30);
        add(titulo);

        JLabel lblPesquisa = new JLabel("Cliente (codigo ou nome):");
        lblPesquisa.setBounds(80, 90, 180, 25);
        lblPesquisa.setFont(Estilos.FONTE_NORMAL);
        add(lblPesquisa);

        txtPesquisaCliente = new JTextField();
        txtPesquisaCliente.setBounds(270, 90, 180, 30);
        txtPesquisaCliente.setFont(Estilos.FONTE_NORMAL);
        add(txtPesquisaCliente);

        btnPesquisar = new JButton("Pesquisar");
        btnPesquisar.setBounds(460, 90, 120, 30);
        Estilos.estilizarBotao(btnPesquisar);
        btnPesquisar.addActionListener(this);
        add(btnPesquisar);

        lblClienteInfo = new JLabel("Nenhum cliente selecionado");
        lblClienteInfo.setBounds(80, 130, 500, 25);
        lblClienteInfo.setFont(Estilos.FONTE_NORMAL);
        add(lblClienteInfo);

        JLabel lblLeitura = new JLabel("Leitura nao paga:");
        lblLeitura.setBounds(80, 170, 150, 25);
        lblLeitura.setFont(Estilos.FONTE_NORMAL);
        add(lblLeitura);

        cbLeituras = new JComboBox<>();
        cbLeituras.setBounds(240, 170, 300, 30);
        cbLeituras.setEnabled(false);
        cbLeituras.addActionListener(this);
        add(cbLeituras);

        JLabel lblMetodo = new JLabel("Metodo de pagamento:");
        lblMetodo.setBounds(80, 210, 150, 25);
        lblMetodo.setFont(Estilos.FONTE_NORMAL);
        add(lblMetodo);

        cbMetodoPagamento = new JComboBox<>(new String[]{"Dinheiro", "Mpesa", "Emola", "Transferencia Bancaria"});
        cbMetodoPagamento.setBounds(240, 210, 200, 30);
        cbMetodoPagamento.setFont(Estilos.FONTE_NORMAL);
        add(cbMetodoPagamento);

        btnPagar = new JButton("Registrar Pagamento");
        btnPagar.setBounds(200, 270, 200, 40);
        Estilos.estilizarBotao(btnPagar);
        btnPagar.addActionListener(this);
        btnPagar.setEnabled(false);
        add(btnPagar);

        btnLimpar = new JButton("Limpar");
        btnLimpar.setBounds(410, 270, 120, 40);
        Estilos.estilizarBotaoLimpar(btnLimpar);
        btnLimpar.addActionListener(this);
        add(btnLimpar);

        lblValor = new JLabel("");
        lblValor.setBounds(80, 330, 400, 25);
        lblValor.setFont(Estilos.FONTE_NORMAL);
        add(lblValor);

        lblMensagem = new JLabel("");
        lblMensagem.setBounds(80, 370, 500, 25);
        lblMensagem.setFont(Estilos.FONTE_NORMAL);
        add(lblMensagem);
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

    private void carregarLeituras() {
        if (codClienteSelecionado == -1) return;
        cbLeituras.setEnabled(false);
        btnPagar.setEnabled(false);
        cbLeituras.removeAllItems();
        lblValor.setText("");
        new SwingWorker<List<Leitura>, Void>() {
            @Override
            protected List<Leitura> doInBackground() throws Exception {
                return new LeituraDAO().listarPorCliente(codClienteSelecionado, false);
            }
            @Override
            protected void done() {
                try {
                    leiturasNaoPagas = get();
                    if (leiturasNaoPagas.isEmpty()) {
                        cbLeituras.addItem("Nenhuma leitura em debito");
                        lblValor.setText("");
                        lblValor.setForeground(Estilos.COR_ERRO);
                        btnPagar.setEnabled(false);
                    } else {
                        for (Leitura l : leiturasNaoPagas) {
                            cbLeituras.addItem("Leitura de " + l.getDataLeitura() + " - Valor: " + l.getValorPagar() + " MT");
                        }
                        cbLeituras.setSelectedIndex(0);
                        double valor = leiturasNaoPagas.get(0).getValorPagar();
                        lblValor.setText(String.format("Valor a pagar: %.2f MT", valor));
                        lblValor.setForeground(Estilos.COR_PRIMARIA);
                        cbLeituras.setEnabled(true);
                        btnPagar.setEnabled(true);
                    }
                } catch (Exception ex) {
                    lblMensagem.setForeground(Color.RED);
                    lblMensagem.setText("Erro ao carregar leituras.");
                }
            }
        }.execute();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnPesquisar) {
            String texto = txtPesquisaCliente.getText().trim();
            if (texto.isEmpty()) {
                lblMensagem.setForeground(Color.RED);
                lblMensagem.setText("Digite o codigo ou nome do cliente.");
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
                            lblClienteInfo.setText("Cliente: " + c.getNome() + " - " + c.getEndereco());
                            carregarLeituras();
                            lblMensagem.setText("");
                        } else {
                            lblClienteInfo.setText("Cliente nao encontrado.");
                            codClienteSelecionado = -1;
                            cbLeituras.removeAllItems();
                            cbLeituras.setEnabled(false);
                            btnPagar.setEnabled(false);
                            lblValor.setText("");
                        }
                    } catch (Exception ex) {
                        lblClienteInfo.setText("Erro na pesquisa.");
                        ex.printStackTrace();
                    } finally {
                        btnPesquisar.setEnabled(true);
                    }
                }
            }.execute();
        }
        else if (e.getSource() == cbLeituras) {
            int idx = cbLeituras.getSelectedIndex();
            if (idx >= 0 && leiturasNaoPagas != null && idx < leiturasNaoPagas.size()) {
                double valor = leiturasNaoPagas.get(idx).getValorPagar();
                lblValor.setText(String.format("Valor a pagar: %.2f MT", valor));
                lblValor.setForeground(Estilos.COR_PRIMARIA);
            }
        }
        else if (e.getSource() == btnPagar) {
            int idxLeitura = cbLeituras.getSelectedIndex();
            if (idxLeitura < 0 || leiturasNaoPagas == null || idxLeitura >= leiturasNaoPagas.size()) {
                lblMensagem.setForeground(Color.RED);
                lblMensagem.setText("Selecione uma leitura valida.");
                return;
            }
            Leitura leitura = leiturasNaoPagas.get(idxLeitura);
            String metodo = (String) cbMetodoPagamento.getSelectedItem();
            btnPagar.setEnabled(false);
            lblMensagem.setForeground(Color.BLUE);
            lblMensagem.setText("A processar...");

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    Pagamento pagamento = new Pagamento();
                    pagamento.setCodLei(leitura.getCodLei());
                    pagamento.setValorPago(leitura.getValorPagar());
                    pagamento.setDataPagamento(new Timestamp(System.currentTimeMillis()));
                    pagamento.setMetodoPagamento(metodo);
                    return new PagamentoDAO().cadastrar(pagamento);
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            lblMensagem.setForeground(Color.GREEN);
                            lblMensagem.setText("Pagamento registado com sucesso!");
                            carregarLeituras();

                            Notificacao notif = new Notificacao();
                            notif.setTipo("Pagamento");
                            notif.setMensagem("Pagamento para cliente cod: " + codClienteSelecionado + " valor: " + leitura.getValorPagar() + " metodo: " + metodo);
                            notif.setNomeFuncionario(utilizadorLogado.getNome());
                            notif.setDataHora(new Timestamp(System.currentTimeMillis()));
                            new NotificacaoDAO().inserir(notif);

                            String mensagem = "PAGAMENTO|Cliente cod: " + codClienteSelecionado + "|Valor: " + leitura.getValorPagar();
                            ConexaoServidor.enviarMensagem(mensagem);

                        } else {
                            lblMensagem.setForeground(Color.RED);
                            lblMensagem.setText("Erro ao registar pagamento.");
                        }
                    } catch (Exception ex) {
                        lblMensagem.setForeground(Color.RED);
                        lblMensagem.setText("Erro na base de dados.");
                        ex.printStackTrace();
                    } finally {
                        btnPagar.setEnabled(true);
                    }
                }
            }.execute();
        }
        else if (e.getSource() == btnLimpar) {
            txtPesquisaCliente.setText("");
            lblClienteInfo.setText("Nenhum cliente selecionado");
            codClienteSelecionado = -1;
            cbLeituras.removeAllItems();
            cbLeituras.setEnabled(false);
            btnPagar.setEnabled(false);
            lblValor.setText("");
            lblMensagem.setText("");
        }
    }
}