package model;

import java.sql.Timestamp;

public class Pagamento {
    private int codPag;
    private int codLei;
    private double valorPago;
    private Timestamp dataPagamento;
    private String metodoPagamento;

    public Pagamento() {}

    // Getters e Setters
    public int getCodPag() { return codPag; }
    public int getCodLei() { return codLei; }
    public double getValorPago() { return valorPago; }
    public Timestamp getDataPagamento() { return dataPagamento; }
    public String getMetodoPagamento() { return metodoPagamento; }

    public void setCodPag(int codPag) { 
        this.codPag = codPag; 
    }

    public void setCodLei(int codLei) { 
        this.codLei = codLei; 

    }

    public void setValorPago(double valorPago) { 
        this.valorPago = valorPago; 
    }

    public void setDataPagamento(Timestamp dataPagamento) { 
        this.dataPagamento = dataPagamento; 
    }

    public void setMetodoPagamento(String metodoPagamento) { 
        this.metodoPagamento = metodoPagamento; 
    }
}