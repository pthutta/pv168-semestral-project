package cz.muni.fi.pv168.impl;

import cz.muni.fi.pv168.Cauldron;
import cz.muni.fi.pv168.CauldronManager;
import cz.muni.fi.pv168.exceptions.EntityNotFoundException;
import cz.muni.fi.pv168.exceptions.ServiceFailureException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Hutta
 * @version 1.0  10.3.2016
 */
public class CauldronManagerImpl implements CauldronManager {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createCauldron(Cauldron cauldron) {
        checkDataSource();
        validate(cauldron);
        if (cauldron.getId() != null) {
            throw new IllegalArgumentException("cauldron id is already set");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(
                     "INSERT INTO CAULDRON (capacity,waterTemperature,hellFloor) VALUES (?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            st.setInt(1, cauldron.getCapacity());
            st.setInt(2, cauldron.getWaterTemperature());
            st.setInt(3, cauldron.getHellFloor());
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: Different number of rows ("
                        + addedRows + ") inserted when trying to insert cauldron " + cauldron);
            }

            ResultSet keyRS = st.getGeneratedKeys();
            cauldron.setId(getKey(keyRS, cauldron));

        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inserting cauldron " + cauldron, ex);
        }

    }

    public void updateCauldron(Cauldron cauldron) {
        checkDataSource();
        validate(cauldron);
        if (cauldron.getId() == null) {
            throw new IllegalArgumentException("Cauldron id is null");
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(
                     "UPDATE CAULDRON SET capacity = ?, waterTemperature = ?, hellFloor = ? WHERE id = ?")) {

            st.setInt(1, cauldron.getCapacity());
            st.setInt(2, cauldron.getWaterTemperature());
            st.setInt(3, cauldron.getHellFloor());
            st.setLong(4, cauldron.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Cauldron " + cauldron + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid updated rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when updating cauldron " + cauldron, ex);
        }
    }

    public void deleteCauldron(Cauldron cauldron) {
        checkDataSource();
        if (cauldron == null) {
            throw new IllegalArgumentException("cauldron is null");
        }
        if (cauldron.getId() == null) {
            throw new IllegalArgumentException("cauldron id is null");
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(
                     "DELETE FROM CAULDRON WHERE id = ?")) {

            st.setLong(1, cauldron.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Cauldron " + cauldron + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid deleted rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating cauldron " + cauldron, ex);
        }
    }

    public Cauldron findCauldronById(Long id) {
        checkDataSource();
        if (id == null) {
            throw new IllegalArgumentException("Id is null");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(
                     "SELECT id,capacity,waterTemperature,hellFloor FROM cauldron WHERE id = ?")) {

            st.setLong(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Cauldron cauldron = resultSetToCauldron(rs);

                    if (rs.next()) {
                        throw new ServiceFailureException("Internal error: More entities with the same id found "
                                + "(source id: " + id + ", found " + cauldron + " and " + resultSetToCauldron(rs));
                    }
                    return cauldron;

                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when retrieving grave with id " + id, ex);
        }
    }

    public List<Cauldron> findAllCauldrons() {
        checkDataSource();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(
                     "SELECT id,capacity, waterTemperature, hellFloor FROM cauldron")) {

            try (ResultSet rs = st.executeQuery()) {
                List<Cauldron> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(resultSetToCauldron(rs));
                }
                return result;
            }

        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving all cauldron", ex);
        }
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    private void validate(Cauldron cauldron) throws IllegalArgumentException {
        if (cauldron == null) {
            throw new IllegalArgumentException("cauldron is null");
        }
        if (cauldron.getCapacity() < 1) {
            throw new IllegalArgumentException("cauldron capacity is lower than 1");
        }
        if (cauldron.getHellFloor() < 0) {
            throw new IllegalArgumentException("cauldron hell floor is lower than 0");
        }
    }

    private Long getKey(ResultSet keyRS, Cauldron cauldron) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert cauldron " + cauldron
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert cauldron " + cauldron
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert cauldron " + cauldron
                    + " - no key found");
        }
    }

    private Cauldron resultSetToCauldron(ResultSet rs) throws SQLException {
        Cauldron cauldron = new Cauldron();
        cauldron.setId(rs.getLong("id"));
        cauldron.setCapacity(rs.getInt("capacity"));
        cauldron.setWaterTemperature(rs.getInt("waterTemperature"));
        cauldron.setHellFloor(rs.getInt("hellFloor"));
        return cauldron;
    }
}
