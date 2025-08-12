package it.unimol.newunimol.materialedidattico.model;

import jakarta.persistence.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Entity
public class FileInCartella {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeFile;
    private String percorsoFile;

    @ManyToOne
    @JoinColumn(name = "cartella_id")
    private Cartella cartella;

    @Transient
    private File file;

    public FileInCartella() {}

    public FileInCartella(File inputFile, Cartella cartella) throws IOException {

        this.associaCartella(cartella);

        Path baseDir = Paths.get("./FileFolder", "cartella_" + cartella.getId_materiale().toString());
        if (!Files.exists(baseDir)) {
            Files.createDirectories(baseDir);
        }

        String uniqueFileName = System.currentTimeMillis() + "_" + inputFile.getName();
        Path targetFile = baseDir.resolve(uniqueFileName);
        Files.copy(inputFile.toPath(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        this.percorsoFile = targetFile.toString();
        this.file = targetFile.toFile();
        this.nomeFile = uniqueFileName;
    }

    public void associaCartella(Cartella cartella) {
        this.cartella = cartella;
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public String getPercorsoFile() {
        return percorsoFile;
    }

    public File getFile() {
        return file == null && percorsoFile != null ? new File(percorsoFile) : file;
    }

    public void removeFile() throws IOException {
        if (getFile() != null && getFile().exists()) {
            Files.delete(getFile().toPath());
        }
    }

    public Cartella getCartella() {
        return cartella;
    }
}
