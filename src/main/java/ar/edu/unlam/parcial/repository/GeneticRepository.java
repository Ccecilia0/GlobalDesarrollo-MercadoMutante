package ar.edu.unlam.parcial.repository;

import ar.edu.unlam.parcial.model.GeneticSequence;
import java.util.List;

/**
 * Interfaz para el repositorio de secuencias genéticas.
 * @author Cecilia Calvo - Legajo 46332
 */
public interface GeneticRepository {
    void save(GeneticSequence sequence);
    List<GeneticSequence> findAll();
    long countMutants();
    long countHumans();
}
