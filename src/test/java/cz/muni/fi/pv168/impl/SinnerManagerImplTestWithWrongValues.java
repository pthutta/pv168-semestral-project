package cz.muni.fi.pv168.impl;

import cz.muni.fi.pv168.Sinner;
import cz.muni.fi.pv168.SinnerManager;

import java.time.LocalDate;

import cz.muni.fi.pv168.common.DBUtils;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.sql.DataSource;

import static org.hamcrest.CoreMatchers.*;

/**
 * @author Peter Hutta
 * @version 1.0  16.3.2016
 */
public class SinnerManagerImplTestWithWrongValues {

    private DataSource ds;
    private SinnerManager manager;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static DataSource prepareDataSource() {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:sinnerMgrWrong-test");
        ds.setCreateDatabase("create");
        return ds;
    }

    @Before
    public void setUp() throws Exception {
        ds = prepareDataSource();
        ClassLoader classLoader = getClass().getClassLoader();
        DBUtils.executeSqlScript (ds, classLoader.getResource("scripts/createTables.sql"));
        manager = new SinnerManagerImpl(ds);
    }

    @After
    public void tearDown() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        DBUtils.executeSqlScript(ds, classLoader.getResource("scripts/dropTables.sql"));
    }

    @Test
    public void testCreateSinnerNullSinner() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("null"));
        manager.createSinner(null);
    }

    @Test
    public void testCreateSinnerSetId() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "killed five babies", null, true);
        sinner.setId(5L);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("id"));
        manager.createSinner(sinner);
    }

    @Test
    public void testCreateSinnerNullFirstName() throws Exception {
        Sinner sinner = newSinner(null, "Doe", "murder", null, true);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("first"), containsString("null")));
        manager.createSinner(sinner);
    }

    @Test
    public void testCreateSinnerEmptyFirstName() throws Exception {
        Sinner sinner = newSinner("", "Doe", "murder", null, true);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("first"), containsString("empty")));
        manager.createSinner(sinner);
    }

    @Test
    public void testCreateSinnerNullLastName() throws Exception {
        Sinner sinner = newSinner("John", null, "murder", null, true);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("last"), containsString("null")));
        manager.createSinner(sinner);
    }

    @Test
    public void testCreateSinnerEmptyLastName() throws Exception {
        Sinner sinner = newSinner("John", "", "murder", null, true);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("last"), containsString("empty")));
        manager.createSinner(sinner);
    }

    @Test
    public void testCreateSinnerNullDateFalseContract() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "murder", null, false);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("date"), containsString("signed")));
        manager.createSinner(sinner);
    }

    @Test
    public void testCreateSinnerSetDateTrueContract() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "murder", LocalDate.of(2125, 12, 27), true);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("date"), containsString("signed")));
        manager.createSinner(sinner);
    }

    @Test
    public void testUpdateSinnerNullSinner() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("null"));
        manager.updateSinner(null);
    }

    @Test
    public void testUpdateSinnerNullId() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "killed five babies", null, true);
        manager.createSinner(sinner);
        Long sinnerId = sinner.getId();

        sinner = manager.findSinnerById(sinnerId);
        sinner.setId(null);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("id"), containsString("null")));
        manager.updateSinner(sinner);
    }

    @Test
    public void testUpdateSinnerChangedId() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "killed five babies", null, true);
        manager.createSinner(sinner);
        Long sinnerId = sinner.getId();

        sinner.setId(sinnerId + 1);
        thrown.expect(EntityNotFoundException.class);
        thrown.expectMessage(containsString("id")); //tuto si treba rozmysliet ci a ako to budeme riesit
        manager.updateSinner(sinner);
    }

    @Test
    public void testUpdateSinnerNullFirstName() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "killed five babies", null, true);
        manager.createSinner(sinner);

        sinner.setFirstName(null);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("first"), containsString("null")));
        manager.updateSinner(sinner);
    }

    @Test
    public void testUpdateSinnerEmptyFirstName() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "killed five babies", null, true);
        manager.createSinner(sinner);

        sinner.setFirstName("");
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("first"), containsString("empty")));
        manager.updateSinner(sinner);
    }

    @Test
    public void testUpdateSinnerNullLastName() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "killed five babies", null, true);
        manager.createSinner(sinner);

        sinner.setLastName(null);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("last"), containsString("null")));
        manager.updateSinner(sinner);
    }

    @Test
    public void testUpdateSinnerEmptyLastName() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "killed five babies", null, true);
        manager.createSinner(sinner);

        sinner.setLastName("");
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("last"), containsString("empty")));
        manager.updateSinner(sinner);
    }

    @Test
    public void testUpdateSinnerSetDateTrueContract() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "killed five babies", null, true);
        manager.createSinner(sinner);

        sinner.setReleaseDate(LocalDate.of(2035, 4, 1));
        sinner.setSignedContractWithDevil(true);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("date"), containsString("signed")));
        manager.updateSinner(sinner);
    }

    @Test
    public void testUpdateSinnerNullDateFalseContract() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "killed five babies", null, true);
        manager.createSinner(sinner);

        sinner.setReleaseDate(null);
        sinner.setSignedContractWithDevil(false);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("date"), containsString("signed")));
        manager.updateSinner(sinner);
    }

    @Test
    public void testDeleteSinnerNullSinner() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("null"));
        manager.deleteSinner(null);
    }

    @Test
    public void testDeleteSinnerNullId() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "murder", null, true);

        sinner.setId(null);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("id"), containsString("null")));
        manager.deleteSinner(sinner);
    }

    @Test
    public void testDeleteSinnerSetId() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "murder", null, true);

        sinner.setId(2L);
        thrown.expect(IllegalArgumentException.class);
        manager.deleteSinner(sinner);
    }

    @Test
    public void testFindSinnerByIdNullId() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("null"));
        manager.findSinnerById(null);
    }

    private static Sinner newSinner(String firstName, String lastName, String sin, LocalDate releaseDate, boolean contract) {
        Sinner sinner = new Sinner();

        sinner.setSin(sin);
        sinner.setReleaseDate(releaseDate);
        sinner.setSignedContractWithDevil(contract);
        sinner.setFirstName(firstName);
        sinner.setLastName(lastName);

        return sinner;
    }
}
