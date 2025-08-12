package it.unimol.newunimol.materialedidattico.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@DiscriminatorValue("pagina")
public class Pagina extends MaterialeDidattico{
    private String contenuto;

    public Pagina(String titolo, String descrizione, String autore, String id_corso, String contenuto) {
        super(titolo, descrizione, autore, id_corso);

        this.contenuto = contenuto;
    }

    public Pagina() {

    }

    public String getContenuto() {
        return contenuto;
    }
    public void setContenuto(String contenuto) {
        this.contenuto = contenuto;
    }

}
