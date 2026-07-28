package view.comum;

import dao.ClienteDAO;
import dao.LeituraDAO;
import dao.NotificacaoDAO;
import dao.TarifaDAO;
import model.Cliente;
import model.Leitura;
import model.Notificacao;
import model.Tarifa;
import model.Utilizador;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public class RegistoLeitura extends JPanel implements ActionListener {
    private JTextField txtPesquisaCliente;
    private JButton btnPesquisar;
    private JLabel lblClienteInfo;
    private int codClienteSelecionado = -1;
    private JTextField txtLeituraActual;
    private JButton btnRegistrar;
    private JButton btnLimpar;
    private JLabel lblValor;
    private JLabel lblMensagem;
    private Utilizador utilizadorLogado;

    public RegistoLeitura(Utilizador utilizadorLogado) {
        this.utilizadorLogado = utilizadorLogado;
        setLayout(null);
        Estilos.estilizarFundo(this);

        JLabel titulo = new JLabel("Registo de Leitura");
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

        JLabel lblLeitura = new JLabel("Leitura Actual (m³):");
        lblLeitura.setBounds(80, 170, 150, 25);
        lblLeitura.setFont(Estilos.FONTE_NORMAL);
        add(lblLeitura);

        txtLeituraActual = new JTextField();
        txtLeituraActual.setBounds(240, 170, 150, 30);
        txtLeituraActual.setFont(Estilos.FONTE_NORMAL);
        //txtLeituraActual.setEnabled(false);
        add(txtLeituraActual);

        btnRegistrar = new JButton("Registrar Leitura");
        btnRegistrar.setBounds(200, 240, 180, 40);
        Estilos.estilizarBotao(btnRegistrar);
        btnRegistrar.addActionListener(this);
        btnRegistrar.setEnabled(false);
        add(btnRegistrar);

        btnLimpar = new JButton("Limpar");
        btnLimpar.setBounds(400, 240, 120, 40);
        Estilos.estilizarBotaoLimpar(btnLimpar);
        btnLimpar.addActionListener(this);
        add(btnLimpar);

        lblValor = new JLabel("");
        lblValor.setBounds(80, 300, 400, 25);
        lblValor.setFont(Estilos.FONTE_NORMAL);
        add(lblValor);

        lblMensagem = new JLabel("");
        lblMensagem.setBounds(80, 340, 500, 25);
        lblMensagem.setFont(Estilos.FONTE_NORMAL);
        add(lblMensagem);
    }

    // metodo auxiliar para selecionar cliente quando ha multiplos
    private Cliente selecionarCliente(List<Cliente> clientes) {
        if (clientes.isEmpty()) return null;
        if (clientes.size() == 1) return clientes.get(0);
        // varios clientes: mostrar opcoes
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
            String texto = txtPesquisaCliente.getText().trim();
            if (!Validater.validarNaoVazio(texto)) {
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
                        // varios clientes: mostrar dialogo na EDT
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
                            txtLeituraActual.setEnabled(true);
                            btnRegistrar.setEnabled(true);
                            lblMensagem.setText("");
                        } else {
                            lblClienteInfo.setText("Cliente nao encontrado.");
                            codClienteSelecionado = -1;
                            txtLeituraActual.setEnabled(false);
                            btnRegistrar.setEnabled(false);
                            txtLeituraActual.setText("");
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
        else if (e.getSource() == btnRegistrar) {
            if (codClienteSelecionado == -1) {
                lblMensagem.setForeground(Color.RED);
                lblMensagem.setText("Pesquise um cliente valido primeiro.");
                return;
            }
            String leituraStr = txtLeituraActual.getText().trim();

            // validacoes usando Validater
            if (!Validater.validarNaoVazio(leituraStr)) {
                lblMensagem.setForeground(Color.RED);
                lblMensagem.setText("Informe a leitura actual.");
                return;
            }
            if (!Validater.validarNumeroPositivo(leituraStr)) {
                lblMensagem.setForeground(Color.RED);
                lblMensagem.setText("Leitura deve ser um numero positivo.");
                return;
            }

            double leituraActual = Double.parseDouble(leituraStr);

            btnRegistrar.setEnabled(false);
            lblMensagem.setForeground(Color.BLUE);
            lblMensagem.setText("A processar...");
            lblValor.setText("");

            new SwingWorker<Leitura, Void>() {
                @Override
                protected Leitura doInBackground() throws Exception {
                    LeituraDAO leituraDAO = new LeituraDAO();
                    Leitura ultima = leituraDAO.ultimaLeitura(codClienteSelecionado);
                    double leituraAnterior = (ultima != null) ? ultima.getLeituraActual() : 0.0;

                    if (leituraActual < leituraAnterior) {
                        throw new Exception("Leitura actual (" + leituraActual + 
                                            ") nao pode ser menor que a anterior (" + leituraAnterior + ").");
                    }

                    LocalDate hoje = LocalDate.now();
                    List<Leitura> leiturasMes = leituraDAO.listarPorCliente(codClienteSelecionado, null);
                    for (Leitura l : leiturasMes) {
                        LocalDate dataLeitura = l.getDataLeitura().toLocalDate();
                        if (dataLeitura.getYear() == hoje.getYear() && dataLeitura.getMonth() == hoje.getMonth()) {
                            throw new Exception("Ja existe uma leitura para este cliente no mes " + 
                                                hoje.getMonth().toString() + " de " + hoje.getYear());
                        }
                    }

                    TarifaDAO tarifaDAO = new TarifaDAO();
                    Tarifa tarifa = tarifaDAO.buscarActual();
                    if (tarifa == null) {
                        throw new Exception("Nenhuma tarifa definida.");
                    }
                    double consumo = leituraActual - leituraAnterior;
                    double valor = consumo * tarifa.getValorPorMetroCubico();
                    if (valor < tarifa.getTaxaMinima()) valor = tarifa.getTaxaMinima();

                    Leitura leitura = new Leitura();
                    leitura.setCodCli(codClienteSelecionado);
                    leitura.setLeituraAnterior(leituraAnterior);
                    leitura.setLeituraActual(leituraActual);
                    leitura.setDataLeitura(Date.valueOf(hoje));
                    leitura.setValorPagar(valor);
                    leitura.setEstadoPagamento(false);

                    boolean ok = leituraDAO.cadastrar(leitura);
                    if (!ok) throw new Exception("Falha ao registar leitura.");
                    return leitura;
                }

                @Override
                protected void done() {
                    try {
                        Leitura leitura = get();
                        lblValor.setText(String.format("Valor a pagar: %.2f MT", leitura.getValorPagar()));
                        lblMensagem.setForeground(Color.GREEN);
                        lblMensagem.setText("Leitura registada com sucesso!");
                        txtLeituraActual.setText("");

                        // notificacao
                        Notificacao notif = new Notificacao();
                        notif.setTipo("Leitura");
                        notif.setMensagem("Nova leitura para cliente cod: " + codClienteSelecionado + " valor: " + leitura.getValorPagar());
                        notif.setNomeFuncionario(utilizadorLogado.getNome());
                        notif.setDataHora(new Timestamp(System.currentTimeMillis()));
                        new NotificacaoDAO().inserir(notif);

                        String mensagem = "LEITURA|Cliente cod: " + codClienteSelecionado + "|Valor: " + leitura.getValorPagar();
                        ConexaoServidor.enviarMensagem(mensagem);

                    } catch (Exception ex) {
                        lblMensagem.setForeground(Color.RED);
                        lblMensagem.setText("Erro: " + ex.getMessage());
                    } finally {
                        btnRegistrar.setEnabled(true);
                    }
                }
            }.execute();
        }
        else if (e.getSource() == btnLimpar) {
            txtPesquisaCliente.setText("");
            lblClienteInfo.setText("Nenhum cliente selecionado");
            txtLeituraActual.setText("");
            txtLeituraActual.setEnabled(false);
            btnRegistrar.setEnabled(false);
            lblValor.setText("");
            lblMensagem.setText("");
            codClienteSelecionado = -1;
        }
    }
}