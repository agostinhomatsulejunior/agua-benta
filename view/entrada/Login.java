package view.entrada;

import view.Principal;
import dao.UtilizadorDAO;
import model.Utilizador;
import view.admin.*;
import view.gestor.*;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;



    public class Login implements ActionListener{
        public JLabel lblImagem = new JLabel();
        public JLabel lblTitulo = new JLabel("Agua Benta Limitada");
        public ImageIcon icon = new ImageIcon(getClass().getResource("agua.jpeg"));
        public JLabel lblEmail = new JLabel("Email");
        public JTextField txtEmail = new JTextField();
        public JLabel lblSenha = new JLabel("Senha");
        public JPasswordField txtSenha = new JPasswordField();
        public JLabel lblErro = new JLabel("Erro email ou senha invalida");
        public JButton btnEntrar = new JButton("Entrar");
        public JButton btnLimpar = new JButton("Limpar");
        public JPanel pnlLogin = new JPanel();
        public JPanel pnlFundoLogin = new JPanel();
        public  Principal frame;


        
        public JPanel pnlLogin(Principal frame){
            this.frame = frame;
            pnlFundoLogin.setLayout(new GridBagLayout());
            pnlFundoLogin.setBackground(new Color(21, 101, 192));
            pnlLogin.setLayout(null);
            pnlLogin.setBackground(Color.white);         

            lblTitulo.setBounds(80,20,300,30);
            lblTitulo.setFont(new Font("Segoe UI",Font.BOLD, 20));
            lblTitulo.setForeground(new Color(25, 118, 210));
            lblTitulo.setOpaque(false); 
            pnlLogin.add(lblTitulo); 

            lblImagem.setIcon(icon);
            lblImagem.setBounds(50,40,450,220);
            lblImagem.setBackground(Color.white); 
            pnlLogin.add(lblImagem);

            lblEmail.setBounds(50,248,200,30);
            lblEmail.setFont(new Font("Segoe UI",Font.BOLD, 17));
            lblEmail.setForeground(new Color(25, 118, 210));
            lblEmail.setOpaque(false); 
            pnlLogin.add(lblEmail); 
            txtEmail.setBounds(50,275,300,35);
            txtEmail.setFont(new Font("Segoe UI",Font.PLAIN, 15));
            txtEmail.setHorizontalAlignment(JTextField.CENTER);
            txtEmail.setForeground(Color.black);
            pnlLogin.add(txtEmail);

            lblSenha.setBounds(50,315,200,30);
            lblSenha.setFont(new Font("Segoe UI",Font.BOLD, 17));
            lblSenha.setForeground(new Color(25, 118, 210));
            lblSenha.setOpaque(false); 
            pnlLogin.add(lblSenha); 
            txtSenha.setBounds(50,340,300,35);
            txtSenha.setFont(new Font("Segoe UI",Font.PLAIN, 15));
            txtSenha.setHorizontalAlignment(JTextField.CENTER);
            txtSenha.setForeground(Color.black);
            pnlLogin.add(txtSenha);

            lblErro.setBounds(50,375,200,20);
            lblErro.setText("");
            lblErro.setFont(new Font("Segoe UI",Font.BOLD, 12));
            lblErro.setForeground(Color.red);
            lblErro.setOpaque(false); 
            pnlLogin.add(lblErro);

            btnEntrar.setText("Entrar");
            btnEntrar.setBounds(50, 400, 140, 40); // move para cima
            btnEntrar.setForeground(Color.white);   
            btnEntrar.setFont(new Font("Segoe UI",Font.BOLD, 15));
            btnEntrar.setOpaque(true);
            btnEntrar.setBackground(new Color(25, 118, 210));   
            btnEntrar.setFocusable(false);
            btnEntrar.setBorderPainted(false);
            btnEntrar.addActionListener(this);
            pnlLogin.add(btnEntrar); 

            
            btnLimpar.setText("Limpar");
            btnLimpar.setBounds(205, 400, 140, 40);
            btnLimpar.setForeground(Color.WHITE);
            btnLimpar.setFont(new Font("Segoe UI", Font.BOLD, 15));
            btnLimpar.setBackground(new Color(100, 100, 100)); // cinza
            btnLimpar.setFocusable(false);
            btnLimpar.setBorderPainted(false);
            btnLimpar.setOpaque(true);
            btnLimpar.addActionListener(this);
            pnlLogin.add(btnLimpar);
            
            pnlLogin.setPreferredSize(new Dimension(400, 490));
            pnlFundoLogin.add(pnlLogin);
            return pnlFundoLogin;
        }

        public void limparCampos() {
            txtEmail.setText("");
            txtSenha.setText("");
            lblErro.setText("");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == btnEntrar) {
                String email = txtEmail.getText().trim();
                String senha = txtSenha.getText().trim();
                if (email.isEmpty() || senha.isEmpty()) {
                    lblErro.setText("Preencha email e senha.");
                    return;
                }
        
                // Desabilitar botão e mostrar cursor de espera
                btnEntrar.setEnabled(false);
                frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                lblErro.setText(""); // limpa mensagem anterior
        
                new SwingWorker<Utilizador, Void>() {
                    @Override
                    protected Utilizador doInBackground() throws Exception {
                        return new UtilizadorDAO().login(email, senha);
                    }
        
                    @Override
                    protected void done() {
                        try {
                            Utilizador u = get();
                            if (u != null) {
                                if (u.getPerfil().equals("Admin")) {
                                    // Criar PainelAdmin e mostrar
                                    PainelAdmin painelAdmin = new PainelAdmin(frame, u);
                                    frame.getContentPane().add(painelAdmin, "admin");
                                    frame.card.show(frame.getContentPane(), "admin");
                                } else if (u.getPerfil().equals("Gestor")) {
                                    PainelGestor painelGestor = new PainelGestor(frame, u);
                                    frame.getContentPane().add(painelGestor, "gestor");
                                    frame.card.show(frame.getContentPane(), "gestor");
                                }
                            } else {
                                lblErro.setText("Email ou senha inválidos");
                            }
                        } catch (Exception ex) {
                            lblErro.setText("Erro ao conectar à base de dados.");
                            ex.printStackTrace();
                        } finally {
                            // Reabilitar botão e restaurar cursor
                            btnEntrar.setEnabled(true);
                            frame.setCursor(Cursor.getDefaultCursor());
                        }
                    }
                }.execute();
            }

            if (e.getSource() == btnLimpar){
                limparCampos();
            }
        }
    }