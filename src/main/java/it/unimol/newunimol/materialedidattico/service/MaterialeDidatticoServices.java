package it.unimol.newunimol.materialedidattico.service;

import it.unimol.newunimol.materialedidattico.model.*;
import it.unimol.newunimol.materialedidattico.model.dto.DtoDettagliCartella;
import it.unimol.newunimol.materialedidattico.model.dto.DtoDettagliMateriale;
import it.unimol.newunimol.materialedidattico.model.dto.DtoDettagliPagina;
import it.unimol.newunimol.materialedidattico.model.dto.MaterialeEventDTO;
import it.unimol.newunimol.materialedidattico.repository.MaterialeDidatticoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class MaterialeDidatticoServices {

    @Autowired
    private MaterialeDidatticoRepository materialeDidatticoRepository;
    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    private static final Logger logger = LoggerFactory.getLogger(MaterialeDidatticoServices.class);

    public Long addMaterialeDidatticoPagina(String titolo, String descrizione, String autore, String id_corso, String contenuto) {
        Pagina pagina = new Pagina(titolo, descrizione, autore, id_corso, contenuto);
        materialeDidatticoRepository.save(pagina);
        rabbitMQProducer.sendMaterialeCreated(
                new MaterialeEventDTO(
                        pagina.getId_materiale(),
                        pagina.getTitolo(),
                        "pagina",
                        pagina.getAutore(),
                        pagina.getId_corso()
                )
        );
        return pagina.getId_materiale();
    }

    public Long addMaterialeDidatticoFile(String titolo, String descrizione, String autore, String id_corso, MultipartFile file) throws IOException {
        Path targetDir = Paths.get("./FileFolder");
        Files.createDirectories(targetDir);

        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = "file_" + UUID.randomUUID() + "_" + originalFileName;
        Path targetPath = targetDir.resolve(uniqueFileName);

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        MaterialeFile materialeFile = new MaterialeFile(titolo, descrizione, autore, id_corso, originalFileName, targetPath.toString());
        materialeDidatticoRepository.save(materialeFile);
        rabbitMQProducer.sendMaterialeCreated(
                new MaterialeEventDTO(
                        materialeFile.getId_materiale(),
                        materialeFile.getTitolo(),
                        "file",
                        materialeFile.getAutore(),
                        materialeFile.getId_corso()
                )
        );


        return materialeFile.getId_materiale();
    }

    public Long addMaterialeDidatticoCartella(String titolo, String descrizione, String autore, String id_corso, String nome_cartella) {
        Cartella cartella = new Cartella(titolo, descrizione, autore, id_corso, nome_cartella);
        materialeDidatticoRepository.save(cartella);
        rabbitMQProducer.sendMaterialeCreated(
                new MaterialeEventDTO(
                        cartella.getId_materiale(),
                        cartella.getTitolo(),
                        "cartella",
                        cartella.getAutore(),
                        cartella.getId_corso()
                )
        );

        return cartella.getId_materiale();
    }

    public boolean deleteMaterialeDidatticoPagina(Long idPagina) {
        Optional<MaterialeDidattico> optional = materialeDidatticoRepository.findById(idPagina.toString());
        if (optional.isPresent() && optional.get() instanceof Pagina) {
            materialeDidatticoRepository.deleteById(idPagina.toString());
            rabbitMQProducer.sendMaterialeDeleted(
                    new MaterialeEventDTO(
                            idPagina,
                            optional.get().getTitolo(),
                            "pagina",
                            optional.get().getAutore(),
                            optional.get().getId_corso()
                    )
            );

            return true;
        }
        return false;
    }

    public boolean deleteMaterialeDidatticoMaterialeFile(Long idFile){
        try {
            Optional<MaterialeDidattico> optional = materialeDidatticoRepository.findById(idFile.toString());
            if (optional.isPresent() && optional.get() instanceof MaterialeFile file) {
                materialeDidatticoRepository.deleteById(idFile.toString());
                file.deleteFileFromDisk();
                rabbitMQProducer.sendMaterialeDeleted(
                        new MaterialeEventDTO(
                                idFile,
                                file.getTitolo(),
                                "file",
                                file.getAutore(),
                                file.getId_corso()
                        )
                );

                return true;
            }
        }catch (IOException e){
            logger.error("Errore durante la rimozione del file: {}", idFile, e);
            return false;
        }
        return false;
    }

    public File downloadMaterialeDidatticoMaterialeFile(Long idFile) {
        try {
            Optional<MaterialeDidattico> optional = materialeDidatticoRepository.findById(idFile.toString());
            if (optional.isPresent() && optional.get() instanceof MaterialeFile file) {
                return file.getFileFromDisk();
            }

        } catch (IOException e){
            logger.error("Errore durante download del file: {}", idFile, e);
            return null;
        }
        return null;
    }

    @Transactional
    public String addFileInCartella(Long idCartella, MultipartFile multipartFile) {
        Optional<MaterialeDidattico> optional = materialeDidatticoRepository.findById(idCartella.toString());
        if (optional.isPresent() && optional.get() instanceof Cartella cartella) {
            try {
                File tempFile = Files.createTempFile("upload-", multipartFile.getOriginalFilename()).toFile();
                multipartFile.transferTo(tempFile);
                FileInCartella fileInCartella = new FileInCartella(tempFile, cartella);
                cartella.aggiungiFile(fileInCartella);
                materialeDidatticoRepository.save(cartella);
                tempFile.deleteOnExit();
                rabbitMQProducer.sendMaterialeCreated(
                        new MaterialeEventDTO(
                                idCartella,
                                cartella.getTitolo(),
                                "file in cartella",
                                cartella.getAutore(),
                                cartella.getId_corso()
                        )
                );
                return fileInCartella.getNomeFile();
            } catch (IOException e) {
                logger.error("Errore durante upload file in cartella: {}", idCartella, e);
                return null;
            }
        }
        return null;
    }

    public boolean removeFileInCartella(Long idCartella, String fileName) {
        try {
            Optional<MaterialeDidattico> optional = materialeDidatticoRepository.findById(idCartella.toString());
            if (optional.isPresent() && optional.get() instanceof Cartella cartella) {
                cartella.eliminaFile(fileName);
                materialeDidatticoRepository.save(cartella);
                rabbitMQProducer.sendMaterialeDeleted(
                        new MaterialeEventDTO(
                                idCartella,
                                cartella.getTitolo(),
                                "file in cartella",
                                cartella.getAutore(),
                                cartella.getId_corso()
                        )
                );
                return true;
            }
            return false;
        } catch (IOException e){
            logger.error("Errore durante la rimozione del file in cartella: {}", idCartella, e);
        }
        return false;
    }

    @Transactional(readOnly = true)
    public DtoDettagliCartella getDettaglioCartella(Long idCartella) {
        Optional<MaterialeDidattico> optional = materialeDidatticoRepository.findById(idCartella.toString());
        if (optional.isPresent() && optional.get() instanceof Cartella cartella) {
            List<String> fileNames = cartella.getFileInCartella().stream()
                    .map(f -> f.getFile().getName())
                    .toList();

            return new DtoDettagliCartella(
                    cartella.getNomeCartella(),
                    cartella.getDescrizione(),
                    cartella.getAutore(),
                    cartella.getUploadDate(),
                    cartella.getId_corso(),
                    fileNames
            );
        }
        return null;
    }

    @Transactional
    public boolean rimuoviCartella(Long idCartella) {
        try {
            Optional<MaterialeDidattico> optional = materialeDidatticoRepository.findById(idCartella.toString());
            if (optional.isPresent() && optional.get() instanceof Cartella cartella) {
                for (FileInCartella fileInCartella : cartella.getFileInCartella()) {
                    Files.deleteIfExists(Paths.get(fileInCartella.getFile().getPath()));
                }

                Path cartellaDir = Paths.get("./FileFolder/cartella_" + idCartella);
                if (Files.exists(cartellaDir) && Files.isDirectory(cartellaDir)) {
                    Files.delete(cartellaDir);
                }

                materialeDidatticoRepository.delete(cartella);
                rabbitMQProducer.sendMaterialeDeleted(
                        new MaterialeEventDTO(
                                idCartella,
                                cartella.getTitolo(),
                                "cartella",
                                cartella.getAutore(),
                                cartella.getId_corso()
                        )
                );

                return true;
            }
            return false;
        } catch (IOException e){
            logger.error("Errore durante la rimozione della cartella: {}", idCartella, e);
            return false;
        }
    }

    public List<DtoDettagliCartella> getCartelleByCorso(String id_corso) {
        List<Cartella> cartelle = materialeDidatticoRepository.findCartelleByIdCorso(id_corso);
        List<DtoDettagliCartella> result = new ArrayList<>();

        for (Cartella cartella : cartelle) {
            List<String> fileNames = cartella.getFileInCartella().stream()
                    .map(f -> f.getFile().getName())
                    .toList();

            result.add(new DtoDettagliCartella(
                    cartella.getNomeCartella(),
                    cartella.getDescrizione(),
                    cartella.getAutore(),
                    cartella.getUploadDate(),
                    cartella.getId_corso(),
                    fileNames
            ));
        }
        return result;
    }

    public File downloadFileInCartella(Long idCartella, String fileName) {
        Optional<MaterialeDidattico> optional = materialeDidatticoRepository.findById(idCartella.toString());
        if (optional.isPresent() && optional.get() instanceof Cartella cartella) {
            return cartella.getFileInCartella().stream()
                    .filter(f -> f.getNomeFile().equals(fileName))
                    .map(FileInCartella::getFile)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public List<String> getFileByCorso(String id_corso) {
        return materialeDidatticoRepository.findNomiFileByCorso(id_corso);
    }

    public List<String> getPagineByCorso(String id_corso) {
        return materialeDidatticoRepository.findPagineByIdCorso(id_corso)
                .stream()
                .map(Pagina::getTitolo)
                .toList();
    }

    public List<DtoDettagliPagina> getDettagliPagineByCorso(String id_corso) {
        return materialeDidatticoRepository.findPagineByIdCorso(id_corso)
                .stream()
                .map(p -> new DtoDettagliPagina(p, p.getContenuto()))
                .toList();
    }

    public boolean aggiornaPagina(Long idPagina, String nuovoContenuto) {
        Optional<MaterialeDidattico> optional = materialeDidatticoRepository.findById(idPagina.toString());
        if (optional.isPresent() && optional.get() instanceof Pagina pagina) {
            pagina.setContenuto(nuovoContenuto);
            materialeDidatticoRepository.save(pagina);

            rabbitMQProducer.sendMaterialeUpdated(
                    new MaterialeEventDTO(
                            pagina.getId_materiale(),
                            pagina.getTitolo(),
                            "pagina",
                            pagina.getAutore(),
                            pagina.getId_corso()
                    )
            );

            return true;
        }
        return false;
    }


    public DtoDettagliPagina getDettagliPagina(Long idPagina) {
        Optional<MaterialeDidattico> optional = materialeDidatticoRepository.findById(idPagina.toString());
        if (optional.isPresent() && optional.get() instanceof Pagina pagina) {
            return new DtoDettagliPagina(pagina, pagina.getContenuto());
        }
        return null;
    }

    public List<String> getMaterialeByCorso(String idCorso) {
        return materialeDidatticoRepository.findTitoliByIdCorso(idCorso);
    }

    public DtoDettagliMateriale getDettagliMateriale(Long idMateriale) {
        return materialeDidatticoRepository.findById(idMateriale.toString())
                .map(DtoDettagliMateriale::new)
                .orElse(null);
    }

    @Transactional
    public boolean aggiornaTitoloMateriale(Long idMateriale, String nuovoTitolo) {
        Optional<MaterialeDidattico> optional = materialeDidatticoRepository.findById(idMateriale.toString());
        if (optional.isPresent()) {
            MaterialeDidattico materiale = optional.get();
            boolean updated = materialeDidatticoRepository.updateTitolo(idMateriale.toString(), nuovoTitolo) > 0;

            if (updated) {
                rabbitMQProducer.sendMaterialeUpdated(
                        new MaterialeEventDTO(
                                materiale.getId_materiale(),
                                nuovoTitolo,
                                materiale.getTipo(),
                                materiale.getAutore(),
                                materiale.getId_corso()
                        )
                );
            }

            return updated;
        }
        return false;
    }


    @Transactional
    public boolean aggiornaDescrizioneMateriale(Long idMateriale, String nuovaDescrizione) {
        Optional<MaterialeDidattico> optional = materialeDidatticoRepository.findById(idMateriale.toString());
        if (optional.isPresent()) {
            MaterialeDidattico materiale = optional.get();
            boolean updated = materialeDidatticoRepository.updateDescrizione(idMateriale.toString(), nuovaDescrizione) > 0;

            if (updated) {
                rabbitMQProducer.sendMaterialeUpdated(
                        new MaterialeEventDTO(
                                materiale.getId_materiale(),
                                materiale.getTitolo(),
                                materiale.getTipo(),
                                materiale.getAutore(),
                                materiale.getId_corso()
                        )
                );
            }

            return updated;
        }
        return false;
    }

}


