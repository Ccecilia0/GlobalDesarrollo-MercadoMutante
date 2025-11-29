package ar.edu.unlam.parcial.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para responder en el endpoint /mutant.
 * @author Cecilia Calvo - Legajo 46332
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MutantResponse {
    private boolean isMutant;
    private String message;
}