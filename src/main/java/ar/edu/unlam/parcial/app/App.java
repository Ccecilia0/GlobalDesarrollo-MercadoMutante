package ar.edu.unlam.parcial.app;

import ar.edu.unlam.parcial.dto.DnaRequest;
import ar.edu.unlam.parcial.dto.ErrorResponse;
import ar.edu.unlam.parcial.dto.MutantResponse;
import ar.edu.unlam.parcial.dto.StatsResponse;
import ar.edu.unlam.parcial.exception.InvalidDnaException;
import ar.edu.unlam.parcial.service.GeneticService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.h2.tools.Server;
import static spark.Spark.*;

import java.util.Map;

/**
 * Clase principal de la aplicación.
 * @author Cecilia Calvo - Legajo 46332
 */
public class App {
    public static void main(String[] args) throws Exception {

        // ========================================
        // INICIAR CONSOLA H2
        // ========================================
        Server h2Server = Server.createWebServer(
                "-web",           // Habilitar interfaz web
                "-webAllowOthers", // Permitir conexiones remotas
                "-webPort", "8082" // Puerto de la consola
        );
        h2Server.start();

        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║       CONSOLA H2 HABILITADA                    ║");
        System.out.println("╠════════════════════════════════════════════════╣");
        System.out.println("║  URL:      http://localhost:8082               ║");
        System.out.println("║  JDBC URL: jdbc:h2:./data/mutantdb             ║");
        System.out.println("║  User:     sa                                  ║");
        System.out.println("║  Password: (dejar vacío)                       ║");
        System.out.println("╚════════════════════════════════════════════════╝");
        System.out.println();
        // ========================================

        GeneticService service = new GeneticService();
        ObjectMapper mapper = new ObjectMapper();

        // Configurar puerto (opcional)
        port(4567);

        // Manejador global de excepciones
        exception(InvalidDnaException.class, (e, req, res) -> {
            res.status(400);
            res.type("application/json");
            try {
                ErrorResponse error = new ErrorResponse(400, "Bad Request", e.getMessage(), req.pathInfo());
                res.body(mapper.writeValueAsString(error));
            } catch (Exception ex) {
                res.body("{\"error\":\"" + e.getMessage() + "\"}");
            }
        });

        exception(Exception.class, (e, req, res) -> {
            res.status(500);
            res.type("application/json");
            try {
                ErrorResponse error = new ErrorResponse(500, "Internal Server Error",
                        "Error interno del servidor", req.pathInfo());
                res.body(mapper.writeValueAsString(error));
            } catch (Exception ex) {
                res.body("{\"error\":\"Error interno del servidor\"}");
            }
            e.printStackTrace();
        });

        // Endpoint para verificar mutantes
        post("/mutant", (req, res) -> {
            res.type("application/json");

            DnaRequest dnaRequest = mapper.readValue(req.body(), DnaRequest.class);
            String[] sequence = dnaRequest.getDna();

            if (sequence == null || sequence.length == 0) {
                throw new InvalidDnaException("El campo 'dna' es requerido y no puede estar vacío");
            }

            boolean isMutant = service.isMutant(sequence);
            res.status(isMutant ? 200 : 403);

            MutantResponse response = new MutantResponse(
                    isMutant,
                    isMutant ? "Mutante detectado" : "Humano"
            );
            return mapper.writeValueAsString(response);
        });

        // Endpoint para estadísticas
        get("/stats", (req, res) -> {
            res.type("application/json");

            long mutants = service.getRepository().countMutants();
            long humans = service.getRepository().countHumans();
            double ratio = humans > 0 ? Math.round((double) mutants / humans * 100.0) / 100.0 : 0;

            StatsResponse statsResponse = new StatsResponse(mutants, humans, ratio);
            return mapper.writeValueAsString(statsResponse);
        });

        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     API REST MUTANTES - ACTIVA                 ║");
        System.out.println("╠════════════════════════════════════════════════╣");
        System.out.println("║  Servidor: http://localhost:4567               ║");
        System.out.println("║                                                ║");
        System.out.println("║  Endpoints disponibles:                        ║");
        System.out.println("║    POST /mutant - Verificar secuencia          ║");
        System.out.println("║    GET  /stats  - Obtener estadísticas         ║");
        System.out.println("╚════════════════════════════════════════════════╝");
    }
}