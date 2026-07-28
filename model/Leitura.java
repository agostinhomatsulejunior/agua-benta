package model;

import java.sql.*;

public class Leitura {
    private int codLei;
    private int codCli;
    private double leituraAnterior;
    private double leituraActual;
    private Date dataLeitura;
    private double valorPagar;
    private boolean estadoPagamento;

    public Leitura() {}

    // Getters e Setters
    public int getCodLei() { return codLei; }
    public void setCodLei(int codLei) { this.codLei = codLei; }

    public int getCodCli() { return codCli; }
    public void setCodCli(int codCli) { this.codCli = codCli; }

    public double getLeituraAnterior() { return leituraAnterior; }
    public void setLeituraAnterior(double leituraAnterior) { this.leituraAnterior = leituraAnterior; }

    public double getLeituraActual() { return leituraActual; }
    public void setLeituraActual(double leituraActual) { this.leituraActual = leituraActual; }

    public Date getDataLeitura() { return dataLeitura; }
    public void setDataLeitura(Date dataLeitura) { this.dataLeitura = dataLeitura; }

    public double getValorPagar() { return valorPagar; }
    public void setValorPagar(double valorPagar) { this.valorPagar = valorPagar; }

    public boolean isEstadoPagamento() { return estadoPagamento; }
    public void setEstadoPagamento(boolean estadoPagamento) { this.estadoPagamento = estadoPagamento; }
}