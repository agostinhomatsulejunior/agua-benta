package dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Gere a ligacao a base de dados MySQL.
 *
 * As credenciais NAO estao no codigo. Sao lidas de um ficheiro externo
 * "db.properties" (que nao vai para o repositorio Git). Para configurar,
 * copie "db.properties.example" para "db.properties" e preencha os valores.
 */
public class ConexaoBD {

    private static final String CONFIG_FILE = "db.properties";
    private static final Properties config = new Properties();
    private static boolean carregado = false;

    private static void carregarConfig() throws SQLException {
        if (carregado) return;

        // 1) tenta ler o ficheiro na pasta de execucao
        try (InputStream in = new FileInputStream(CONFIG_FILE)) {
            config.load(in);
            carregado = true;
            return;
        } catch (IOException ignorado) {
            // 2) tenta ler do classpath (util quando empacotado)
            try (InputStream in = ConexaoBD.class.getClassLoader()
                    .getResourceAsStream(CONFIG_FILE)) {
                if (in != null) {
                    config.load(in);
                    carregado = true;
                    return;
                }
            } catch (IOException e) {
                // cai no erro abaixo
            }
        }

        throw new SQLException(
            "Ficheiro de configuracao '" + CONFIG_FILE + "' nao encontrado. " +
            "Copie 'db.properties.example' para 'db.properties' e preencha as credenciais.");
    }

    public static Connection getConexao() throws SQLException {
        carregarConfig();

        String url   = config.getProperty("db.url");
        String user  = config.getProperty("db.user");
        String senha = config.getProperty("db.password");

        if (url == null || user == null) {
            throw new SQLException("Configuracao de base de dados incompleta em '" + CONFIG_FILE + "'.");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL nao encontrado. Adicione o JAR ao classpath.", e);
        }

        return DriverManager.getConnection(url, user, senha);
    }
}
