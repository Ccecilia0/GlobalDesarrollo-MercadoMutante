package ar.edu.unlam.parcial.utils;

/**
 * Valida secuencias genéticas.
 * @author Cecilia Calvo - Legajo 46332
 */
public class SequenceValidator {
    public boolean isValid(String[] sequence) {
        if (sequence == null || sequence.length == 0) return false;
        int n = sequence.length;
        for (String row : sequence) {
            if (row == null || row.length() != n) return false;
            for (char c : row.toCharArray()) {
                if (c != 'A' && c != 'T' && c != 'C' && c != 'G') return false;
            }
        }
        return true;
    }
}
