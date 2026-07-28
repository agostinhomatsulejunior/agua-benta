// util/Validater.java
package util;

public class Validater {

    // nome: apenas letras, espacos, apóstrofo, hífen e acentos
    public static boolean validarNome(String nome) {
        return nome != null && nome.matches("^[a-zA-ZÀ-ÖØ-öø-ÿ' -]+$");
    }

    // email simples
    public static boolean validarEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    // endereco: letras, numeros, espacos, virgulas, pontos, hífen, apóstrofo
    public static boolean validarEndereco(String endereco) {
        return endereco != null && endereco.matches("^[a-zA-Z0-9À-ÖØ-öø-ÿ .,'\\-]+$");
    }

    // numero positivo (double)
    public static boolean validarNumeroPositivo(String valor) {
        try {
            double d = Double.parseDouble(valor);
            return d > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // inteiro positivo (para codigos, etc.)
    public static boolean validarInteiroPositivo(String valor) {
        try {
            int i = Integer.parseInt(valor);
            return i > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // nao vazio
    public static boolean validarNaoVazio(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    // senha com tamanho minimo 6
    public static boolean validarSenha(String senha) {
        return senha != null && senha.length() >= 6;
    }
}