package ar.edu.unlam.parcial;

import ar.edu.unlam.parcial.exception.InvalidDnaException;
import ar.edu.unlam.parcial.service.GeneticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el servicio de análisis genético.
 * @author Cecilia Calvo - Legajo 46332
 */
public class GeneticServiceTest {
    private GeneticService service;

    @BeforeEach
    public void setUp() throws Exception {
        service = new GeneticService();
    }

    // ===== TESTS DE MUTANTES (deben retornar TRUE) =====

    @Test
    public void testMutante_DosSecuenciasHorizontales() {
        String[] dna = {
                "AAAATG",
                "TGCAGT",
                "GCTTCC",
                "CCCCTG",
                "GTAGTC",
                "AGTCAC"
        };
        assertTrue(service.isMutant(dna), "Debe detectar mutante con 2 secuencias horizontales");
    }

    @Test
    public void testMutante_DosSecuenciasVerticales() {
        String[] dna = {
                "ATGCGA",
                "ATGTGC",
                "ATATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertTrue(service.isMutant(dna), "Debe detectar mutante con 2 secuencias verticales");
    }

    @Test
    public void testMutante_HorizontalYVertical() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertTrue(service.isMutant(dna), "Debe detectar mutante (horizontal + vertical)");
    }

    @Test
    public void testMutante_DosDiagonales() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CTCCTA",
                "TCACTG"
        };
        assertTrue(service.isMutant(dna), "Debe detectar mutante con diagonales");
    }

    @Test
    public void testMutante_VariasSecuencias() {
        String[] dna = {
                "AAAA",
                "AAAA",
                "AAAA",
                "AAAA"
        };
        assertTrue(service.isMutant(dna), "Debe detectar mutante con muchas secuencias");
    }

    // ===== TESTS DE HUMANOS (deben retornar FALSE) =====

    @Test
    public void testHumano_SoloUnaSecuencia() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATTT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
        };
        assertFalse(service.isMutant(dna), "Debe detectar humano (solo 1 secuencia)");
    }

    @Test
    public void testHumano_NingunaSecuencia() {
        String[] dna = {
                "ATGC",
                "CAGT",
                "TTAT",
                "AGAC"
        };
        assertFalse(service.isMutant(dna), "Debe detectar humano (sin secuencias)");
    }

    @Test
    public void testHumano_Matriz6x6_SinSecuencias() {
        String[] dna = {
                "ATGCAT",
                "CAGTGC",
                "TCATGT",
                "AGACGG",
                "GCATCA",
                "TCGCTG"
        };
        assertFalse(service.isMutant(dna), "Debe detectar humano en matriz 6x6 sin secuencias");
    }

    // ===== TESTS DE VALIDACIÓN =====

    @Test
    public void testSecuenciaInvalida_ContieneX() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATXG",
                "AGACGG"
        };
        assertThrows(InvalidDnaException.class, () -> {
            service.isMutant(dna);
        }, "Debe lanzar excepción por caracter inválido");
    }

    @Test
    public void testSecuenciaNula() {
        assertThrows(InvalidDnaException.class, () -> {
            service.isMutant(null);
        }, "Debe lanzar excepción por secuencia nula");
    }

    @Test
    public void testSecuenciaVacia() {
        String[] dna = {};
        assertThrows(InvalidDnaException.class, () -> {
            service.isMutant(dna);
        }, "Debe lanzar excepción por secuencia vacía");
    }

    @Test
    public void testSecuenciaNoEsCuadrada() {
        String[] dna = {
                "ATGCGA",
                "CAGT",  // Diferente longitud
                "TTATGT"
        };
        assertThrows(InvalidDnaException.class, () -> {
            service.isMutant(dna);
        }, "Debe lanzar excepción por matriz no cuadrada");
    }

    @Test
    public void testSecuenciaConMinusculas() {
        String[] dna = {
                "atgc",
                "cagt",
                "ttat",
                "agac"
        };
        assertThrows(InvalidDnaException.class, () -> {
            service.isMutant(dna);
        }, "Debe lanzar excepción por letras minúsculas");
    }

    // ===== TESTS DE CASOS BORDE =====

    @Test
    public void testMatrizMinima_4x4_Mutante() {
        String[] dna = {
                "AAAA",
                "CCCC",
                "TTTT",
                "GGGG"
        };
        assertTrue(service.isMutant(dna), "Debe detectar mutante en matriz mínima 4x4");
    }

    @Test
    public void testMatrizMinima_4x4_Humano() {
        String[] dna = {
                "ATGC",
                "CAGT",
                "TGCA",
                "GCAT"
        };
        assertFalse(service.isMutant(dna), "Debe detectar humano en matriz 4x4");
    }

    @Test
    public void testDiagonalCompleta() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCTCTA",
                "TCACTG"
        };
        assertTrue(service.isMutant(dna), "Debe detectar mutante con diagonal y otra secuencia");
    }
}