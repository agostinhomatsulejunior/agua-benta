package dao;

import model.Pagamento;
import java.sql.*;
import java.util.*;

public class PagamentoDAO {

    public boolean cadastrar(Pagamento pagamento) {
        String sql = "INSERT INTO pagamento (codLei, valorPago, dataPagamento, metodoPaga) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pagamento.getCodLei());
            ps.setDouble(2, pagamento.getValorPago());
            ps.setTimestamp(3, pagamento.getDataPagamento());
            ps.setString(4, pagamento.getMetodoPagamento());
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                // Após registar pagamento, atualiza o estado da leitura
                LeituraDAO leituraDAO = new LeituraDAO();
                leituraDAO.atualizarEstadoPagamento(pagamento.getCodLei(), true);
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Listar pagamentos de um cliente (útil se quiser mostrar detalhes do pagamento)
    public List<Pagamento> listarPagamentosPorCliente(int codCliente) {
        List<Pagamento> lista = new ArrayList<>();
        String sql = "SELECT p.* FROM pagamento p " +
                     "INNER JOIN leitura l ON p.codLei = l.codLei " +
                     "WHERE l.codCli = ?";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pagamento p = new Pagamento();
                    p.setCodPag(rs.getInt("codPag"));
                    p.setCodLei(rs.getInt("codLei"));
                    p.setValorPago(rs.getDouble("valorPago"));
                    p.setDataPagamento(rs.getTimestamp("dataPagamento"));
                    p.setMetodoPagamento(rs.getString("metodoPaga"));
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public int contarPagamentosMesAtual() {
        String sql = "SELECT COUNT(*) FROM pagamento WHERE MONTH(dataPagamento) = MONTH(CURDATE()) AND YEAR(dataPagamento) = YEAR(CURDATE())";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    
    public double somaPagamentosMesAtual() {
        String sql = "SELECT SUM(valorPago) FROM pagamento WHERE MONTH(dataPagamento) = MONTH(CURDATE()) AND YEAR(dataPagamento) = YEAR(CURDATE())";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }
}