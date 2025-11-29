package ar.edu.unlam.parcial.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa una secuencia genética.
 * @author Cecilia Calvo - Legajo 46332
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GeneticSequence {
    private Long id;
    private String sequence;
    private String dnaHash;  // Hash para deduplicación
    private boolean isMutant;
}
