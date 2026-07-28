package view.admin;

import model.Utilizador;
import view.comum.*;
import view.Principal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PainelAdmin extends JPanel implements ActionListener {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private Utilizador utilizadorLogado;
    private Principal principal;
    private ListaClientes listaClientes;

    private JPanel cadastroGestor;
    private JPanel listaGestores;
    private JPanel cadastroTarifa;
    private JPanel listaTarifas;
    private JPanel cadastroCliente;
    private JPanel registoLeitura;
    private JPanel registoPagamento;
    private JPanel leiturasCliente;
    private JPanel notificacoes;
    private JPanel clientesDebito;
    private JPanel editarCliente;

    public PainelAdmin(Principal principal, Utilizador utilizadorLogado) {
        this.principal = principal;
        this.utilizadorLogado = utilizadorLogado;        
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();

        // Menu Gestores
        JMenu mnGestor = new JMenu("Gestores");
        JMenuItem miCadastrarGestor = new JMenuItem("Cadastrar Gestor");
        JMenuItem miListarGestores = new JMenuItem("Listar Gestores");
        miCadastrarGestor.addActionListener(this);
        miListarGestores.addActionListener(this);
        mnGestor.add(miCadastrarGestor);
        mnGestor.add(miListarGestores);

        // Menu Tarifas
        JMenu mnTarifa = new JMenu("Tarifas");
        JMenuItem miCadastrarTarifa = new JMenuItem("Cadastrar Tarifa");
        JMenuItem miListarTarifas = new JMenuItem("Listar Tarifas");
        miCadastrarTarifa.addActionListener(this);
        miListarTarifas.addActionListener(this);
        mnTarifa.add(miCadastrarTarifa);
        mnTarifa.add(miListarTarifas);

        // Menu Clientes
        JMenu mnCliente = new JMenu("Clientes");
        JMenuItem miCadastrarCliente = new JMenuItem("Cadastrar Clientes");
        JMenuItem miListarClientes = new JMenuItem("Listar Clientes");
        JMenuItem miClientesDebito = new JMenuItem("Clientes em Debito");
        miCadastrarCliente.addActionListener(this);
        miListarClientes.addActionListener(this);
        miClientesDebito.addActionListener(this);
        mnCliente.add(miCadastrarCliente);
        mnCliente.add(miListarClientes);
        mnCliente.add(miClientesDebito);
        // Menu Leituras
        JMenu mnLeitura = new JMenu("Leituras");
        JMenuItem miRegistarLeitura = new JMenuItem("Registar Leituras");
        JMenuItem miRegistarPagamento = new JMenuItem("Registar Pagamento");
        JMenuItem miListarLeitura = new JMenuItem("Listar Leituras");
        miRegistarLeitura.addActionListener(this);
        miRegistarPagamento.addActionListener(this);
        miListarLeitura.addActionListener(this);
        mnLeitura.add(miRegistarLeitura);
        mnLeitura.add(miRegistarPagamento);
        mnLeitura.add(miListarLeitura);

        // Menu Notificacoes
        JMenu mnNotificacoes = new JMenu("Notificacoes");
        JMenuItem miVerNotificacoes = new JMenuItem("Ver Notificacoes");
        miVerNotificacoes.addActionListener(this);
        mnNotificacoes.add(miVerNotificacoes);
        menuBar.add(mnNotificacoes);

        // Menu Sistema 
        JMenu mnSistema = new JMenu("Sistema");
        JMenuItem miDashboard = new JMenuItem("Dashboard");
        miDashboard.addActionListener(this);
        mnSistema.add(miDashboard);
        mnSistema.addSeparator();
        JMenuItem miSair = new JMenuItem("Sair");
        miSair.addActionListener(this);
        mnSistema.add(miSair);

        menuBar.add(mnGestor);
        menuBar.add(mnTarifa);
        menuBar.add(mnCliente);
        menuBar.add(mnLeitura);
        menuBar.add(mnSistema);

        add(menuBar, BorderLayout.NORTH);

        menuBar.setBackground(new Color(45, 45, 45));
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        for (JMenu menu : new JMenu[]{mnGestor, mnTarifa, mnCliente, mnLeitura, mnNotificacoes, mnSistema}) {
            menu.setForeground(new Color(25, 118, 210));
            menu.setFont(new Font("Segoe UI", Font.BOLD, 14));
            menu.setBorderPainted(false);
            menu.setOpaque(true);
            menu.setBackground(new Color(45, 45, 45));
        }

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Inicializa todos os sub‑paineis
        cadastroGestor = new CadastroGestor();
        listaGestores = new ListaGestores(utilizadorLogado);
        cadastroTarifa = new CadastroTarifa();
        listaTarifas = new ListaTarifas();
        cadastroCliente = new CadastroCliente(utilizadorLogado);
        listaClientes = new ListaClientes(); // instancia sem callback
        listaClientes.setOnEditListener(cliente -> {
            editarCliente = new EditarCliente(utilizadorLogado, cliente, () -> {
                cardLayout.show(contentPanel, "listClientes");
                listaClientes.carregarDados();
            });
            contentPanel.add(editarCliente, "editarCliente");
            cardLayout.show(contentPanel, "editarCliente");
        });
        registoLeitura = new RegistoLeitura(utilizadorLogado);
        registoPagamento = new RegistoPagamento(utilizadorLogado);
        leiturasCliente = new LeituraCliente();
        clientesDebito = new ClientesDebito(utilizadorLogado);
        notificacoes = new ListaNotificacoes(utilizadorLogado);
        


        contentPanel.add(cadastroCliente, "cadCliente");
        contentPanel.add(listaClientes, "listClientes");
        contentPanel.add(registoLeitura, "regLeitura");
        contentPanel.add(registoPagamento, "regPagamento");
        contentPanel.add(leiturasCliente, "leiturasCliente");
        contentPanel.add(cadastroGestor, "cadGestor");
        contentPanel.add(listaGestores, "listGestor");
        contentPanel.add(cadastroTarifa, "cadTarifa");
        contentPanel.add(listaTarifas, "listTarifa");
        contentPanel.add(notificacoes, "notificacoes");
        contentPanel.add(clientesDebito, "clientesDebito");

        JPanel dashboardPanel = new DashboardAdmin();
        contentPanel.add(dashboardPanel, "dashboard");

        add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "dashboard");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        switch (comando) {
            case "Cadastrar Gestor":
                cardLayout.show(contentPanel, "cadGestor");
                break;
            case "Listar Gestores":
                cardLayout.show(contentPanel, "listGestor");
                break;
            case "Cadastrar Tarifa":
                cardLayout.show(contentPanel, "cadTarifa");
                break;
            case "Listar Tarifas":
                cardLayout.show(contentPanel, "listTarifa");
                break;
            case "Cadastrar Clientes":
                cardLayout.show(contentPanel, "cadCliente");
                break;
            case "Listar Clientes":
                cardLayout.show(contentPanel, "listClientes");
                break;
            case "Registar Leituras":
                cardLayout.show(contentPanel, "regLeitura");
                break;
            case "Clientes em Debito":
                    cardLayout.show(contentPanel, "clientesDebito");
                    break;
            case "Registar Pagamento":
                cardLayout.show(contentPanel, "regPagamento");
                break;
            case "Listar Leituras":
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