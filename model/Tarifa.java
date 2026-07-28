package model;

import java.sql.*;

public class Tarifa {
    private int codTar;
    private double valorPorMetroCubico;
    private double taxaMinima;
    private Date dataActualizacao;

    public Tarifa() {}

    public Tarifa(int codTar, double valorPorMetroCubico, double taxaMinima, Date dataActualizacao) {
        this.codTar = codTar;
        this.valorPorMetroCubico = valorPorMetroCubico;
        this.taxaMinima = taxaMinima;
        this.dataActualizacao = dataActualizacao;
    }

    public int getCodTar() { return codTar; }
    public double getValorPorMetroCubico() { return valorPorMetroCubico; }
    public double getTaxaMinima() { return taxaMinima; }
    public Date getDataActualizacao() { return dataActualizacao; }


    public void setCodTar(int codTar) { 
        this.codTar = codTar; 

    }

    public void setValorPorMetroCubico(double valorPorMetroCubico) { 
        this.valorPorMetroCubico = valorPorMetroCubico; 
    }
    
    public void setTaxaMinima(double taxaMinima) { 
        this.taxaMinima = taxaMinima;

     }

    public void setDataActualizacao(Date dataActualizacao) { 
        this.dataActualizacao = dataActualizacao; 
    }
}