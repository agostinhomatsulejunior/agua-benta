package dao;

import model.Notificacao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificacaoDAO {

    // inserir uma notificacao
    public boolean inserir(Notificacao notificacao) {
        String sql = "INSERT INTO notificacao (tipo, mensagem, nomeFuncionario, dataHora) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, notificacao.getTipo());
            ps.setString(2, notificacao.getMensagem());
            ps.setString(3, notificacao.getNomeFuncionario());
            ps.setTimestamp(4, notificacao.getDataHora());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // listar todas as notificacoes ordenadas por data decrescente (mais recentes primeiro)
    public List<Notificacao> listarTodas() {
        List<Notificacao> lista = new ArrayList<>();
        String sql = "SELECT * FROM notificacao ORDER BY dataHora DESC";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Notificacao n = new Notificacao();
                n.setCodNotificacao(rs.getInt("codNotificacao"));
                n.setTipo(rs.getString("tipo"));
                n.setMensagem(rs.getString("mensagem"));
                n.setNomeFuncionario(rs.getString("nomeFuncionario"));
                n.setDataHora(rs.getTimestamp("dataHora"));
                lista.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // listar apenas as ultimas N notificacoes (ex: 10)
    public List<Notificacao> listarUltimas(int limite) {
        List<Notificacao> lista = new ArrayList<>();
        String sql = "SELECT * FROM notificacao ORDER BY dataHora DESC LIMIT ?";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notificacao n = new Notificacao();
                    n.setCodNotificacao(rs.getInt("codNotificacao"));
                    n.setTipo(rs.getString("tipo"));
                    n.setMensagem(rs.getString("mensagem"));
                    n.setNomeFuncionario(rs.getString("nomeFuncionario"));
                    n.setDataHora(rs.getTimestamp("dataHora"));
                    lista.add(n);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    public List<Notificacao> listarComFiltro(String tipo, String nomeFuncionario, int limite) {
        List<Notificacao> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM notificacao WHERE 1=1");
        if (tipo != null && !tipo.isEmpty() && !"Todos".equals(tipo)) {
            sql.append(" AND tipo = ?");
        }
        if (nomeFuncionario != null && !nomeFuncionario.trim().isEmpty()) {
            sql.append(" AND nomeFuncionario LIKE ?");
        }
        sql.append(" ORDER BY dataHora DESC");
        if (limite > 0) {
            sql.append(" LIMIT ?");
        }
    
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            if (tipo != null && !tipo.isEmpty() && !"Todos".equals(tipo)) {
                ps.setString(idx++, tipo);
            }
            if (nomeFuncionario != null && !nomeFuncionario.trim().isEmpty()) {
                ps.setString(idx++, "%" + nomeFuncionario + "%");
            }
            if (limite > 0) {
                ps.setInt(idx, limite);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notificacao n = new Notificacao();
                    n.setCodNotificacao(rs.getInt("codNotificacao"));
                    n.setTipo(rs.getString("tipo"));
                    n.setMensagem(rs.getString("mensagem"));
                    n.setNomeFuncionario(rs.getString("nomeFuncionario"));
                    n.setDataHora(rs.getTimestamp("dataHora"));
                    lista.add(n);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}