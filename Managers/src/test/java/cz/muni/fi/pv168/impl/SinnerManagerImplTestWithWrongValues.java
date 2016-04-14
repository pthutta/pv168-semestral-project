package cz.muni.fi.pv168.impl;

import cz.muni.fi.pv168.Sinner;

import java.time.*;

import cz.muni.fi.pv168.common.DBUtils;
import cz.muni.fi.pv168.exceptions.EntityNotFoundException;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.sql.DataSource;

import static java.time.Month.MARCH;
import static org.hamcrest.CoreMatchers.*;

/**
 * @author Peter Hutta
 * @version 1.0  16.3.2016
 */
public class SinnerManagerImplTestWithWrongValues {

    private DataSource ds;
    private SinnerManagerImpl manager;
    private final static ZonedDateTime NOW
            = LocalDateTime.of(2016, MARCH, 29, 14, 00).atZone(ZoneId.of("UTC"));

    private static DataSource prepareDataSource() {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:sinnerMgrWrong-test");
        ds.setCreateDatabase("create");
        return ds;
    }

    private static Clock prepareClockMock(ZonedDateTime now) {
        return Clock.fixed(now.toInstant(), now.getZone());
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
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
    public void testCreateSinnerNullSinner() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(anyOf(containsString("null"), containsString("Null")));
        manager.createSinner(null);
    }

    @Test
    public void testCreateSinnerSetId() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "killed five babies", null, true);
        sinner.setId(5L);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(anyOf(containsString("id"), containsString("Id")));
        manager.createSinner(sinner);
    }

    @Test
    public void testCreateSinnerNullFirstName() throws Exception {
        Sinner sinner = newSinner(null, "Doe", "murder", null, true);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(anyOf(allOf(containsString("first"), containsString("Null")),
                allOf(containsString("First"), containsString("null"))));
        manager.createSinner(sinner);
    }

    @Test
    public void testCreateSinnerEmptyFirstName() throws Exception {
        Sinner sinner = newSinner("", "Doe", "murder", null, true);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(anyOf(allOf(containsString("First"), containsString("empty")),
                allOf(containsString("first"), containsString("Empty"))));
        manager.createSinner(sinner);
    }

    @Test
    public void testCreateSinnerNullLastName() throws Exception {
        Sinner sinner = newSinner("John", null, "murder", null, true);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(anyOf(allOf(containsString("Last"), containsString("null")),
                allOf(containsString("last"), containsString("Null"))));
        manager.createSinner(sinner);
    }

    @Test
    public void testCreateSinnerEmptyLastName() throws Exception {
        Sinner sinner = newSinner("John", "", "murder", null, true);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(anyOf(allOf(containsString("Last"), containsString("empty")),
                allOf(containsString("last"), containsString("Empty"))));
        manager.createSinner(sinner);
    }

    @Test
    public void testCreateSinnerWithOutdatedReleaseDate() throws Exception{
        Sinner sinner = newSinner("John", "Black", "murder", NOW.toLocalDate().minusDays(1), false);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("Release"), containsString("date")));
        manager.createSinner(sinner);
    }


    @Test
    public void testCreateSinnerNullDateFalseContract() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "murder", null, false);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(anyOf(allOf(containsString("date"), containsString("signed")),
                allOf(containsString("Date"), containsString("signed")), allOf(containsString("date"), containsString("Signed"))));
        manager.createSinner(sinner);
    }

    @Test
    public void testCreateSinnerSetDateTrueContract() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "murder", LocalDate.of(2125, 12, 27), true);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(anyOf(allOf(containsString("date"), containsString("signed")),
                allOf(containsString("Date"), containsString("signed")), allOf(containsString("date"), containsString("Signed"))));
        manager.createSinner(sinner);
    }

    @Test
    public void testUpdateSinnerNullSinner() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(anyOf(containsString("null"), containsString("Null")));
        manager.updateSinner(null);
    }

    @Test
    public void testUpdateSinnerNullId() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "killed five babies", null, true);

        sinner.setId(null);
        thrown.expect(IllegalArgumentException.class);
        manager.updateSinner(sinner);
    }

    @Test
    public void testUpdateSinnerNullFirstName() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "killed five babies", null, true);
        manager.createSinner(sinner);

        sinner.setFirstName(null);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(anyOf(allOf(containsString("First"), containsString("null")),
                allOf(containsString("first"), containsString("Null"))));
        manager.updateSinner(sinner);
    }

    @Test
    public void testUpdateSinnerEmptyFirstName() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "killed five babies", null, true);
        manager.createSinner(sinner);

        sinner.setFirstName("");
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(anyOf(allOf(containsString("First"), containsString("empty")),
                allOf(containsString("first"), containsString("Empty"))));
        manager.updateSinner(sinner);
    }

    @Test
    public void testUpdateSinnerNullLastName() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "killed five babies", null, true);
        manager.createSinner(sinner);

        sinner.setLastName(null);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(anyOf(allOf(containsString("Last"), containsString("null")),
                allOf(containsString("last"), containsString("Null"))));
        manager.updateSinner(sinner);
    }

    @Test
    public void testUpdateSinnerEmptyLastName() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "killed five babies", null, true);
        manager.createSinner(sinner);

        sinner.setLastName("");
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(anyOf(allOf(containsString("Last"), containsString("empty")),
                allOf(containsString("last"), containsString("Empty"))));
        manager.updateSinner(sinner);
    }

    @Test
    public void testUpdateSinnerWithOutdatedReleaseDate() throws Exception{
        Sinner sinner = newSinner("John", "Black", "murder", NOW.toLocalDate().plusDays(1), false);
        manager.createSinner(sinner);

        sinner.setReleaseDate(NOW.toLocalDate().minusDays(1));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("Release"), containsString("date")));
        manager.updateSinner(sinner);
    }

    @Test
    public void testUpdateSinnerSetDateTrueContract() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "killed five babies", null, true);
        manager.createSinner(sinner);

        sinner.setReleaseDate(LocalDate.of(2035, 4, 1));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(anyOf(allOf(containsString("date"), containsString("signed")),
                allOf(containsString("Date"), containsString("signed")), allOf(containsString("date"), containsString("Signed"))));
        manager.updateSinner(sinner);
    }

    @Test
    public void testUpdateSinnerNullDateFalseContract() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "killed five babies", null, true);
        manager.createSinner(sinner);

        sinner.setReleaseDate(null);
        sinner.setSignedContractWithDevil(false);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(anyOf(allOf(containsString("date"), containsString("signed")),
                allOf(containsString("Date"), containsString("signed")),allOf(containsString("date"), containsString("Signed"))));
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
        thrown.expect(IllegalArgumentException.class); //jak tedy?
        thrown.expectMessage(anyOf(allOf(containsString("id"), containsString("null")),allOf(containsString("Id"),
                containsString("null")), allOf(containsString("id"), containsString("Null"))));
        manager.deleteSinner(sinner);
    }

    @Test
    public void testDeleteSinnerSetId() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "murder", null, true);

        sinner.setId(2L);
        thrown.expect(EntityNotFoundException.class);
        manager.deleteSinner(sinner);
    }

    @Test
    public void testFindSinnerByIdNullId() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(anyOf(containsString("null"), containsString("Null")));
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
