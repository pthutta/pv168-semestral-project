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
import java.time.*;
import java.util.Comparator;

import static java.time.Month.FEBRUARY;
import static java.time.Month.MARCH;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;

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
        assert(false);
    }

    @Test
    public void findCauldronWithSinner() throws Exception {
        assert(false);
    }

    @Test
    public void boilSinnerInCauldron() throws Exception {
        assert(false);
    }

    @Test
    public void releaseSinnerFromCauldron() throws Exception {
        assert(false);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test                                                              //TODO - VYMAZ MA A DOKONCI TO
    public void boilSinnerInCauldronFullCauldron() throws Exception {  //presunul som to sem, lebo musia byt v databaze
        Sinner sinner1 = newSinner("Joe", "Doe", "murder", null, true); //a nechcelo sa mi to konfigurovat kvoli jednemu testu
        sinner1.setId(1L);
        Sinner sinner2 = newSinner("Jack", "Black", "murder", null, true);
        sinner2.setId(2L);
        Cauldron cauldron = newCauldron(1, 1, 32);
        cauldron.setId(1L);

        manager.boilSinnerInCauldron(sinner1, cauldron);

        thrown.expect(IllegalArgumentException.class);
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
