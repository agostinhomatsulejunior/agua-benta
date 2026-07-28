// view/admin/CadastroGestor.java
package view.admin;

import dao.NotificacaoDAO;
import dao.UtilizadorDAO;
import model.Notificacao;
import model.Utilizador;
import util.ConexaoServidor;
import util.Estilos;
import util.Validater;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

public class CadastroGestor extends JPanel implements ActionListener {
    private JTextField txtNome;
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JButton btnCadastrar;
    private JButton btnLimpar;
    private JLabel lblMensagem;

    public CadastroGestor() {
        setLayout(null);
        setBackground(new Color(248, 248, 248));

        JLabel titulo = new JLabel("Cadastro de Gestor");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(44, 62, 80));
        titulo.setBounds(150, 30, 300, 30);
        add(titulo);

        JLabel lblNome = new JLabel("Nome completo:");
        lblNome.setBounds(100, 90, 120, 25);
        lblNome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(lblNome);

        txtNome = new JTextField();
        txtNome.setBounds(230, 90, 250, 30);
        txtNome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(txtNome);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(100, 140, 120, 25);
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(230, 140, 250, 30);
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(txtEmail);

        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setBounds(100, 190, 120, 25);
        lblSenha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(lblSenha);

        txtSenha = new JPasswordField();
        txtSenha.setBounds(230, 190, 250, 30);
        txtSenha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(txtSenha);

        btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.setBounds(230, 250, 150, 40);
        Estilos.estilizarBotao(btnCadastrar);
        btnCadastrar.addActionListener(this);
        add(btnCadastrar);

        btnLimpar = new JButton("Limpar");
        btnLimpar.setBounds(390, 250, 150, 40);
        Estilos.estilizarBotaoLimpar(btnLimpar);
        btnLimpar.addActionListener(this);
        add(btnLimpar);

        lblMensagem = new JLabel("");
        lblMensagem.setBounds(230, 310, 300, 25);
        lblMensagem.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        add(lblMensagem);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCadastrar) {
            String nome = txtNome.getText().trim();
            String email = txtEmail.getText().trim();
            String senha = new String(txtSenha.getPassword()).trim();

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
            if (!Validater.validarNaoVazio(email)) {
                lblMensagem.setForeground(Color.RED);
                lblMensagem.setText("Email e obrigatorio.");
                return;
            }
            if (!Validater.validarEmail(email)) {
                lblMensagem.setForeground(Color.RED);
                lblMensagem.setText("Email invalido (ex: utilizador@dominio.com).");
                return;
            }
            if (!Validater.validarNaoVazio(senha)) {
                lblMensagem.setForeground(Color.RED);
                lblMensagem.setText("Senha e obrigatoria.");
                return;
            }
            if (!Validater.validarSenha(senha)) {
                lblMensagem.setForeground(Color.RED);
                lblMensagem.setText("Senha deve ter no minimo 6 caracteres.");
                return;
            }

            btnCadastrar.setEnabled(false);
            lblMensagem.setForeground(Color.BLUE);
            lblMensagem.setText("A processar...");

            final String nomeFinal = nome;
            final String emailFinal = email;
            final String senhaFinal = senha;

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    Utilizador u = new Utilizador();
                    u.setNome(nomeFinal);
                    u.setEmail(emailFinal);
                    u.setSenha(senhaFinal);
                    u.setPerfil("Gestor");
                    u.setDataContrato(Date.valueOf(LocalDate.now()));
                    return new UtilizadorDAO().cadastrar(u);
                }

                @Override
                protected void done() {
                    try {
                        boolean sucesso = get();
                        if (sucesso) {
                            lblMensagem.setForeground(Estilos.COR_SUCESSO);
                            lblMensagem.setText("Gestor cadastrado com sucesso!");
                            txtNome.setText("");
                            txtEmail.setText("");
                            txtSenha.setText("");

                            // notificacao
                            Notificacao notif = new Notificacao();
                            notif.setTipo("Gestor");
                            notif.setMensagem("Novo gestor cadastrado: " + nomeFinal);
                            notif.setNomeFuncionario("Admin");
                            notif.setDataHora(new Timestamp(System.currentTimeMillis()));
                            new NotificacaoDAO().inserir(notif);

                            String mensagem = "GESTOR|Novo gestor: " + nomeFinal;
                            ConexaoServidor.enviarMensagem(mensagem);

                        } else {
                            lblMensagem.setForeground(Estilos.COR_ERRO);
                            lblMensagem.setText("Erro ao cadastrar gestor.");
                        }
                    } catch (Exception ex) {
                        lblMensagem.setForeground(Estilos.COR_ERRO);
                        lblMensagem.setText("Erro na base de dados.");
                        ex.printStackTrace();
                    } finally {
                        btnCadastrar.setEnabled(true);
                    }
                }
            }.execute();
        }

        if (e.getSource() == btnLimpar) {
            txtNome.setText("");
            txtEmail.setText("");
            txtSenha.setText("");
            lblMensagem.setText("");
        }
    }
}