package it.unimol.newunimol.materialedidattico.repository;

import it.unimol.newunimol.materialedidattico.model.Cartella;
import it.unimol.newunimol.materialedidattico.model.MaterialeDidattico;
import it.unimol.newunimol.materialedidattico.model.Pagina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MaterialeDidatticoRepository extends JpaRepository<MaterialeDidattico, String> {

    @Query("SELECT m.titolo FROM MaterialeDidattico m WHERE m.id_corso = :idCorso")
    List<String> findTitoliByIdCorso(String idCorso);

    @Query("SELECT p FROM Pagina p WHERE p.id_corso = :idCorso")
    List<Pagina> findPagineByIdCorso(String idCorso);

    @Query("SELECT f.nomeFile FROM MaterialeFile f WHERE f.id_corso = :idCorso")
    List<String> findNomiFileByCorso(String idCorso);

    @Query("SELECT c FROM Cartella c WHERE c.id_corso = :idCorso")
    List<Cartella> findCartelleByIdCorso(String idCorso);

    @Modifying
    @Query("UPDATE MaterialeDidattico m SET m.titolo = :titolo WHERE m.id_materiale = :id")
    int updateTitolo(String id, String titolo);

    @Modifying
    @Query("UPDATE MaterialeDidattico m SET m.descrizione = :descrizione WHERE m.id_materiale = :id")
    int updateDescrizione(String id, String descrizione);
}
