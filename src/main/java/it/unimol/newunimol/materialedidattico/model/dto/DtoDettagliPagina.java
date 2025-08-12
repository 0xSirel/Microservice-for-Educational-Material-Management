package it.unimol.newunimol.materialedidattico.model.dto;

import it.unimol.newunimol.materialedidattico.model.MaterialeDidattico;

import java.time.LocalDate;

public class DtoDettagliPagina {
    private String nomePagina;
    private String descrizione;
    private String autore;
    private LocalDate dataCreazione;
    private String contenuto;

    public DtoDettagliPagina(MaterialeDidattico materiale, String contenuto){
        this.nomePagina=materiale.getTitolo();
        this.descrizione=materiale.getDescrizione();
        this.autore=materiale.getAutore();
        this.dataCreazione=materiale.getUploadDate();
        this.contenuto = contenuto;
    }

    public String getNomePagina() {
        return nomePagina;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getAutore() {
        return autore;
    }

    public LocalDate getDataCreazione() {
        return dataCreazione;
    }

    public String getContenuto() {
        return contenuto;
    }
}
