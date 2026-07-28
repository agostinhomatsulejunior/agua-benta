package dao;

import model.Tarifa;
import java.sql.*;
import java.util.*;

public class TarifaDAO {

    // Inserir nova tarifa 
    public boolean cadastrar(Tarifa tarifa) {
        String sql = "INSERT INTO tarifa (valorPorMetroCubico, taxaMinima, dataActualizacao) VALUES (?, ?, ?)";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, tarifa.getValorPorMetroCubico());
            ps.setDouble(2, tarifa.getTaxaMinima());
            ps.setDate(3, tarifa.getDataActualizacao());
            int linhas = ps.executeUpdate();
            return linhas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obter a tarifa mais recente 
    public Tarifa buscarActual() {
        String sql = "SELECT * FROM tarifa ORDER BY dataActualizacao DESC LIMIT 1";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Tarifa t = new Tarifa();
                t.setCodTar(rs.getInt("codTar"));
                t.setValorPorMetroCubico(rs.getDouble("valorPorMetroCubico"));
                t.setTaxaMinima(rs.getDouble("taxaMinima"));
                t.setDataActualizacao(rs.getDate("dataActualizacao"));
                return t;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // caso não exista nenhuma tarifa
    }

    // Listar todas as tarifas 
    public List<Tarifa> listarTodas() {
        List<Tarifa> lista = new ArrayList<>();
        String sql = "SELECT * FROM tarifa ORDER BY dataActualizacao DESC";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Tarifa t = new Tarifa();
                t.setCodTar(rs.getInt("codTar"));
                t.setValorPorMetroCubico(rs.getDouble("valorPorMetroCubico"));
                t.setTaxaMinima(rs.getDouble("taxaMinima"));
                t.setDataActualizacao(rs.getDate("dataActualizacao"));
                lista.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean eliminar(int codTar) {
        String sql = "DELETE FROM tarifa WHERE codTar = ?";
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codTar);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}