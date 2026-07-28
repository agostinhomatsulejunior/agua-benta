// view/admin/CadastroTarifa.java
package view.admin;

import dao.NotificacaoDAO;
import dao.TarifaDAO;
import model.Notificacao;
import model.Tarifa;
import util.ConexaoServidor;
import util.Estilos;
import util.Validater;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;

public class CadastroTarifa extends JPanel implements ActionListener {
    private JTextField txtValorPorMetroCubico;
    private JTextField txtTaxaMinima;
    private JButton btnCadastrar;
    private JButton btnLimpar;
    private JLabel lblMensagem;

    public CadastroTarifa() {
        setLayout(null);
        Estilos.estilizarFundo(this);

        JLabel titulo = new JLabel("Cadastrar Nova Tarifa");
        titulo.setFont(Estilos.FONTE_TITULO);
        titulo.setForeground(Estilos.COR_TEXTO);
        titulo.setBounds(150, 30, 300, 30);
        add(titulo);

        JLabel lblValor = new JLabel("Valor por metro cubico (MT):");
        lblValor.setBounds(80, 90, 220, 25);
        lblValor.setFont(Estilos.FONTE_NORMAL);
        add(lblValor);

        txtValorPorMetroCubico = new JTextField();
        txtValorPorMetroCubico.setBounds(310, 90, 180, 30);
        txtValorPorMetroCubico.setFont(Estilos.FONTE_NORMAL);
        add(txtValorPorMetroCubico);

        JLabel lblTaxa = new JLabel("Taxa minima (MT):");
        lblTaxa.setBounds(80, 150, 220, 25);
        lblTaxa.setFont(Estilos.FONTE_NORMAL);
        add(lblTaxa);

        txtTaxaMinima = new JTextField();
        txtTaxaMinima.setBounds(310, 150, 180, 30);
        txtTaxaMinima.setFont(Estilos.FONTE_NORMAL);
        add(txtTaxaMinima);

        btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.setBounds(200, 230, 150, 40);
        Estilos.estilizarBotao(btnCadastrar);
        btnCadastrar.addActionListener(this);
        add(btnCadastrar);

        btnLimpar = new JButton("Limpar");
        btnLimpar.setBounds(360, 230, 150, 40);
        Estilos.estilizarBotaoLimpar(btnLimpar);
        btnLimpar.addActionListener(this);
        add(btnLimpar);

        lblMensagem = new JLabel("");
        lblMensagem.setBounds(200, 290, 300, 25);
        lblMensagem.setFont(Estilos.FONTE_NORMAL);
        add(lblMensagem);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCadastrar) {
            String valorStr = txtValorPorMetroCubico.getText().trim();
            String taxaStr = txtTaxaMinima.getText().trim();

            if (!Validater.validarNaoVazio(valorStr)) {
                lblMensagem.setForeground(Color.RED);
                lblMensagem.setText("Valor por metro cubico e obrigatorio.");
                return;
            }
            if (!Validater.validarNumeroPositivo(valorStr)) {
                lblMensagem.setForeground(Color.RED);
                lblMensagem.setText("Valor por metro cubico deve ser um numero positivo.");
                return;
            }
            if (!Validater.validarNaoVazio(taxaStr)) {
                lblMensagem.setForeground(Color.RED);
                lblMensagem.setText("Taxa minima e obrigatoria.");
                return;
            }
            if (!Validater.validarNumeroPositivo(taxaStr)) {
                lblMensagem.setForeground(Color.RED);
                lblMensagem.setText("Taxa minima deve ser um numero positivo.");
                return;
            }

            final double valor = Double.parseDouble(valorStr);
            final double taxa = Double.parseDouble(taxaStr);

            btnCadastrar.setEnabled(false);
            lblMensagem.setForeground(Color.BLUE);
            lblMensagem.setText("A processar...");

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    Tarifa tarifa = new Tarifa();
                    tarifa.setValorPorMetroCubico(valor);
                    tarifa.setTaxaMinima(taxa);
                    tarifa.setDataActualizacao(java.sql.Date.valueOf(java.time.LocalDate.now()));
                    return new TarifaDAO().cadastrar(tarifa);
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            lblMensagem.setForeground(Color.GREEN);
                            lblMensagem.setText("Tarifa cadastrada com sucesso!");
                            txtValorPorMetroCubico.setText("");
                            txtTaxaMinima.setText("");

                            // notificacao
                            Notificacao notif = new Notificacao();
                            notif.setTipo("Tarifa");
                            notif.setMensagem("Nova tarifa cadastrada: " + valor + " MT/m3, taxa minima: " + taxa + " MT");
                            notif.setNomeFuncionario("Admin");
                            notif.setDataHora(new Timestamp(System.currentTimeMillis()));
                            new NotificacaoDAO().inserir(notif);

                            String mensagem = "TARIFA|Nova tarifa: " + valor + " MT/m3";
                            ConexaoServidor.enviarMensagem(mensagem);

                        } else {
                            lblMensagem.setForeground(Color.RED);
                            lblMensagem.setText("Erro ao cadastrar tarifa.");
                        }
                    } catch (Exception ex) {
                        lblMensagem.setForeground(Color.RED);
                        lblMensagem.setText("Erro: " + ex.getMessage());
                        ex.printStackTrace();
                    } finally {
                        btnCadastrar.setEnabled(true);
                    }
                }
            }.execute();
        }

        if (e.getSource() == btnLimpar) {
            txtValorPorMetroCubico.setText("");
            txtTaxaMinima.setText("");
            lblMensagem.setText("");
        }
    }
}