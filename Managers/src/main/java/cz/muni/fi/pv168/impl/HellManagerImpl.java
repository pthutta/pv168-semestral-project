package cz.muni.fi.pv168.impl;

import cz.muni.fi.pv168.Cauldron;
import cz.muni.fi.pv168.HellManager;
import cz.muni.fi.pv168.Sinner;
import cz.muni.fi.pv168.common.DBUtils;
import cz.muni.fi.pv168.exceptions.IllegalEntityException;
import cz.muni.fi.pv168.exceptions.ServiceFailureException;

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

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Sinner> findSinnersInCauldron(Cauldron cauldron) {
        checkDataSource();
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
        checkDataSource();
        if (sinner == null) {
            throw new IllegalArgumentException("Sinner is null");
        }
        if (sinner.getId() == null) {
            throw new IllegalArgumentException("Sinner id is null");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement("SELECT cauldron.id, cauldron.capacity, " +
                     "cauldron.waterTemperature, cauldron.hellFloor FROM cauldron JOIN sinner ON " +
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
        checkDataSource();
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

            checkIfCauldronHasSpace(connection, cauldron);

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
        checkDataSource();
        if (sinner == null) {
            throw new IllegalArgumentException("Sinner is null");
        }
        if (sinner.getId() == null) {
            throw new IllegalArgumentException("Sinner id is null");
        }
        if (sinner.isSignedContractWithDevil()) {
            throw new IllegalArgumentException("Contract was signed, cannot release sinner from cauldron");
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

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    private static void checkIfCauldronHasSpace(Connection conn, Cauldron cauldron) throws SQLException {
        PreparedStatement checkSt = null;
        try {
            checkSt = conn.prepareStatement(
                    "SELECT capacity, COUNT(sinner.id) as sinnerCount " +
                            "FROM cauldron LEFT JOIN sinner ON cauldron.id = sinner.cauldronId " +
                            "WHERE cauldron.id = ? " +
                            "GROUP BY cauldron.id, capacity");
            checkSt.setLong(1, cauldron.getId());
            ResultSet rs = checkSt.executeQuery();
            if (rs.next()) {
                if (rs.getInt("capacity") <= rs.getInt("sinnerCount")) {
                    throw new IllegalEntityException("Cauldron " + cauldron + " is already full");
                }
            } else {
                throw new IllegalEntityException("Cauldron " + cauldron + " does not exist in the database");
            }
        }
        finally {
            DBUtils.closeQuietly(null, checkSt);
        }
    }

    private Sinner resultSetToSinner(ResultSet rs) throws SQLException {
        Sinner sinner = new Sinner();

        sinner.setId(rs.getLong("id"));
        sinner.setFirstName(rs.getString("firstName"));
        sinner.setLastName(rs.getString("lastName"));
        sinner.setSin(rs.getString("sin"));
        if (sinner.getReleaseDate() != null) {
            sinner.setReleaseDate(rs.getDate("releaseDate").toLocalDate());
        }
        else sinner.setReleaseDate(null);
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
