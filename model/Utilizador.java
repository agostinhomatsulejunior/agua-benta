package model;
import java.sql.*;

public class Utilizador{

    private int codUtilizador;
    private String nome;
    private String email;
    private String senha;
    private String perfil;
    private Date dataContrato;
    private Timestamp dataCadastro;


    public Utilizador(){}

    public Utilizador(int codUtilizador, String nome, String email, String senha, 
        String perfil, Date dataContrato, Timestamp dataCadastro){
        this.codUtilizador=codUtilizador;
        this.nome=nome;
        this.email=email;
        this.senha=senha;
        this.perfil=perfil;
        this.dataContrato=dataContrato;
        this.dataCadastro=dataCadastro;
    }
    
    //getters
    public int getCodUtilizador(){
        return codUtilizador;
    }

    public String getNome(){
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getPerfil() {
        return perfil;
    }

    public Date getDataContrato() {
        return dataContrato;
    }

    public Timestamp getDataCadastro() {
        return dataCadastro;
    }

    //setters
    public void setCodUtilizador(int codUtilizador) {
        this.codUtilizador = codUtilizador;
    }

    public void setNome(String nome) {  
        this.nome = nome; 
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public void setDataContrato(Date dataContrato) {
        this.dataContrato = dataContrato;
    }

    public void setDataCadastro(Timestamp dataCadastro) {
        this.dataCadastro = dataCadastro;
    }


}