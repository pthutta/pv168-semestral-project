package cz.muni.fi.pv168.impl;

import cz.muni.fi.pv168.Cauldron;
import cz.muni.fi.pv168.HellManager;
import cz.muni.fi.pv168.Sinner;
import cz.muni.fi.pv168.common.DBUtils;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.sql.DataSource;
import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;

/**
 * @author Peter Hutta
 * @version 1.0  30.3.2016
 */
public class HellManagerImplTestWithWrongValues {

    private DataSource ds;
    private HellManagerImpl manager;

    private static DataSource prepareDataSource() {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:hellMgrWrong-test");
        ds.setCreateDatabase("create");
        return ds;
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        ds = prepareDataSource();
        ClassLoader classLoader = getClass().getClassLoader();
        DBUtils.executeSqlScript (ds, classLoader.getResource("scripts/createTables.sql"));
        manager = new HellManagerImpl();
        manager.setDataSource(ds);
    }

    @After
    public void tearDown() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        DBUtils.executeSqlScript(ds, classLoader.getResource("scripts/dropTables.sql"));
    }

    @Test
    public void findSinnersInCauldronNullCauldron() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("null"));
        manager.findSinnersInCauldron(null);
    }

    @Test
    public void findSinnersInCauldronNullId() throws Exception {
        Cauldron cauldron = newCauldron(1, 10, 32);
        cauldron.setId(null);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("id"), containsString("null")));
        manager.findSinnersInCauldron(cauldron);
    }

    @Test
    public void findCauldronWithSinnerNullSinner() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("null"));
        manager.findCauldronWithSinner(null);
    }

    @Test
    public void findCauldronWithSinnerNullId() throws Exception {
        Sinner sinner = newSinner("Joe", "Doe", "murder", null, true);
        sinner.setId(null);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("id"), containsString("null")));
        manager.findCauldronWithSinner(sinner);
    }

    @Test
    public void boilSinnerInCauldronNullSinner() throws Exception {
        Cauldron cauldron = newCauldron(1, 10, 32);
        cauldron.setId(1L);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("Sinner"), containsString("null")));
        manager.boilSinnerInCauldron(null, cauldron);
    }

    @Test
    public void boilSinnerInCauldronNullSinnerId() throws Exception {
        Sinner sinner = newSinner("Joe", "Doe", "murder", null, true);
        sinner.setId(null);
        Cauldron cauldron = newCauldron(1, 10, 32);
        cauldron.setId(1L);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("Sinner"), containsString("id"), containsString("null")));
        manager.boilSinnerInCauldron(sinner, cauldron);
    }

    @Test
    public void boilSinnerInCauldronNullCauldron() throws Exception {
        Sinner sinner = newSinner("Joe", "Doe", "murder", null, true);
        sinner.setId(1L);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("Cauldron"), containsString("null")));
        manager.boilSinnerInCauldron(sinner, null);
    }

    @Test
    public void boilSinnerInCauldronNullCauldronId() throws Exception {
        Sinner sinner = newSinner("Joe", "Doe", "murder", null, true);
        sinner.setId(1L);
        Cauldron cauldron = newCauldron(1, 10, 32);
        cauldron.setId(null);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("Cauldron"), containsString("id"), containsString("null")));
        manager.boilSinnerInCauldron(sinner, cauldron);
    }

    @Test
    public void releaseSinnerFromCauldronNullSinner() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("null"));
        manager.releaseSinnerFromCauldron(null);
    }

    @Test
    public void releaseSinnerFromCauldronNullId() throws Exception {
        Sinner sinner = newSinner("Joe", "Doe", "murder", null, true);
        sinner.setId(null);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("id"), containsString("null")));
        manager.releaseSinnerFromCauldron(sinner);
    }

    @Test
    public void releaseSinnerFromCauldronTrueContract() throws Exception {
        Sinner sinner = newSinner("Joe", "Doe", "murder", null, true);
        sinner.setId(1L);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("Contract"), containsString("signed")));
        manager.releaseSinnerFromCauldron(sinner);
    }


    private static Cauldron newCauldron(int floor, int capacity, int temperature) {
        Cauldron cauldron = new Cauldron();
        cauldron.setHellFloor(floor);
        cauldron.setWaterTemperature(temperature);
        cauldron.setCapacity(capacity);
        return cauldron;
    }

    private static Sinner newSinner(String firstName, String lastName, String sin, LocalDate releaseDate, boolean contract) {
        Sinner sinner = new Sinner();

        sinner.setFirstName(firstName);
        sinner.setLastName(lastName);
        sinner.setReleaseDate(releaseDate);
        sinner.setSignedContractWithDevil(contract);
        sinner.setSin(sin);

        return sinner;
    }
}
