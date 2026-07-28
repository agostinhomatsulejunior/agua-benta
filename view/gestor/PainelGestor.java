package view.gestor;

import model.Utilizador;
import view.Principal;
import view.comum.*;
import util.Estilos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PainelGestor extends JPanel implements ActionListener {
    private Principal principal;
    private Utilizador gestorLogado;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private ListaClientes listaClientes;

    private JPanel cadastroCliente;
    private JPanel registoLeitura;
    private JPanel registoPagamento;
    private JPanel leiturasCliente;
    private JPanel notificacoes;
    private JPanel clientesDebito;
    private JPanel editarCliente;

    public PainelGestor(Principal principal, Utilizador gestorLogado) {
        this.principal = principal;
        this.gestorLogado = gestorLogado;
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(45, 45, 45));
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Menu Clientes
        JMenu mnCliente = new JMenu("Clientes");
        mnCliente.setForeground(new Color(25, 118, 210));
        mnCliente.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JMenuItem miCadastrarCliente = new JMenuItem("Cadastrar Cliente");
        JMenuItem miListarClientes = new JMenuItem("Listar Clientes");
        JMenuItem miClientesDebito = new JMenuItem("Clientes em Debito");
        miCadastrarCliente.addActionListener(this);
        miListarClientes.addActionListener(this);
        miClientesDebito.addActionListener(this);
        mnCliente.add(miCadastrarCliente);
        mnCliente.add(miListarClientes);
        mnCliente.add(miClientesDebito);
        menuBar.add(mnCliente);

        // Menu Leituras
        JMenu mnLeitura = new JMenu("Leituras");
        mnLeitura.setForeground(new Color(25, 118, 210));
        mnLeitura.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JMenuItem miRegistarLeitura = new JMenuItem("Registar Leitura");
        JMenuItem miListarLeituras = new JMenuItem("Listar Leituras de Cliente");
        miRegistarLeitura.addActionListener(this);
        miListarLeituras.addActionListener(this);
        mnLeitura.add(miRegistarLeitura);
        mnLeitura.add(miListarLeituras);
        menuBar.add(mnLeitura);

        // Menu Pagamentos
        JMenu mnPagamento = new JMenu("Pagamentos");
        mnPagamento.setForeground(new Color(25, 118, 210));
        mnPagamento.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JMenuItem miRegistarPagamento = new JMenuItem("Registar Pagamento");
        miRegistarPagamento.addActionListener(this);
        mnPagamento.add(miRegistarPagamento);
        menuBar.add(mnPagamento);

        // Menu Notificacoes
        JMenu mnNotificacoes = new JMenu("Notificacoes");
        mnNotificacoes.setForeground(new Color(25, 118, 210));
        mnNotificacoes.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JMenuItem miVerNotificacoes = new JMenuItem("Ver Notificacoes");
        miVerNotificacoes.addActionListener(this);
        mnNotificacoes.add(miVerNotificacoes);
        menuBar.add(mnNotificacoes);

        // Menu Sistema (Dashboard + Sair)
        JMenu mnSistema = new JMenu("Sistema");
        mnSistema.setForeground(new Color(25, 118, 210));
        mnSistema.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JMenuItem miDashboard = new JMenuItem("Dashboard");
        miDashboard.addActionListener(this);
        mnSistema.add(miDashboard);
        mnSistema.addSeparator();
        JMenuItem miSair = new JMenuItem("Sair");
        miSair.addActionListener(this);
        mnSistema.add(miSair);
        menuBar.add(mnSistema);

        add(menuBar, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Estilos.COR_FUNDO);

        cadastroCliente = new CadastroCliente(gestorLogado);
        listaClientes = new ListaClientes();
        
        //para editar clientes
        listaClientes.setOnEditListener(cliente -> {
            editarCliente = new EditarCliente(gestorLogado, cliente, () -> {
                cardLayout.show(contentPanel, "listClientes");
                listaClientes.carregarDados();
            });
            contentPanel.add(editarCliente, "editarCliente");
            cardLayout.show(contentPanel, "editarCliente");
        });

        registoLeitura = new RegistoLeitura(gestorLogado);
        registoPagamento = new RegistoPagamento(gestorLogado);
        leiturasCliente = new LeituraCliente();
        clientesDebito = new ClientesDebito(gestorLogado);
        notificacoes = new ListaNotificacoes(gestorLogado);


        contentPanel.add(cadastroCliente, "cadCliente");
        contentPanel.add(listaClientes, "listClientes");
        contentPanel.add(registoLeitura, "regLeitura");
        contentPanel.add(registoPagamento, "regPagamento");
        contentPanel.add(leiturasCliente, "leiturasCliente");
        contentPanel.add(notificacoes, "notificacoes");
        contentPanel.add(clientesDebito, "clientesDebito");

        JPanel dashboard = new DashboardGestor(gestorLogado);
        contentPanel.add(dashboard, "dashboard");

        add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "dashboard");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        switch (comando) {
            case "Cadastrar Cliente":
                cardLayout.show(contentPanel, "cadCliente");
                break;
            case "Listar Clientes":
                cardLayout.show(contentPanel, "listClientes");
                break;
            case "Registar Leitura":
                cardLayout.show(contentPanel, "regLeitura");
                break;
            case "Clientes em Debito":
                cardLayout.show(contentPanel, "clientesDebito");
                break;
            case "Registar Pagamento":
                cardLayout.show(contentPanel, "regPagamento");
                break;
            case "Listar Leituras de Cliente":
                cardLayout.show(contentPanel, "leiturasCliente");
                break;
            case "Ver Notificacoes":
                cardLayout.show(contentPanel, "notificacoes");
                break;
            case "Dashboard":
                cardLayout.show(contentPanel, "dashboard");
                break;
            case "Sair":
                principal.mostrarLogin();
                break;
        }
    }
}