package ar.edu.unlam.parcial.service;

import ar.edu.unlam.parcial.exception.InvalidDnaException;
import ar.edu.unlam.parcial.model.GeneticSequence;
import ar.edu.unlam.parcial.repository.GeneticRepository;
import ar.edu.unlam.parcial.repository.GeneticRepositoryImpl;
import ar.edu.unlam.parcial.utils.SequenceValidator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * Servicio para el análisis de secuencias genéticas.
 * @author Cecilia Calvo - Legajo 46332
 */
public class GeneticService {
    private final GeneticRepository repository;
    private final SequenceValidator validator;

    public GeneticService() throws SQLException {
        this.repository = new GeneticRepositoryImpl();
        this.validator = new SequenceValidator();
    }

    /**
     * Analiza si una secuencia genética corresponde a un mutante.
     * @param sequence Secuencia a analizar.
     * @return true si es mutante, false si es humano.
     */
    public boolean isMutant(String[] sequence) {
        if (!validator.isValid(sequence)) {
            throw new InvalidDnaException("Secuencia de ADN inválida. Debe ser una matriz NxN con solo letras A, T, C, G");
        }
        boolean isMutant = checkMutant(sequence);
        String sequenceStr = String.join(",", sequence);
        String hash = calculateHash(sequenceStr);
        repository.save(new GeneticSequence(null, sequenceStr, hash, isMutant));
        return isMutant;
    }

    /**
     * Calcula el hash SHA-256 de una secuencia de ADN.
     */
    private String calculateHash(String dna) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(dna.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al calcular hash", e);
        }
    }

    /**
     * Verifica si la secuencia es de un mutante.
     * CORREGIDO: Ahora cuenta secuencias y retorna true solo si encuentra 2 o más.
     * @param sequence Secuencia a verificar.
     * @return true si es mutante (2+ secuencias), false si no lo es.
     */
    public boolean checkMutant(String[] sequence) {
        int sequenceCount = 0;

        // Contar secuencias horizontales
        sequenceCount += countHorizontal(sequence);
        if (sequenceCount >= 2) return true; // Terminación temprana

        // Contar secuencias verticales
        sequenceCount += countVertical(sequence);
        if (sequenceCount >= 2) return true; // Terminación temprana

        // Contar secuencias diagonales (izquierda a derecha)
        sequenceCount += countDiagonalLeftToRight(sequence);
        if (sequenceCount >= 2) return true; // Terminación temprana

        // Contar secuencias diagonales (derecha a izquierda)
        sequenceCount += countDiagonalRightToLeft(sequence);

        return sequenceCount >= 2;
    }

    /**
     * Cuenta secuencias horizontales de 4 letras iguales.
     */
    private int countHorizontal(String[] sequence) {
        int count = 0;
        int n = sequence.length;
        for (String row : sequence) {
            for (int j = 0; j <= n - 4; j++) {
                String sub = row.substring(j, j + 4);
                if (sub.matches("AAAA|TTTT|CCCC|GGGG")) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Cuenta secuencias verticales de 4 letras iguales.
     */
    private int countVertical(String[] sequence) {
        int count = 0;
        int n = sequence.length;
        for (int j = 0; j < n; j++) {
            for (int i = 0; i <= n - 4; i++) {
                char c1 = sequence[i].charAt(j);
                char c2 = sequence[i+1].charAt(j);
                char c3 = sequence[i+2].charAt(j);
                char c4 = sequence[i+3].charAt(j);
                if (c1 == c2 && c2 == c3 && c3 == c4) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Cuenta secuencias diagonales (izquierda a derecha) de 4 letras iguales.
     */
    private int countDiagonalLeftToRight(String[] sequence) {
        int count = 0;
        int n = sequence.length;
        for (int i = 0; i <= n - 4; i++) {
            for (int j = 0; j <= n - 4; j++) {
                char c1 = sequence[i].charAt(j);
                char c2 = sequence[i+1].charAt(j+1);
                char c3 = sequence[i+2].charAt(j+2);
                char c4 = sequence[i+3].charAt(j+3);
                if (c1 == c2 && c2 == c3 && c3 == c4) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Cuenta secuencias diagonales (derecha a izquierda) de 4 letras iguales.
     */
    private int countDiagonalRightToLeft(String[] sequence) {
        int count = 0;
        int n = sequence.length;
        for (int i = 0; i <= n - 4; i++) {
            for (int j = 3; j < n; j++) {
                char c1 = sequence[i].charAt(j);
                char c2 = sequence[i+1].charAt(j-1);
                char c3 = sequence[i+2].charAt(j-2);
                char c4 = sequence[i+3].charAt(j-3);
                if (c1 == c2 && c2 == c3 && c3 == c4) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Devuelve el repositorio para acceso desde App.
     */
    public GeneticRepository getRepository() {
        return repository;
    }
}
