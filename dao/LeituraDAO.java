package dao;

import model.Leitura;
import model.Tarifa;
import java.sql.*;
import java.util.*;

public class LeituraDAO {

    public boolean cadastrar(Leitura leitura) {
        String sql = "INSERT INTO leitura (codCli, leituraAnterior, leituraActual, dataLeitura, valorPagar, estadoPaga) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, leitura.getCodCli());
            ps.setDouble(2, leitura.getLeituraAnterior());
            ps.setDouble(3, leitura.getLeituraActual());
            ps.setDate(4, leitura.getDataLeitura());
            ps.setDouble(5, leitura.getValorPagar());
            ps.setBoolean(6, leitura.isEstadoPagamento());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obter a última leitura de um cliente (para saber leitura anterior)
    public Leitura ultimaLeitura(int codCliente) {
        String sql = "SELECT * FROM leitura WHERE codCli = ? ORDER BY dataLeitura DESC LIMIT 1";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Leitura l = new Leitura();
                    l.setCodLei(rs.getInt("codLei"));
                    l.setCodCli(rs.getInt("codCli"));
                    l.setLeituraAnterior(rs.getDouble("leituraAnterior"));
                    l.setLeituraActual(rs.getDouble("leituraActual"));
                    l.setDataLeitura(rs.getDate("dataLeitura"));
                    l.setValorPagar(rs.getDouble("valorPagar"));
                    l.setEstadoPagamento(rs.getBoolean("estadoPaga"));
                    return l;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // cliente nunca teve leitura
    }

    // Listar leituras de um cliente (com estado específico: null = todas, true = pagas, false = não pagas)
    public List<Leitura> listarPorCliente(int codCliente, Boolean estadoPagamento) {
        List<Leitura> lista = new ArrayList<>();
        String sql = "SELECT * FROM leitura WHERE codCli = ?";
        if (estadoPagamento != null) sql += " AND estadoPaga = ?";
        sql += " ORDER BY dataLeitura DESC";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codCliente);
            if (estadoPagamento != null) ps.setBoolean(2, estadoPagamento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Leitura l = new Leitura();
                    l.setCodLei(rs.getInt("codLei"));
                    l.setCodCli(rs.getInt("codCli"));
                    l.setLeituraAnterior(rs.getDouble("leituraAnterior"));
                    l.setLeituraActual(rs.getDouble("leituraActual"));
                    l.setDataLeitura(rs.getDate("dataLeitura"));
                    l.setValorPagar(rs.getDouble("valorPagar"));
                    l.setEstadoPagamento(rs.getBoolean("estadoPaga"));
                    lista.add(l);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Atualizar estado de pagamento da leitura
    public boolean atualizarEstadoPagamento(int codLei, boolean pago) {
        String sql = "UPDATE leitura SET estadoPaga = ? WHERE codLei = ?";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, pago);
            ps.setInt(2, codLei);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int contarLeiturasMesAtual() {
        String sql = "SELECT COUNT(*) FROM leitura WHERE MONTH(dateLeitura) = MONTH(CURDATE()) AND YEAR(dateLeitura) = YEAR(CURDATE())";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int contarLeiturasPorCliente(int codCliente) {
        String sql = "SELECT COUNT(*) FROM leitura WHERE codCli = ?";
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
}