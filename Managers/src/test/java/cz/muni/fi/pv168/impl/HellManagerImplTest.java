package cz.muni.fi.pv168.impl;

import cz.muni.fi.pv168.Cauldron;
import cz.muni.fi.pv168.Sinner;
import cz.muni.fi.pv168.common.DBUtils;
import cz.muni.fi.pv168.exceptions.IllegalEntityException;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.sql.DataSource;
import java.time.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.time.Month.MARCH;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * @author Peter Hutta
 * @version 1.0  30.3.2016
 */
public class HellManagerImplTest {

    private DataSource ds;
    private HellManagerImpl manager;
    private SinnerManagerImpl sinnerManager;
    private CauldronManagerImpl cauldronManager;

    private final static ZonedDateTime NOW
            = LocalDateTime.of(2016, MARCH, 29, 14, 00).atZone(ZoneId.of("UTC"));

    private static DataSource prepareDataSource() {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:hellMgr-test");
        ds.setCreateDatabase("create");
        return ds;
    }

    private static Clock prepareClockMock(ZonedDateTime now) {
        return Clock.fixed(now.toInstant(), now.getZone());
    }

    @Before
    public void setUp() throws Exception {
        ds = prepareDataSource();
        ClassLoader classLoader = getClass().getClassLoader();
        DBUtils.executeSqlScript (ds, classLoader.getResource("scripts/createTables.sql"));

        manager = new HellManagerImpl();
        manager.setDataSource(ds);

        sinnerManager = new SinnerManagerImpl(prepareClockMock(NOW));
        sinnerManager.setDataSource(ds);

        cauldronManager = new CauldronManagerImpl();
        cauldronManager.setDataSource(ds);
    }

    @After
    public void tearDown() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        DBUtils.executeSqlScript(ds, classLoader.getResource("scripts/dropTables.sql"));
    }

    @Test
    public void findSinnersInCauldron() throws Exception {
        Cauldron cauldron = newCauldron(1, 10, 32);
        Sinner s1 = newSinner("Jack", "Ripper", "serial killer", null, true);
        Sinner s2 = newSinner("Francisco", "Guerrero", "serial killer", null, true);
        sinnerManager.createSinner(s1);
        sinnerManager.createSinner(s2);
        cauldronManager.createCauldron(cauldron);

        manager.boilSinnerInCauldron(s1, cauldron);
        manager.boilSinnerInCauldron(s2, cauldron);

        List<Sinner> actual = manager.findSinnersInCauldron(cauldron);
        List<Sinner> expected = Arrays.asList(s1, s2);

        actual.sort(sinnerComparator);
        expected.sort(sinnerComparator);

        assertEquals("Saved and retrieved sinners differ.", expected, actual);
    }

    @Test
    public void findCauldronWithSinner() throws Exception {
        Cauldron c1 = newCauldron(9, 1, 666);
        Sinner s1 = newSinner("John", "Red", "serial serial killer", null, true);
        sinnerManager.createSinner(s1);
        cauldronManager.createCauldron(c1);

        manager.boilSinnerInCauldron(s1, c1);
        Cauldron c2 = manager.findCauldronWithSinner(s1);

        assertThat("Sinner was not found in the right cauldron.", c1, is(equalTo(c2)));
    }

    @Test
    public void boilSinnerInCauldronDependantOnFindCauldronWithSinner() throws Exception {
        Cauldron c1 = newCauldron(5, 3, 999);
        Sinner s1 = newSinner("Dexter", "Morgan", "serial serial killer", NOW.toLocalDate(), false);
        sinnerManager.createSinner(s1);
        cauldronManager.createCauldron(c1);

        manager.boilSinnerInCauldron(s1, c1);

        Long sinnerId = s1.getId();
        assertThat("Saved sinner has null id", sinnerId, is(not(equalTo(null))));

        Cauldron c2 = manager.findCauldronWithSinner(s1);

        assertEquals("Cauldron to which sinner was saved and the one where he was find differs.", c1, c2);
    }

    @Test
    public void boilSinnerInCauldronDependantOnFindSinnersInCauldron() throws Exception {
        Cauldron cauldron = newCauldron(5, 3, 999);
        Sinner s1 = newSinner("Dexter", "Morgan", "serial serial killer", null, true);
        sinnerManager.createSinner(s1);
        cauldronManager.createCauldron(cauldron);

        manager.boilSinnerInCauldron(s1, cauldron);

        Long sinnerId = s1.getId();
        assertThat("Saved sinner has null id", sinnerId, is(not(equalTo(null))));

        List<Sinner> actual = manager.findSinnersInCauldron(cauldron);

        actual.sort(sinnerComparator);
        assertThat("Saved sinner differs from the loaded one", s1, is(equalTo(actual.get(0))));
        assertThat("Loaded sinner is the same instance", s1, is(not(sameInstance(actual.get(0)))));

        assertEquals("Saved and retrieved sinners differ.", s1, actual.get(0));
    }

    @Test
    public void releaseSinnerFromCauldron() throws Exception {
        Sinner sinner = newSinner("Dexter", "Morgan", "serial serial killer", NOW.toLocalDate(), false);
        Cauldron c1 = newCauldron(2, 1, 36);
        sinnerManager.createSinner(sinner);
        cauldronManager.createCauldron(c1);

        manager.boilSinnerInCauldron(sinner, c1);
        Cauldron c2 = manager.findCauldronWithSinner(sinner);
        manager.releaseSinnerFromCauldron(sinner);
        Cauldron c3 = manager.findCauldronWithSinner(sinner);

        assertThat("Sinner was found in a cauldron after release." , c3, is(equalTo(null)));
        assertThat("Cauldron where was sinner boiled is the same instance " +
                "as the one where he was find after release method.", c2, is(not(sameInstance(c3))));
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void boilSinnerInCauldronFullCauldron() throws Exception {
        Sinner sinner1 = newSinner("Joe", "Doe", "murder", null, true);
        Sinner sinner2 = newSinner("Jack", "Black", "murder", null, true);
        Cauldron cauldron = newCauldron(1, 1, 32);

        sinnerManager.createSinner(sinner1);
        sinnerManager.createSinner(sinner2);
        cauldronManager.createCauldron(cauldron);

        manager.boilSinnerInCauldron(sinner1, cauldron);

        thrown.expect(IllegalEntityException.class);
        thrown.expectMessage(allOf(containsString("Cauldron"), containsString("full")));
        manager.boilSinnerInCauldron(sinner2, cauldron);
    }

    private static Cauldron newCauldron(int floor, int capacity, int temperature) {
        Cauldron cauldron = new Cauldron();
        cauldron.setWaterTemperature(temperature);
        cauldron.setCapacity(capacity);
        cauldron.setHellFloor(floor);
        return cauldron;
    }

    private static Sinner newSinner(String firstName, String lastName, String sin, LocalDate releaseDate, boolean contract) {
        Sinner sinner = new Sinner();

        sinner.setFirstName(firstName);
        sinner.setLastName(lastName);
        sinner.setSignedContractWithDevil(contract);
        sinner.setSin(sin);
        sinner.setReleaseDate(releaseDate);

        return sinner;
    }

    private static Comparator<Sinner> sinnerComparator = (o1, o2) -> o1.getId().compareTo(o2.getId());
}
