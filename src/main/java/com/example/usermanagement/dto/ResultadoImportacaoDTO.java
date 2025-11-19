package com.example.usermanagement.dto;

import java.util.ArrayList;
import java.util.List;

public class ResultadoImportacaoDTO {
    private int totalLinhas;
    private int inseridos;
    private List<String> erros = new ArrayList<>();

    public int getTotalLinhas() { return totalLinhas; }
    public void setTotalLinhas(int totalLinhas) { this.totalLinhas = totalLinhas; }

    public int getInseridos() { return inseridos; }
    public void setInseridos(int inseridos) { this.inseridos = inseridos; }

    public List<String> getErros() { return erros; }
    public void addErro(String erro) { this.erros.add(erro); }
}
