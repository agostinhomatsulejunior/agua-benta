package view.comum;

import dao.ClienteDAO;
import dao.NotificacaoDAO;
import model.Cliente;
import model.Utilizador;
import model.Notificacao;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class CadastroCliente extends JPanel implements ActionListener {
    private JTextField txtNome;
    private JTextField txtEndereco;
    private JButton btnCadastrar;
    private JButton btnLimpar;
    private JLabel lblMensagem;
    private Utilizador utilizadorLogado;

    public CadastroCliente(Utilizador utilizadorLogado) {
        this.utilizadorLogado = utilizadorLogado;
        setLayout(null);
        Estilos.estilizarFundo(this);

        JLabel titulo = new JLabel("Cadastro de Cliente");
        titulo.setFont(Estilos.FONTE_TITULO);
        titulo.setBounds(150, 30, 300, 30);
        add(titulo);

        JLabel lblNome = new JLabel("Nome completo:");
        lblNome.setBounds(80, 90, 120, 25);
        lblNome.setFont(Estilos.FONTE_NORMAL);
        add(lblNome);

        txtNome = new JTextField();
        txtNome.setBounds(210, 90, 250, 30);
        txtNome.setFont(Estilos.FONTE_NORMAL);
        add(txtNome);

        JLabel lblEndereco = new JLabel("Endereço:");
        lblEndereco.setBounds(80, 140, 120, 25);
        lblEndereco.setFont(Estilos.FONTE_NORMAL);
        add(lblEndereco);

        txtEndereco = new JTextField();
        txtEndereco.setBounds(210, 140, 250, 30);
        txtEndereco.setFont(Estilos.FONTE_NORMAL);
        add(txtEndereco);

        btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.setBounds(200, 210, 150, 40);
        Estilos.estilizarBotao(btnCadastrar);
        btnCadastrar.addActionListener(this);
        add(btnCadastrar);

        btnLimpar = new JButton("Limpar");
        btnLimpar.setBounds(360, 210, 150, 40);
        Estilos.estilizarBotaoLimpar(btnLimpar);
        btnLimpar.addActionListener(this);
        add(btnLimpar);

        lblMensagem = new JLabel("");
        lblMensagem.setBounds(200, 270, 300, 25);
        lblMensagem.setFont(Estilos.FONTE_NORMAL);
        add(lblMensagem);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCadastrar) {
            String nome = txtNome.getText().trim();
            String endereco = txtEndereco.getText().trim();

            // validacoes
            if (!Validater.validarNaoVazio(nome)) {
                lblMensagem.setForeground(Color.RED);
                lblMensagem.setText("Nome e obrigatorio.");
                return;
            }
            if (!Validater.validarNome(nome)) {
                lblMensagem.setForeground(Color.RED);
                lblMensagem.setText("Nome invalido (apenas letras, espaco, hifen e apostrofo).");
                return;
            }
            if (!Validater.validarNaoVazio(endereco)) {
                lblMensagem.setForeground(Color.RED);
                lblMensagem.setText("Endereco e obrigatorio.");
                return;
            }
            if (!Validater.validarEndereco(endereco)) {
                lblMensagem.setForeground(Color.RED);
                lblMensagem.setText("Endereco invalido (caracteres especiais nao permitidos).");
                return;
            }

            btnCadastrar.setEnabled(false);
            lblMensagem.setForeground(Color.BLUE);
            lblMensagem.setText("A processar...");

            final String nomeFinal = nome;
            final String enderecoFinal = endereco;

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    Cliente cliente = new Cliente();
                    cliente.setNome(nomeFinal);
                    cliente.setEndereco(enderecoFinal);
                    cliente.setCodFunc(utilizadorLogado.getCodUtilizador());
                    cliente.setDataContrato(java.sql.Date.valueOf(java.time.LocalDate.now()));
                    return new ClienteDAO().cadastrar(cliente);
                }
                @Override
                protected void done() {
                    try {
                        if (get()) {
                            lblMensagem.setForeground(Color.GREEN);
                            lblMensagem.setText("Cliente cadastrado com sucesso!");
                            txtNome.setText("");
                            txtEndereco.setText("");

                            Notificacao notif = new Notificacao();
                            notif.setTipo("Cliente");
                            notif.setMensagem("Novo cliente cadastrado: " + nomeFinal);
                            notif.setNomeFuncionario(utilizadorLogado.getNome());
                            notif.setDataHora(new Timestamp(System.currentTimeMillis()));
                            new NotificacaoDAO().inserir(notif);

                            String mensagem = "CLIENTE|Novo cliente: " + nomeFinal;
                            ConexaoServidor.enviarMensagem(mensagem);
                        } else {
                            lblMensagem.setForeground(Color.RED);
                            lblMensagem.setText("Erro ao cadastrar cliente.");
                        }
                    } catch (Exception ex) {
                        lblMensagem.setForeground(Color.RED);
                        lblMensagem.setText("Erro: " + ex.getMessage());
                    } finally {
                        btnCadastrar.setEnabled(true);
                    }
                }
            }.execute();
        }
        if (e.getSource() == btnLimpar) {
            txtNome.setText("");
            txtEndereco.setText("");
            lblMensagem.setText("");
        }
    }
}