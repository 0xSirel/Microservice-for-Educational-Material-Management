package it.unimol.newunimol.materialedidattico.model.dto;

import it.unimol.newunimol.materialedidattico.model.MaterialeDidattico;

import java.time.LocalDate;

public class DtoDettagliMateriale {
    private String nome;
    private String descrizione;
    private String autore;
    private LocalDate dataCreazione;

    public DtoDettagliMateriale(MaterialeDidattico materiale){
        this.nome=materiale.getTitolo();
        this.descrizione=materiale.getDescrizione();
        this.autore=materiale.getAutore();
        this.dataCreazione=materiale.getUploadDate();
    }

    public String getNome() {
        return nome;
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
}
