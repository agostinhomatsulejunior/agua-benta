// dao/ClienteDAO.java
package dao;

import model.Cliente;
import java.sql.*;
import java.util.*;

public class ClienteDAO {

    public boolean cadastrar(Cliente cliente) {
        String sql = "INSERT INTO cliente (nome, endereco, codFunc, dataContrato) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getEndereco());
            ps.setInt(3, cliente.getCodFunc());
            ps.setDate(4, cliente.getDataContrato());
            int linhas = ps.executeUpdate(); 
            return linhas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente ORDER BY codCliente";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setCodCliente(rs.getInt("codCliente"));
                c.setNome(rs.getString("nome"));
                c.setEndereco(rs.getString("endereco"));
                c.setCodFunc(rs.getInt("codFunc"));
                c.setDataContrato(rs.getDate("dataContrato"));
                lista.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Cliente buscarPorCodigo(int codCliente) {
        String sql = "SELECT * FROM cliente WHERE codCliente = ?";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente c = new Cliente();
                    c.setCodCliente(rs.getInt("codCliente"));
                    c.setNome(rs.getString("nome"));
                    c.setEndereco(rs.getString("endereco"));
                    c.setCodFunc(rs.getInt("codFunc"));
                    c.setDataContrato(rs.getDate("dataContrato"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Cliente> buscarPorNome(String nome) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE nome LIKE ?";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nome + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cliente c = new Cliente();
                    c.setCodCliente(rs.getInt("codCliente"));
                    c.setNome(rs.getString("nome"));
                    c.setEndereco(rs.getString("endereco"));
                    c.setCodFunc(rs.getInt("codFunc"));
                    c.setDataContrato(rs.getDate("dataContrato"));
                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public int quantidadeLeiturasNaoPagas(int codCliente) {
        String sql = "SELECT COUNT(*) FROM leitura WHERE codCli = ? AND estadoPaga = FALSE";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int quantidadeClientesPorGestor(int codFunc) {
        String sql = "SELECT COUNT(*) FROM cliente WHERE codFunc = ?";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codFunc);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // metodo eliminar com cascata 
    public boolean eliminar(int codCliente) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean sucesso = false;
        try {
            con = ConexaoBD.getConexao();
            con.setAutoCommit(false); // inicia transacao

            // 1. eliminar pagamentos das leituras deste cliente
            String sqlPag = "DELETE p FROM pagamento p INNER JOIN leitura l ON p.codLei = l.codLei WHERE l.codCli = ?";
            ps = con.prepareStatement(sqlPag);
            ps.setInt(1, codCliente);
            ps.executeUpdate();
            ps.close();

            // 2. eliminar leituras do cliente
            String sqlLei = "DELETE FROM leitura WHERE codCli = ?";
            ps = con.prepareStatement(sqlLei);
            ps.setInt(1, codCliente);
            ps.executeUpdate();
            ps.close();

            // 3. eliminar cliente
            String sqlCli = "DELETE FROM cliente WHERE codCliente = ?";
            ps = con.prepareStatement(sqlCli);
            ps.setInt(1, codCliente);
            int linhas = ps.executeUpdate();
            sucesso = (linhas > 0);

            con.commit(); // confirma transacao
        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            try { if (ps != null) ps.close(); if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return sucesso;
    }
    //lista clientes com leituras em atraso
    public List<Cliente> listarClientesEmAtraso(Integer codGestor) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT c.* FROM cliente c " +
                     "INNER JOIN leitura l ON c.codCliente = l.codCli " +
                     "WHERE l.estadoPaga = FALSE ";
        if (codGestor != null) {
            sql += " AND c.codFunc = ? ";
        }
        sql += " ORDER BY c.nome";

        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (codGestor != null) {
                ps.setInt(1, codGestor);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cliente c = new Cliente();
                    c.setCodCliente(rs.getInt("codCliente"));
                    c.setNome(rs.getString("nome"));
                    c.setEndereco(rs.getString("endereco"));
                    c.setCodFunc(rs.getInt("codFunc"));
                    c.setDataContrato(rs.getDate("dataContrato"));
                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean atualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET nome = ?, endereco = ?, codFunc = ? WHERE codCliente = ?";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getEndereco());
            ps.setInt(3, cliente.getCodFunc());
            ps.setInt(4, cliente.getCodCliente());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}