package it.unimol.newunimol.materialedidattico.controller;

import it.unimol.newunimol.materialedidattico.client.CorsoServiceClient;
import it.unimol.newunimol.materialedidattico.model.dto.DtoDettagliCartella;
import it.unimol.newunimol.materialedidattico.model.dto.DtoDettagliMateriale;
import it.unimol.newunimol.materialedidattico.model.dto.DtoDettagliPagina;
import it.unimol.newunimol.materialedidattico.service.MaterialeDidatticoServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
public class MaterialeDidatticoController {
    @Autowired
    private MaterialeDidatticoServices materialeDidatticoServices;

    private final CorsoServiceClient corsoServiceClient;

    public MaterialeDidatticoController(CorsoServiceClient corsoServiceClient) {
        this.corsoServiceClient = corsoServiceClient;
    }

    @GetMapping("/api/v1/public/healtcheck")
    public boolean healtcheck(){
        return true;
    }

    @GetMapping("/api/v1/public/materials/get_by_course/{courseId}")
    public ResponseEntity<List<String>> getMaterialsByCourse(@PathVariable Long courseId) {
        if (!corsoServiceClient.corsoEsiste(courseId)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
            List<String> materiali = materialeDidatticoServices.getMaterialeByCorso(courseId.toString());
            if (materiali == null || materiali.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(materiali);
    }

    @GetMapping("/api/v1/public/materials/get_details/{id_material}")
    public ResponseEntity<DtoDettagliMateriale> getMaterialDetailsById(@PathVariable Long id_material) {
        DtoDettagliMateriale dettagli = materialeDidatticoServices.getDettagliMateriale(id_material);
        if (dettagli == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(dettagli);
    }

    @PreAuthorize("hasRole('teach')")
    @PutMapping("/api/v1/materials/update_title/{id_material}")
    public ResponseEntity<Boolean> updateTitleMaterial(@PathVariable Long id_material,
                                                       @RequestParam String new_title) {
        boolean updated = materialeDidatticoServices.aggiornaTitoloMateriale(id_material, new_title);
        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
        return ResponseEntity.ok(true);
    }

    @PreAuthorize("hasRole('teach')")
    @PutMapping("/api/v1/materials/update_description/{id_material}")
    public ResponseEntity<Boolean> updateDescriptionMaterial(@PathVariable Long id_material,
                                                             @RequestParam String new_description) {
        boolean updated = materialeDidatticoServices.aggiornaDescrizioneMateriale(id_material, new_description);
        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
        return ResponseEntity.ok(true);
    }

    @PreAuthorize("hasRole('teach')")
    @PostMapping("/api/v1/materials/upload_file/{courseId}")
    public ResponseEntity<Long> uploadFile(@PathVariable Long courseId,
                                           @RequestParam String titolo,
                                           @RequestParam String descrizione,
                                           @RequestParam("file") MultipartFile file){
        try {
            if (!corsoServiceClient.corsoEsiste(courseId)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String autore = SecurityContextHolder.getContext().getAuthentication().getName();

            Long id = materialeDidatticoServices.addMaterialeDidatticoFile(titolo, descrizione, autore, courseId.toString(), file);
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(id);
        } catch (IOException e){
            return ResponseEntity.badRequest().build();
        }

    }

    @PreAuthorize("hasRole('teach')")
    @DeleteMapping("/api/v1/materials/file/{id_file}")
    public ResponseEntity<Boolean> deleteFile(@PathVariable Long id_file){
        boolean deleted = materialeDidatticoServices.deleteMaterialeDidatticoMaterialeFile(id_file);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
        return ResponseEntity.ok(true);
    }

    @GetMapping("/api/v1/public/materials/file/download/{id_file}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id_file){
        try {
            File file = materialeDidatticoServices.downloadMaterialeDidatticoMaterialeFile(id_file);
            if (file == null || !file.exists()) return ResponseEntity.notFound().build();

            Path path = file.toPath();
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .body(resource);
        }catch (IOException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/v1/public/materials/files/get_by_course/{courseId}")
    public ResponseEntity<List<String>> getFilesByCourseId(@PathVariable Long courseId) {
        if (!corsoServiceClient.corsoEsiste(courseId)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<String> files = materialeDidatticoServices.getFileByCorso(courseId.toString());
        if (files == null || files.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(files);
    }

    @PreAuthorize("hasRole('teach')")
    @PostMapping("/api/v1/materials/folder/course/{courseId}")
    public ResponseEntity<Long> createFolderForCourse(@PathVariable Long courseId,
                                                      @RequestParam String titolo,
                                                      @RequestParam String description,
                                                      @RequestParam String nomeCartella) {

        if (!corsoServiceClient.corsoEsiste(courseId)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        String autore = SecurityContextHolder.getContext().getAuthentication().getName();

        Long id = materialeDidatticoServices.addMaterialeDidatticoCartella(titolo, description, autore, courseId.toString(), nomeCartella);
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(id);
    }

    @PreAuthorize("hasRole('teach')")
    @PostMapping("/api/v1/materials/folder/add_file/{id_folder}")
    public ResponseEntity<String> addFileToFolder(@PathVariable Long id_folder,
                                                  @RequestParam("file") MultipartFile file) {
        String result = materialeDidatticoServices.addFileInCartella(id_folder, file);
        if (result == null || result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/v1/public/materials/folder/get_details/{id_folder}")
    public ResponseEntity<DtoDettagliCartella> getDetailsForFolder(@PathVariable Long id_folder) {
        DtoDettagliCartella dettagli = materialeDidatticoServices.getDettaglioCartella(id_folder);
        if (dettagli == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(dettagli);
    }

    @PreAuthorize("hasRole('teach')")
    @DeleteMapping("/api/v1/materials/folder/{id_folder}")
    public ResponseEntity<Boolean> deleteFolder(@PathVariable Long id_folder) {
        boolean deleted = materialeDidatticoServices.rimuoviCartella(id_folder);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
        return ResponseEntity.ok(true);
    }

    @GetMapping("/api/v1/public/materials/folder/get_by_course/{courseId}")
    public ResponseEntity<List<DtoDettagliCartella>> getFoldersByCourse(@PathVariable Long courseId) {
        if (!corsoServiceClient.corsoEsiste(courseId)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<DtoDettagliCartella> folders = materialeDidatticoServices.getCartelleByCorso(courseId.toString());
        if (folders == null || folders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(folders);
    }

    @GetMapping("/api/v1/public/materials/folder/download/{id_folder}/{fileName}")
    public ResponseEntity<Resource> downloadFileFromFolder(@PathVariable Long id_folder,
                                                           @PathVariable String fileName){
        try {
            File file = materialeDidatticoServices.downloadFileInCartella(id_folder, fileName);
            if (file == null || !file.exists()) return ResponseEntity.notFound().build();

            Path path = file.toPath();
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .body(resource);
        } catch (IOException e){
            return ResponseEntity.notFound().build();
        }

    }

    @PreAuthorize("hasRole('teach')")
    @DeleteMapping("/api/v1/materials/folder/delete_file/{id_folder}/{fileName}")
    public ResponseEntity<Boolean> deleteFileFromFolder(@PathVariable Long id_folder,
                                                        @PathVariable String fileName) {
            boolean deleted = materialeDidatticoServices.removeFileInCartella(id_folder, fileName);
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
            }
            return ResponseEntity.ok(true);
        }

    @PreAuthorize("hasRole('teach')")
    @PostMapping("/api/v1/materials/page/course/{courseId}")
    public ResponseEntity<Long> createPageForCourse(@PathVariable Long courseId,
                                                    @RequestParam String titolo,
                                                    @RequestParam String description,
                                                    @RequestParam String contenuto) {
        if (!corsoServiceClient.corsoEsiste(courseId)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String autore = SecurityContextHolder.getContext().getAuthentication().getName();

        Long id = materialeDidatticoServices.addMaterialeDidatticoPagina(titolo, description, autore, courseId.toString(), contenuto);
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(id);
    }

    @GetMapping("/api/v1/public/materials/page/get_by_course/{courseId}")
    public ResponseEntity<List<DtoDettagliPagina>> getPagesByCourse(@PathVariable Long courseId) {
        if (!corsoServiceClient.corsoEsiste(courseId)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<DtoDettagliPagina> pages = materialeDidatticoServices.getDettagliPagineByCorso(courseId.toString());
        if (pages == null || pages.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(pages);
    }

    @GetMapping("/api/v1/public/materials/page/get_details/{id_page}")
    public ResponseEntity<DtoDettagliPagina> getDetailsPageById(@PathVariable Long id_page) {
        DtoDettagliPagina dettagli = materialeDidatticoServices.getDettagliPagina(id_page);
        if (dettagli == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(dettagli);
    }

    @PreAuthorize("hasRole('teach')")
    @PutMapping("/api/v1/materials/page/update_content/{id_page}")
    public ResponseEntity<Boolean> updateContentPage(@PathVariable Long id_page,
                                                     @RequestParam String contenuto) {
        boolean updated = materialeDidatticoServices.aggiornaPagina(id_page, contenuto);
        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
        return ResponseEntity.ok(true);
    }

    @PreAuthorize("hasRole('teach')")
    @DeleteMapping("/api/v1/materials/page/delete/{id_page}")
    public ResponseEntity<Boolean> deletePageById(@PathVariable Long id_page) {
        boolean deleted = materialeDidatticoServices.deleteMaterialeDidatticoPagina(id_page);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
        return ResponseEntity.ok(true);
    }

}
