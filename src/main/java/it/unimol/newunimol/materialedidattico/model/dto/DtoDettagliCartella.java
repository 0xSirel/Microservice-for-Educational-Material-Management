package it.unimol.newunimol.materialedidattico.model.dto;

import java.time.LocalDate;
import java.util.List;

public record DtoDettagliCartella(
        String nomeCartella,
        String descrizione,
        String autore,
        LocalDate dataCreazione,
        String corsoId,
        List<String> nomiFile
) {}
