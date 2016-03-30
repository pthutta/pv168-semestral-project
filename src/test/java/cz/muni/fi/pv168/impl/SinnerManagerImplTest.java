package cz.muni.fi.pv168.impl;

import cz.muni.fi.pv168.Sinner;

import java.time.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import cz.muni.fi.pv168.common.DBUtils;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.sql.DataSource;

import static java.time.Month.FEBRUARY;
import static java.time.Month.MARCH;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;


/**
 * @author Peter Hutta
 * @version 1.0  15.3.2016
 */
public class SinnerManagerImplTest {

    private DataSource ds;
    private SinnerManagerImpl manager;

    private final static ZonedDateTime NOW
            = LocalDateTime.of(2016, MARCH, 29, 14, 00).atZone(ZoneId.of("UTC"));

    private static DataSource prepareDataSource() {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:sinnerMgr-test");
        ds.setCreateDatabase("create");
        return ds;
    }

    private static Clock prepareClockMock(ZonedDateTime now) {
        return Clock.fixed(now.toInstant(), now.getZone());
    }

    @Before
    public void setUp() throws Exception {
        ds = null;
        ds = prepareDataSource();
        ClassLoader classLoader = getClass().getClassLoader();
        DBUtils.executeSqlScript(ds, classLoader.getResource("scripts/createTables.sql"));
        manager = new SinnerManagerImpl(prepareClockMock(NOW));
        manager.setDataSource(ds);
    }

    @After
    public void tearDown() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        DBUtils.executeSqlScript(ds, classLoader.getResource("scripts/dropTables.sql"));
    }

    @Test
    public void testCreateSinner() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "killed five babies", NOW.toLocalDate(), false);
        manager.createSinner(sinner);

        Long sinnerId = sinner.getId();
        assertThat("Saved sinner has null id", sinnerId, is(not(equalTo(null))));

        Sinner result = manager.findSinnerById(sinnerId);
        assertThat("Saved sinner differs from the loaded one", sinner, is(equalTo(result)));
        assertThat("Loaded sinner is the same instance", sinner, is(not(sameInstance(result))));
    }

    @Test
    public void testUpdateSinner() throws Exception {
        Sinner s1 = newSinner("Joe", "Doe", "murder", null, true);
        Sinner s2 = newSinner("Jack", "Black", "murder", null, true);

        manager.createSinner(s1);
        manager.createSinner(s2);
        Long sinnerId = s1.getId();

        s1.setFirstName("Hodor");
        manager.updateSinner(s1);
        s1 = manager.findSinnerById(sinnerId);
        assertThat("First name wasn't changed", s1.getFirstName(), is(equalTo("Hodor")));
        assertThat("Last name was changed", s1.getLastName(), is(equalTo("Doe")));
        assertThat("Sin was changed", s1.getSin(), is(equalTo("murder")));
        assertThat("Release date was changed", s1.getReleaseDate(), is(equalTo(null)));
        assertTrue("Signed contract was changed", s1.isSignedContractWithDevil());

        s1.setLastName("The First");
        manager.updateSinner(s1);
        s1 = manager.findSinnerById(sinnerId);
        assertThat("First name was changed", s1.getFirstName(), is(equalTo("Hodor")));
        assertThat("Last name wasn't changed", s1.getLastName(), is(equalTo("The First")));
        assertThat("Sin was changed", s1.getSin(), is(equalTo("murder")));
        assertThat("Release date was changed", s1.getReleaseDate(), is(equalTo(null)));
        assertTrue("Signed contract was changed", s1.isSignedContractWithDevil());

        s1.setSin("stupidity");
        manager.updateSinner(s1);
        s1 = manager.findSinnerById(sinnerId);
        assertThat("First name was changed", s1.getFirstName(), is(equalTo("Hodor")));
        assertThat("Last name was changed", s1.getLastName(), is(equalTo("The First")));
        assertThat("Sin wasn't changed", s1.getSin(), is(equalTo("stupidity")));
        assertThat("Release date was changed", s1.getReleaseDate(), is(equalTo(null)));
        assertTrue("Signed contract was changed", s1.isSignedContractWithDevil());

        s1.setSignedContractWithDevil(false);
        s1.setReleaseDate(LocalDate.of(2020, 8, 11));
        manager.updateSinner(s1);
        s1 = manager.findSinnerById(sinnerId);
        assertThat("First name was changed", s1.getFirstName(), is(equalTo("Hodor")));
        assertThat("Last name was changed", s1.getLastName(), is(equalTo("The First")));
        assertThat("Sin was changed", s1.getSin(), is(equalTo("stupidity")));
        assertThat("Release date wasn't changed", s1.getReleaseDate(), is(equalTo(LocalDate.of(2020, 8, 11))));
        assertFalse("Signed contract wasn't changed", s1.isSignedContractWithDevil());

        assertThat("Other record was changed", s2, is(equalTo(manager.findSinnerById(s2.getId()))));
    }

    @Test
    public void testDeleteSinner() throws Exception {
        Sinner s1 = newSinner("Joe", "Doe", "murder", null, true);
        Sinner s2 = newSinner("Jack", "Black", "murder", null, true);

        manager.createSinner(s1);
        manager.createSinner(s2);

        assertThat(manager.findSinnerById(s1.getId()), is(not(equalTo(null))));
        assertThat(manager.findSinnerById(s2.getId()), is(not(equalTo(null))));

        manager.deleteSinner(s1);

        assertThat(manager.findSinnerById(s1.getId()), is(equalTo(null)));
        assertThat(manager.findSinnerById(s2.getId()), is(not(equalTo(null))));
    }

    @Test
    public void testFindSinnerById() throws Exception {
        Sinner s1 = newSinner("Joe", "First", "murder", null, true);
        Sinner s2 = newSinner("Joe", "Second", "murder", null, true);

        manager.createSinner(s1);
        manager.createSinner(s2);

        assertEquals(s1,manager.findSinnerById(s1.getId()));
        assertEquals(s2,manager.findSinnerById(s2.getId()));
    }

    @Test
    public void testFindAllSinners() throws Exception {
        assertTrue("Database should be empty.", manager.findAllSinners().isEmpty());

        Sinner s1 = newSinner("Joe", "First", "murder", null, true);
        Sinner s2 = newSinner("Joe", "Second", "murder", null, true);

        manager.createSinner(s1);
        manager.createSinner(s2);

        List<Sinner> actual = manager.findAllSinners();
        List<Sinner> expected = Arrays.asList(s1, s2);

        actual.sort(sinnerComparator);
        expected.sort(sinnerComparator);

        assertEquals("Saved and retrieved sinners differ.", expected, actual);
    }

    private static Sinner newSinner(String firstName, String lastName, String sin, LocalDate releaseDate, boolean contract) {
        Sinner sinner = new Sinner();

        sinner.setFirstName(firstName);
        sinner.setLastName(lastName);
        sinner.setSin(sin);
        sinner.setReleaseDate(releaseDate);
        sinner.setSignedContractWithDevil(contract);

        return sinner;
    }

    private static Comparator<Sinner> sinnerComparator = (o1, o2) -> o1.getId().compareTo(o2.getId());
}