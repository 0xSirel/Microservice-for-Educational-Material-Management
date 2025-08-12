package it.unimol.newunimol.materialedidattico;

import it.unimol.newunimol.materialedidattico.model.dto.DtoDettagliCartella;
import it.unimol.newunimol.materialedidattico.service.MaterialeDidatticoServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@SpringBootTest
class NewunimolApplicationTests {

	@Autowired
	private MaterialeDidatticoServices materialeDidatticoServices;


	@Test
	void testAddMaterialeDidatticoPagina() {
		String titolo = "Test Tireggretolo";
		String descrizione = "Test Desrgecrizione";
		String autore = "Test Atyrrtgutore";
		String id_corso = "CORtryeSO123";
		String contenuto = "Test Conrgegregrettrhryttenuto della pagina";

		Long idPagina = materialeDidatticoServices.addMaterialeDidatticoPagina(titolo, descrizione, autore, id_corso, contenuto);

		List<String> pagine = materialeDidatticoServices.getPagineByCorso(id_corso);

		assert !pagine.isEmpty() : "La lista delle pagine non dovrebbe essere vuota";
		boolean delFlag = materialeDidatticoServices.deleteMaterialeDidatticoPagina(idPagina);
		assert delFlag : "La cancellazione della pagina dovrebbe essere riuscita";

	}
	@Test
	void testAddMaterialeDidatticoFile() throws IOException {
		String titolo = "Test";
		String descrizione = "Test Descriz";
		String autore = "Test Autore";
		String id_corso = "CORSO";

		Path path = Path.of("Documentazione.md");
		byte[] content = Files.readAllBytes(path);

		MockMultipartFile multipartFile = new MockMultipartFile(
				"file",
				"Documentazione.md",
				"text/markdown",
				content
		);

		Long id_file = materialeDidatticoServices.addMaterialeDidatticoFile(titolo, descrizione, autore, id_corso, multipartFile);

		List<String> files = materialeDidatticoServices.getFileByCorso(id_corso);
		assert !files.isEmpty() : "La lista dei file non dovrebbe essere vuota";

		boolean flag = materialeDidatticoServices.deleteMaterialeDidatticoMaterialeFile(id_file);
		assert flag : "Il file non è stato eliminato";
	}

	@Test
	void testAddMaterialeDidatticoCartella() throws IOException {
		String titolo = "Test";
		String descrizione = "Test Descriz";
		String autore = "Test Autore";
		String id_corso = "CORSO";
		String nomeCartella = "Cartella1";

		Long idCartella = materialeDidatticoServices.addMaterialeDidatticoCartella(titolo, descrizione, autore, id_corso, nomeCartella);

		Path path = Path.of("Documentazione.md");
		byte[] content = Files.readAllBytes(path);

		MockMultipartFile multipartFile = new MockMultipartFile(
				"file",
				"Documentazione.md",
				"text/markdown",
				content
		);

		String a = materialeDidatticoServices.addFileInCartella(idCartella, multipartFile);
		assert a != null : "L'aggiunta del file nella cartella ha fallito";

		DtoDettagliCartella cartella = materialeDidatticoServices.getDettaglioCartella(idCartella);
		List<String> nomiFile = cartella.nomiFile();
		assert !nomiFile.isEmpty() : "La lista dei file non dovrebbe essere vuota";

		boolean flag2 = materialeDidatticoServices.rimuoviCartella(idCartella);
		assert flag2 : "Il materiale didattico non è stato eliminato";
	}

}
