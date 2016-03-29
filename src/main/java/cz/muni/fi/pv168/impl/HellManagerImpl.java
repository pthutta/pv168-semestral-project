package cz.muni.fi.pv168.impl;

import cz.muni.fi.pv168.Cauldron;
import cz.muni.fi.pv168.HellManager;
import cz.muni.fi.pv168.Sinner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Hutta
 * @version 1.0  10.3.2016
 */
public class HellManagerImpl implements HellManager {

    private final DataSource dataSource;

    public HellManagerImpl(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("Data source cannot be null.");
        }
        this.dataSource = dataSource;
    }

    public List<Sinner> findSinnersInCauldron(Cauldron cauldron) {
        if (cauldron == null) {
            throw new IllegalArgumentException("Cauldron is null");
        }
        if (cauldron.getId() == null) {
            throw new IllegalArgumentException("Cauldron id is null");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(
                     "SELECT id, firstName, lastName, sin, releaseDate, signedContract " +
                             "from sinner WHERE cauldronId = ?")) {

            st.setLong(1, cauldron.getId());

            try (ResultSet rs = st.executeQuery()) {
                List<Sinner> sinners = new ArrayList<>();

                while (rs.next()) {
                    sinners.add(resultSetToSinner(rs));
                }

                return sinners;
            }

        } catch (SQLException e) {
            throw new ServiceFailureException("Error retrieving data from database", e);
        }
    }

    public Cauldron findCauldronWithSinner(Sinner sinner) {
        if (sinner == null) {
            throw new IllegalArgumentException("Sinner is null");
        }
        if (sinner.getId() == null) {
            throw new IllegalArgumentException("Sinner id is null");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement("SELECT cauldron.id, cauldron.capacity, " +
                     "cauldron.waterTemperature, cauldron.hellFloor FROM sinner LEFT JOIN cauldron ON " +
                     "sinner.cauldronId = cauldron.id AND sinner.id = ?")) {
            st.setLong(1, sinner.getId());

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Cauldron cauldron = resultSetToCauldron(rs);

                    if (rs.next()) {
                        throw new ServiceFailureException("Error, multiple cauldrons with the same sinner found "
                                + "(source sinner: " + sinner + ", found " + cauldron + " and " + resultSetToCauldron(rs));
                    }
                    return cauldron;

                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            throw new ServiceFailureException("Error retrieving cauldron with sinner", e);
        }
    }

    public void boilSinnerInCauldron(Sinner sinner, Cauldron cauldron) {
        if (sinner == null) {
            throw new IllegalArgumentException("Sinner is null");
        }
        if (sinner.getId() == null) {
            throw new IllegalArgumentException("Sinner id is null");
        }
        if (cauldron == null) {
            throw new IllegalArgumentException("Cauldron is null");
        }
        if (cauldron.getId() == null) {
            throw new IllegalArgumentException("Cauldron id is null");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement("UPDATE sinner SET cauldronId = ? WHERE id = ?")) {
            st.setLong(1, cauldron.getId());
            st.setLong(2, sinner.getId());

            int changed = st.executeUpdate();
            if (changed != 1) {
                throw new ServiceFailureException("Updated invalid number of rows: " + changed);
            }

        } catch (SQLException e) {
            throw new ServiceFailureException("Error updating data in database", e);
        }
    }

    public void releaseSinnerFromCauldron(Sinner sinner) {
        if (sinner == null) {
            throw new IllegalArgumentException("Sinner is null");
        }
        if (sinner.getId() == null) {
            throw new IllegalArgumentException("Sinner id is null");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement("UPDATE sinner SET cauldronId = NULL WHERE id = ?")) {
            st.setLong(1, sinner.getId());

            int changed = st.executeUpdate();
            if (changed != 1) {
                throw new ServiceFailureException("Updated invalid number of rows: " + changed);
            }

        } catch (SQLException e) {
            throw new ServiceFailureException("Error updating data in database", e);
        }
    }

    private Sinner resultSetToSinner(ResultSet rs) throws SQLException {
        Sinner sinner = new Sinner();

        sinner.setId(rs.getLong("id"));
        sinner.setFirstName(rs.getString("firstName"));
        sinner.setLastName(rs.getString("lastName"));
        sinner.setSin(rs.getString("sin"));
        sinner.setReleaseDate(rs.getDate("releaseDate").toLocalDate());
        sinner.setSignedContractWithDevil(rs.getBoolean("signedContract"));

        return sinner;
    }

    private Cauldron resultSetToCauldron(ResultSet rs) throws SQLException {
        Cauldron cauldron = new Cauldron();

        cauldron.setId(rs.getLong("id"));
        cauldron.setCapacity(rs.getInt("capacity"));
        cauldron.setHellFloor(rs.getInt("hellFloor"));
        cauldron.setWaterTemperature(rs.getInt("waterTemperature"));

        return cauldron;
    }
}
