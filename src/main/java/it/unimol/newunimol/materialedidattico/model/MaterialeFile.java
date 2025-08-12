package it.unimol.newunimol.materialedidattico.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Entity
@DiscriminatorValue("file")
public class MaterialeFile extends MaterialeDidattico {

    private String nomeFile;
    private String percorsoFile;

    public MaterialeFile(String titolo,
                         String descrizione,
                         String autore,
                         String id_corso,
                         String nomeFile,
                         String percorsoFile) {
        super(titolo, descrizione, autore, id_corso);
        this.nomeFile = nomeFile;
        this.percorsoFile = percorsoFile;
    }

    public MaterialeFile() {
    }

    public File getFileFromDisk() throws IOException {
        if (this.percorsoFile == null) {
            throw new IOException("Percorso file nullo.");
        }

        Path filePath = Paths.get(this.percorsoFile);
        if (!Files.exists(filePath)) {
            throw new IOException("Il file non esiste: " + filePath.toAbsolutePath());
        }

        return filePath.toFile();
    }

    public void deleteFileFromDisk() throws IOException {
        File file = getFileFromDisk();
        if (file.exists()) {
            Files.delete(file.toPath());
        }
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    public String getPercorsoFile() {
        return percorsoFile;
    }

    public void setPercorsoFile(String percorsoFile) {
        this.percorsoFile = percorsoFile;
    }
}
