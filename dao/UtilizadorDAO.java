package dao;
import model.Utilizador;
import dao.ConexaoBD;

import java.sql.*;
import java.util.*;



public class UtilizadorDAO{

    public Utilizador login(String email, String senha) {
        String sql = "SELECT * FROM utilizador WHERE email = ? AND senha = ?";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, senha);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Utilizador u = new Utilizador();
                    u.setCodUtilizador(rs.getInt("codUtilizador"));
                    u.setNome(rs.getString("nome"));
                    u.setEmail(rs.getString("email"));
                    u.setSenha(rs.getString("senha"));
                    u.setPerfil(rs.getString("perfil"));
                    u.setDataContrato(rs.getDate("dataContrato"));
                    u.setDataCadastro(rs.getTimestamp("dataCadastro"));
                    return u;
                } 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*public Utilizador login(String email, String senha) {
        String sql = "SELECT * FROM utilizador WHERE email = ? AND senha = ?";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, senha);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("SQL: SELECT * FROM utilizador WHERE email = '" + email + "' AND senha = '" + senha + "'");
                    Utilizador u = new Utilizador();
                    u.setCodUtilizador(rs.getInt("codUtilizador"));
                    u.setNome(rs.getString("nome"));
                    u.setEmail(rs.getString("email"));
                    u.setSenha(rs.getString("senha"));
                    u.setPerfil(rs.getString("perfil"));
                    u.setDataContrato(rs.getDate("dataContrato"));
                    u.setDataCadastro(rs.getTimestamp("dataCadastro"));
                    return u;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    //verifica se existe o admin no sistema
    public boolean existeAdmin(){
        String sql = "SELECT COUNT(*) FROM utilizador WHERE perfil = 'Admin' ";
        try (Connection con = ConexaoBD.getConexao();
            PreparedStatement ps = con.prepareStatement(sql)){
            try (ResultSet rs= ps.executeQuery()) {
                if(rs.next()){
                    int total = rs.getInt(1);
                    return total>0;
                } 
             } 
            }catch (SQLException e) {
                e.printStackTrace();
        }
        return false;
    }

    //regista utilizador(gestor)
    public boolean cadastrar(Utilizador u){
        String sql = "INSERT INTO utilizador (nome, email, senha, perfil, dataContrato) VALUES (?, ?, ?, ?, ?)";

        try (Connection con  = ConexaoBD.getConexao();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, u.getNome());
                ps.setString(2, u.getEmail());
                ps.setString(3, u.getSenha());
                ps.setString(4, u.getPerfil());
                //para  garantir que nao lance exception por causa do campo ser null na BDpor causa do jdbc
                if (u.getDataContrato() == null) {
                    ps.setNull(5, java.sql.Types.DATE);
                } else {
                    ps.setDate(5, u.getDataContrato());
                }
            
            int linhas = ps.executeUpdate();
            return linhas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Utilizador> listarPorPerfil(String perfil) {
        List<Utilizador> lista = new ArrayList<>();
        String sql = "SELECT * FROM utilizador WHERE perfil = ?";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, perfil);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Utilizador u = new Utilizador();
                    u.setCodUtilizador(rs.getInt("codUtilizador"));
                    u.setNome(rs.getString("nome"));
                    u.setEmail(rs.getString("email"));
                    u.setSenha(rs.getString("senha")); // cuidado: senha pode ser omitida
                    u.setPerfil(rs.getString("perfil"));
                    u.setDataContrato(rs.getDate("dataContrato"));
                    u.setDataCadastro(rs.getTimestamp("dataCadastro"));
                    lista.add(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean eliminar(int codUtilizador, int novoGestorCod) {
        // novoGestorCod = codigo do gestor para onde transferir os clientes
        // se for -1, significa que nao ha gestor disponivel ou o admin cancelou
        Connection con = null;
        PreparedStatement ps = null;
        boolean sucesso = false;
        try {
            con = ConexaoBD.getConexao();
            con.setAutoCommit(false);

            // 1. Transferir clientes para o novo gestor (se houver)
            if (novoGestorCod > 0) {
                String sqlTransfer = "UPDATE cliente SET codFunc = ? WHERE codFunc = ?";
                ps = con.prepareStatement(sqlTransfer);
                ps.setInt(1, novoGestorCod);
                ps.setInt(2, codUtilizador);
                ps.executeUpdate();
                ps.close();
            }

            // 2. Eliminar o gestor
            String sqlDelete = "DELETE FROM utilizador WHERE codUtilizador = ? AND perfil = 'Gestor'";
            ps = con.prepareStatement(sqlDelete);
            ps.setInt(1, codUtilizador);
            int linhas = ps.executeUpdate();
            sucesso = (linhas > 0);

            con.commit();
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

    public List<Utilizador> listarGestoresExceto(int codGestorExcluir) {
        List<Utilizador> lista = new ArrayList<>();
        String sql = "SELECT * FROM utilizador WHERE perfil = 'Gestor' AND codUtilizador != ?";
        try (Connection con = ConexaoBD.getConexao();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codGestorExcluir);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Utilizador u = new Utilizador();
                    u.setCodUtilizador(rs.getInt("codUtilizador"));
                    u.setNome(rs.getString("nome"));
                    u.setEmail(rs.getString("email"));
                    u.setPerfil(rs.getString("perfil"));
                    u.setDataContrato(rs.getDate("dataContrato"));
                    lista.add(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

}