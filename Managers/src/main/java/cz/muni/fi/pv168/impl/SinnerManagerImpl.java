package cz.muni.fi.pv168.impl;

import cz.muni.fi.pv168.Sinner;
import cz.muni.fi.pv168.SinnerManager;
import cz.muni.fi.pv168.exceptions.EntityNotFoundException;
import cz.muni.fi.pv168.exceptions.ServiceFailureException;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of sinner manager. Keeps data in memory.
 *
 * @author Peter Hutta
 * @version 1.0  10.3.2016
 */
public class SinnerManagerImpl implements SinnerManager {

    private DataSource dataSource;
    private final Clock clock;

    public SinnerManagerImpl(Clock clock) {
        this.clock = clock;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createSinner(Sinner sinner) {
        checkDataSource();
        validateSinner(sinner);
        if (sinner.getId() != null) {
            throw new IllegalArgumentException("Sinner id is already set");
        }

        try (   Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "INSERT INTO SINNER (firstName, lastName, releaseDate, sin, signedContract) VALUES (?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {

            st.setString(1, sinner.getFirstName());
            st.setString(2, sinner.getLastName());
            if (sinner.getReleaseDate() == null) {
                st.setNull(3, Types.DATE);
            }
            else {
                st.setDate(3, toSqlDate(sinner.getReleaseDate()));
            }
            st.setString(4, sinner.getSin());
            st.setBoolean(5, sinner.isSignedContractWithDevil());
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: Different number of rows ("
                        + addedRows + ") inserted when trying to insert sinner " + sinner);
            }

            ResultSet keyRS = st.getGeneratedKeys();
            sinner.setId(getKey(keyRS, sinner));

        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inserting sinner " + sinner, ex);
        }
    }

    public void updateSinner(Sinner sinner) {
        checkDataSource();
        validateSinner(sinner);
        if (sinner.getId() == null) {
            throw new IllegalArgumentException("Sinner id is null");
        }
        try (   Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "UPDATE sinner SET firstName = ?, lastName = ?, releaseDate = ?, sin = ?, signedContract = ? WHERE id = ?")) {

            st.setString(1, sinner.getFirstName());
            st.setString(2, sinner.getLastName());
            if (sinner.getReleaseDate() == null) {
                st.setNull(3, Types.DATE);
            }
            else {
                st.setDate(3, toSqlDate(sinner.getReleaseDate()));
            }
            st.setString(4, sinner.getSin());
            st.setBoolean(5, sinner.isSignedContractWithDevil());
            st.setLong(6, sinner.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Sinner " + sinner + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid updated rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating sinner " + sinner, ex);
        }
    }

    public void deleteSinner(Sinner sinner) {
        checkDataSource();
        if (sinner == null) {
            throw new IllegalArgumentException("sinner is null");
        }
        if (sinner.getId() == null) {
            throw new IllegalArgumentException("sinner id is null");
        }
        try (   Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "DELETE FROM sinner WHERE id = ?")) {

            st.setLong(1, sinner.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Sinner " + sinner + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid deleted rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating cauldron " + sinner, ex);
        }
    }


    public Sinner findSinnerById(Long id) {
        checkDataSource();
        if (id == null){
            throw new IllegalArgumentException("Id cant be null");
        }
        try (   Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT id, firstName, lastName, releaseDate, sin, signedContract FROM sinner WHERE id = ?")) {

            st.setLong(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                Sinner sinner = resultSetToSinner(rs);

                if (rs.next()) {
                    throw new ServiceFailureException(
                            "Internal error: More entities with the same id found "
                                    + "(source id: " + id + ", found " + sinner + " and " + resultSetToSinner(rs));
                }

                return sinner;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving sinner with id " + id, ex);
        }
    }

    public List<Sinner> findAllSinners() {
        checkDataSource();
        try (   Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT id, firstName, lastName, releaseDate, sin, signedContract FROM sinner")) {

            ResultSet rs = st.executeQuery();

            List<Sinner> result = new ArrayList<>();
            while (rs.next()) {
                result.add(resultSetToSinner(rs));
            }
            return result;

        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving all sinners", ex);
        }
    }



    //subsidiary methods:

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    private void validateSinner(Sinner sinner) throws IllegalArgumentException {
        if (sinner == null) {
            throw new IllegalArgumentException("Cauldron is null");
        }
        if (sinner.getFirstName() == null) {
            throw new IllegalArgumentException("First name is null");
        }
        if (sinner.getLastName() == null) {
            throw new IllegalArgumentException("Last name is null");
        }

        LocalDate today = (clock == null) ? LocalDate.now() : LocalDate.now(clock);
        if (sinner.getReleaseDate() != null && sinner.getReleaseDate().isBefore(today)) {
            throw new IllegalArgumentException("Release date cannot be in the past");
        }
        if (sinner.getReleaseDate() == null && !sinner.isSignedContractWithDevil()) {
            throw new IllegalArgumentException("Sinner has to have either a release date or signed contract with a devil.");
        }
        if (sinner.getReleaseDate() != null && sinner.isSignedContractWithDevil()) {
            throw new IllegalArgumentException("Sinner can have only one, a release date or a signed contract with a devil.");
        }
        if (sinner.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("First name is empty");
        }
        if (sinner.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Last name is empty");
        }
    }

    private Long getKey(ResultSet keyRS, Sinner sinner) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retrieving failed when trying to insert sinner " + sinner
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retrieving failed when trying to insert sinner " + sinner
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retrieving failed when trying to insert sinner " + sinner
                    + " - no key found");
        }
    }
    
    private Sinner resultSetToSinner(ResultSet rs) throws SQLException {
        Sinner sinner = new Sinner();
        sinner.setId(rs.getLong("id"));
        sinner.setFirstName(rs.getString("firstName"));
        sinner.setLastName(rs.getString("lastName"));
        sinner.setReleaseDate(toLocalDate(rs.getDate("releaseDate")));
        sinner.setSin(rs.getString("sin"));
        sinner.setSignedContractWithDevil(rs.getBoolean("signedContract"));
        return sinner;
    }

    private static Date toSqlDate(LocalDate localDate) {
        return localDate == null ? null : Date.valueOf(localDate);
    }

    private static LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }

}
