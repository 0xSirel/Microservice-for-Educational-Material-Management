package it.unimol.newunimol.materialedidattico.model;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "materiale_didattico")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_materiale")
public class MaterialeDidattico {
    @Column(name="titolo",nullable = false, unique = false,length = 255)
    private String titolo;
    private String descrizione;
    private LocalDate uploadDate;
    private String autore;
    private String id_corso;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_materiale")
    private Long id_materiale;
    private static int counter=0;


    public MaterialeDidattico(String titolo, String descrizione, String autore, String id_corso) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.uploadDate = LocalDate.now();
        this.autore = autore;
        this.id_corso = id_corso;
        counter++;
    }

    public MaterialeDidattico() {

    }


    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }
    public LocalDate getUploadDate() {
        return uploadDate;
    }
    public String getAutore() {
        return autore;
    }
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    public Long getId_materiale() {
        return id_materiale;
    }

    public String getId_corso() {
        return id_corso;
    }

    public String getTipo() {
        return "materiale";
    }
}
