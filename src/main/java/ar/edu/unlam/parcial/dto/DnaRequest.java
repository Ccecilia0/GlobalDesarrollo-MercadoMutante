package ar.edu.unlam.parcial.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para recibir secuencias de ADN en el endpoint /mutant.
 * Incluye validaciones básicas.
 * @author Cecilia Calvo - Legajo 46332
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DnaRequest {
    private String[] dna;

    /**
     * Valida que el array no sea nulo o vacío.
     */
    public boolean isValid() {
        return dna != null && dna.length > 0;
    }
}