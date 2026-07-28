package model;

import java.sql.*;

public class Cliente {
    private int codCliente;
    private String nome;
    private String endereco;
    private int codFunc;      // código do gestor que cadastrou
    private Date dataContrato;

    public Cliente() {}

    public Cliente(int codCliente, String nome, String endereco, int codFunc, Date dataContrato) {
        this.codCliente = codCliente;
        this.nome = nome;
        this.endereco = endereco;
        this.codFunc = codFunc;
        this.dataContrato = dataContrato;
    }

    // Getters e Setters
    public int getCodCliente() { return codCliente; }
    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
    public int getCodFunc() { return codFunc; }
    public Date getDataContrato() { return dataContrato; }

    public void setCodCliente(int codCliente) { 
        this.codCliente = codCliente; 
    }
    
    public void setNome(String nome) { 
        this.nome = nome; 
    }
    
    public void setEndereco(String endereco) { 
        this.endereco = endereco; 

    }
    
    public void setCodFunc(int codFunc) { 
        this.codFunc = codFunc; 
    }
    
    public void setDataContrato(Date dataContrato) { 
        this.dataContrato = dataContrato; 
    }
}