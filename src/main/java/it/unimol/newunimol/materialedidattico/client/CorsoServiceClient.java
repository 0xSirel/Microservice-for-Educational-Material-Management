package it.unimol.newunimol.materialedidattico.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CorsoServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = LoggerFactory.getLogger(CorsoServiceClient.class);

    public boolean corsoEsiste(Long courseId) {
        try {
            String BASE_URL = "http://localhost:8081/api/v1/public/corsi/corso_exist/";
            ResponseEntity<Boolean> response = restTemplate.getForEntity(BASE_URL + courseId, Boolean.class);
            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            logger.error("Errore nella chiamata al microservizio gestione corsi: {}", e.getMessage());
            return false;
        }
    }
}
