package util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Estilos {

    // Cores principais
    public static final Color COR_FUNDO = new Color(248, 248, 248);
    public static final Color COR_PRIMARIA = new Color(44, 62, 80);      // azul escuro / cinza
    public static final Color COR_SECUNDARIA = new Color(52, 73, 94);
    public static final Color COR_DESTAQUE = new Color(41, 128, 185);    // azul claro
    public static final Color COR_ERRO = new Color(192, 57, 43);
    public static final Color COR_SUCESSO = new Color(39, 174, 96);
    public static final Color COR_TEXTO = new Color(44, 62, 80);
    public static final Color COR_TEXTO_CLARO = Color.WHITE;

    // Fontes
    public static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONTE_BOTAO = new Font("Segoe UI", Font.BOLD, 14);

    // Bordas
    public static final Border BORDA_CAMPO = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
    );

    // Método para estilizar um botão padrão
    public static void estilizarBotao(JButton botao) {
        botao.setBackground(new Color(41, 128, 185));
        botao.setForeground(Color.WHITE);
        botao.setFont(FONTE_BOTAO);
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        botao.setOpaque(true);
        // Forçar override do Nimbus
        botao.putClientProperty("Nimbus.Overrides", Boolean.TRUE);
        botao.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.FALSE);
    }
    
    public static void estilizarBotaoEliminar(JButton botao) {
        botao.setBackground(new Color(192, 57, 43));
        botao.setForeground(Color.WHITE);
        botao.setFont(FONTE_BOTAO);
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        botao.setOpaque(true);
        botao.putClientProperty("Nimbus.Overrides", Boolean.TRUE);
        botao.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.FALSE);
    }

    public static void estilizarBotaoLimpar(JButton botao) {
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 15));
        botao.setBackground(new Color(100, 100, 100)); // cinza
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Método para estilizar um JPanel fundo
    public static void estilizarFundo(JPanel panel) {
        panel.setBackground(COR_FUNDO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    // Método para aplicar Look and Feel Nimbus (chamar no main)
    public static void aplicarLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}