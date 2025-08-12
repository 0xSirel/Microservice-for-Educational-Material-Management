package it.unimol.newunimol.materialedidattico.model;

import jakarta.persistence.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("cartella")
public class Cartella extends MaterialeDidattico {

    private String nomeCartella;

    @OneToMany(mappedBy = "cartella", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileInCartella> fileInCartella = new ArrayList<>();

    public Cartella() {}

    public Cartella(String titolo, String descrizione, String autore, String idCorso, String nomeCartella) {
        super(titolo, descrizione, autore, idCorso);
        this.nomeCartella = nomeCartella;
    }

    public void aggiungiFile(FileInCartella file) {
        file.associaCartella(this);
        this.fileInCartella.add(file);
    }

    public void eliminaFile(String nomeFile) throws IOException {
        var iterator = fileInCartella.iterator();
        while (iterator.hasNext()) {
            FileInCartella f = iterator.next();
            if (f.getNomeFile().equals(nomeFile)) {
                f.removeFile();
                iterator.remove();
                break;
            }
        }
    }

    public FileInCartella downloadFile(String nomeFile) {
        return fileInCartella.stream()
                .filter(f -> f.getNomeFile().equalsIgnoreCase(nomeFile))
                .findFirst()
                .orElse(null);
    }

    public List<FileInCartella> getFileInCartella() {
        return fileInCartella;
    }

    public void setFileInCartella(List<FileInCartella> fileInCartella) {
        this.fileInCartella = fileInCartella;
    }

    public String getNomeCartella() {
        return nomeCartella;
    }

    public void setNomeCartella(String nomeCartella) {
        this.nomeCartella = nomeCartella;
    }
}
