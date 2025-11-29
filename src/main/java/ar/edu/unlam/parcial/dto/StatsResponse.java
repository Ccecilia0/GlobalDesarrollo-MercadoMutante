package ar.edu.unlam.parcial.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para responder con estadísticas en el endpoint /stats.
 * @author Cecilia Calvo - Legajo 46332
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatsResponse {
    private long count_mutant_dna;
    private long count_human_dna;
    private double ratio;
}