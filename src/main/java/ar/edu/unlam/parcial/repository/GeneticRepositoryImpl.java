package ar.edu.unlam.parcial.repository;

import ar.edu.unlam.parcial.model.GeneticSequence;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del repositorio usando H2 con deduplicación por hash.
 * @author Cecilia Calvo - Legajo 46332
 */
public class GeneticRepositoryImpl implements GeneticRepository {
    private final Connection connection;

    public GeneticRepositoryImpl() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:h2:mem:parcialdb;DB_CLOSE_DELAY=-1", "sa", "");
        createTable();
    }

    private void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS genetic_sequences (" +
                "id IDENTITY PRIMARY KEY, " +
                "sequence VARCHAR(1000), " +
                "dna_hash VARCHAR(64) UNIQUE, " +  // UNIQUE constraint para evitar duplicados
                "is_mutant BOOLEAN)";
        connection.createStatement().execute(sql);
    }

    @Override
    public void save(GeneticSequence sequence) {
        try {
            // Verificar si ya existe por hash
            if (existsByHash(sequence.getDnaHash())) {
                System.out.println("ADN duplicado detectado, no se guarda: " + sequence.getDnaHash());
                return; // No guardar duplicados
            }

            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO genetic_sequences (sequence, dna_hash, is_mutant) VALUES (?, ?, ?)");
            stmt.setString(1, sequence.getSequence());
            stmt.setString(2, sequence.getDnaHash());
            stmt.setBoolean(3, sequence.isMutant());
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Si hay error de UNIQUE constraint, simplemente lo ignoramos
            if (!e.getMessage().contains("Unique index")) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Verifica si ya existe un ADN con el mismo hash.
     */
    private boolean existsByHash(String hash) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT COUNT(*) FROM genetic_sequences WHERE dna_hash = ?");
        stmt.setString(1, hash);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }

    @Override
    public List<GeneticSequence> findAll() {
        List<GeneticSequence> sequences = new ArrayList<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM genetic_sequences");
            while (rs.next()) {
                sequences.add(new GeneticSequence(
                        rs.getLong("id"),
                        rs.getString("sequence"),
                        rs.getString("dna_hash"),
                        rs.getBoolean("is_mutant")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sequences;
    }

    @Override
    public long countMutants() {
        try {
            ResultSet rs = connection.createStatement().executeQuery(
                    "SELECT COUNT(*) FROM genetic_sequences WHERE is_mutant = TRUE");
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long countHumans() {
        try {
            ResultSet rs = connection.createStatement().executeQuery(
                    "SELECT COUNT(*) FROM genetic_sequences WHERE is_mutant = FALSE");
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
