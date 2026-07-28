package model;

import java.sql.Timestamp;

public class Notificacao {
    private int codNotificacao;
    private String tipo;          // "Pagamento", "Leitura", "Cliente", "Tarifa", "Gestor"
    private String mensagem;
    private String nomeFuncionario;
    private Timestamp dataHora;

    public Notificacao() {}

    public Notificacao(String tipo, String mensagem, String nomeFuncionario, Timestamp dataHora) {
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.nomeFuncionario = nomeFuncionario;
        this.dataHora = dataHora;
    }

    
    public int getCodNotificacao() { return codNotificacao; }
    public void setCodNotificacao(int codNotificacao) {
         this.codNotificacao = codNotificacao; 
        }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) {
         this.tipo = tipo; 
        }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { 
        this.mensagem = mensagem; 
    }

    public String getNomeFuncionario() { return nomeFuncionario; }
    public void setNomeFuncionario(String nomeFuncionario) { 
        this.nomeFuncionario = nomeFuncionario; 
    }

    public Timestamp getDataHora() { return dataHora; }
    public void setDataHora(Timestamp dataHora) { 
        this.dataHora = dataHora; 
    }
}