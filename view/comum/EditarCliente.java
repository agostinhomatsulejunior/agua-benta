// view/comum/EditarCliente.java
package view.comum;

import dao.ClienteDAO;
import dao.UtilizadorDAO;
import dao.NotificacaoDAO;
import model.Notificacao;
import model.Cliente;
import model.Utilizador;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.sql.Timestamp;

public class EditarCliente extends JPanel implements ActionListener {
    private JTextField txtNome;
    private JTextField txtEndereco;
    private JComboBox<String> cbGestor;
    private JButton btnSalvar;
    private JButton btnCancelar;
    private JLabel lblMensagem;
    private Cliente cliente;
    private Utilizador utilizadorLogado;
    private Runnable onUpdateCallback;

    public EditarCliente(Utilizador utilizadorLogado, Cliente cliente, Runnable onUpdateCallback) {
        this.utilizadorLogado = utilizadorLogado;
        this.cliente = cliente;
        this.onUpdateCallback = onUpdateCallback;

        setLayout(null);
        Estilos.estilizarFundo(this);

        JLabel titulo = new JLabel("Editar Cliente - Cod: " + cliente.getCodCliente());
        titulo.setFont(Estilos.FONTE_TITULO);
        titulo.setBounds(150, 30, 350, 30);
        add(titulo);

        JLabel lblNome = new JLabel("Nome completo:");
        lblNome.setBounds(80, 90, 120, 25);
        lblNome.setFont(Estilos.FONTE_NORMAL);
        add(lblNome);

        txtNome = new JTextField(cliente.getNome());
        txtNome.setBounds(210, 90, 250, 30);
        txtNome.setFont(Estilos.FONTE_NORMAL);
        add(txtNome);

        JLabel lblEndereco = new JLabel("Endereco:");
        lblEndereco.setBounds(80, 140, 120, 25);
        lblEndereco.setFont(Estilos.FONTE_NORMAL);
        add(lblEndereco);

        txtEndereco = new JTextField(cliente.getEndereco());
        txtEndereco.setBounds(210, 140, 250, 30);
        txtEndereco.setFont(Estilos.FONTE_NORMAL);
        add(txtEndereco);

        JLabel lblGestor = new JLabel("Gestor responsavel:");
        lblGestor.setBounds(80, 190, 150, 25);
        lblGestor.setFont(Estilos.FONTE_NORMAL);
        add(lblGestor);

        cbGestor = new JComboBox<>();
        cbGestor.setBounds(240, 190, 220, 30);
        cbGestor.setFont(Estilos.FONTE_NORMAL);
        carregarGestores();
        // pre-selecionar o gestor atual
        for (int i = 0; i < cbGestor.getItemCount(); i++) {
            String item = cbGestor.getItemAt(i);
            if (item.startsWith(String.valueOf(cliente.getCodFunc()) + " - ")) {
                cbGestor.setSelectedIndex(i);
                break;
            }
        }
        if (!"Admin".equals(utilizadorLogado.getPerfil())) {
            cbGestor.setEnabled(false);
        }
        add(cbGestor);

        btnSalvar = new JButton("Salvar");
        btnSalvar.setBounds(200, 260, 150, 40);
        Estilos.estilizarBotao(btnSalvar);
        btnSalvar.addActionListener(this);
        add(btnSalvar);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(360, 260, 150, 40);
        Estilos.estilizarBotaoLimpar(btnCancelar);
        btnCancelar.addActionListener(this);
        add(btnCancelar);

        lblMensagem = new JLabel("");
        lblMensagem.setBounds(200, 320, 350, 25);
        lblMensagem.setFont(Estilos.FONTE_NORMAL);
        add(lblMensagem);
    }

    private void carregarGestores() {
        cbGestor.removeAllItems();
        UtilizadorDAO dao = new UtilizadorDAO();
        List<Utilizador> gestores = dao.listarPorPerfil("Gestor");
        for (Utilizador u : gestores) {
            cbGestor.addItem(u.getCodUtilizador() + " - " + u.getNome());
        }
        if (cbGestor.getItemCount() == 0) {
            cbGestor.addItem("Nenhum gestor disponivel");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSalvar) {
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

            btnSalvar.setEnabled(false);
            lblMensagem.setForeground(Color.BLUE);
            lblMensagem.setText("A processar...");

            final String nomeFinal = nome;
            final String enderecoFinal = endereco;
            final int codFuncFinal;
            if ("Admin".equals(utilizadorLogado.getPerfil())) {
                String selected = (String) cbGestor.getSelectedItem();
                if (selected != null && !selected.startsWith("Nenhum")) {
                    codFuncFinal = Integer.parseInt(selected.split(" - ")[0]);
                } else {
                    codFuncFinal = cliente.getCodFunc();
                }
            } else {
                codFuncFinal = cliente.getCodFunc();
            }

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    cliente.setNome(nomeFinal);
                    cliente.setEndereco(enderecoFinal);
                    cliente.setCodFunc(codFuncFinal);
                    return new ClienteDAO().atualizar(cliente);
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            lblMensagem.setForeground(Color.GREEN);
                            lblMensagem.setText("Cliente atualizado com sucesso!");
                            Notificacao notif = new Notificacao();
                            notif.setTipo("Cliente");
                            notif.setMensagem("Cliente editado: " + cliente.getNome() + " cod: " + cliente.getCodCliente());
                            notif.setNomeFuncionario(utilizadorLogado.getNome());
                            notif.setDataHora(new Timestamp(System.currentTimeMillis()));
                            new NotificacaoDAO().inserir(notif);

                            String mensagem = "CLIENTE_EDITADO|" + cliente.getNome() + "|cod: " + cliente.getCodCliente();
                            ConexaoServidor.enviarMensagem(mensagem);

                            if (onUpdateCallback != null) {
                                onUpdateCallback.run();
                            }
                        } else {
                            lblMensagem.setForeground(Color.RED);
                            lblMensagem.setText("Erro ao atualizar cliente.");
                        }
                    } catch (Exception ex) {
                        lblMensagem.setForeground(Color.RED);
                        lblMensagem.setText("Erro: " + ex.getMessage());
                        ex.printStackTrace();
                    } finally {
                        btnSalvar.setEnabled(true);
                    }
                }
            }.execute();
        }

        if (e.getSource() == btnCancelar) {
            if (onUpdateCallback != null) {
                onUpdateCallback.run();
            }
        }
    }
}