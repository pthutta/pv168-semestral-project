package cz.muni.fi.pv168.impl;

import cz.muni.fi.pv168.Sinner;
import cz.muni.fi.pv168.SinnerManager;
import java.time.LocalDate;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.*;

/**
 * @author Peter Hutta
 * @version 1.0  16.3.2016
 */
public class SinnerManagerImplTestWithWrongValues {

    private SinnerManager manager;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        manager = new SinnerManagerImpl();
    }

    @Test
    public void testCreateSinner() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("null"));
        manager.createSinner(null);

        Sinner sinner = newSinner("John", "Doe", "killed five babies", null, true);
        sinner.setId(5L);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("id"));
        manager.createSinner(sinner);

        sinner = newSinner(null, "Doe", "murder", null, true);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("first"), containsString("null")));
        manager.createSinner(sinner);

        sinner = newSinner("", "Doe", "murder", null, true);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("first"), containsString("empty")));
        manager.createSinner(sinner);

        sinner = newSinner("John", null, "murder", null, true);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("last"), containsString("null")));
        manager.createSinner(sinner);

        sinner = newSinner("John", "", "murder", null, true);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("last"), containsString("empty")));
        manager.createSinner(sinner);

        sinner = newSinner("John", "Doe", "murder", null, false);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("date"), containsString("signed")));
        manager.createSinner(sinner);

        sinner = newSinner("John", "Doe", "murder", LocalDate.of(2125, 12, 27), true);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("date"), containsString("signed")));
        manager.createSinner(sinner);
    }

    @Test
    public void testUpdateSinner() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("null"));
        manager.updateSinner(null);

        Sinner sinner = newSinner("John", "Doe", "killed five babies", null, true);
        manager.createSinner(sinner);
        Long sinnerId = sinner.getId();

        sinner = manager.findSinnerById(sinnerId);
        sinner.setId(null);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("id"), containsString("null")));
        manager.updateSinner(sinner);

        sinner = manager.findSinnerById(sinnerId);
        sinner.setId(sinnerId + 1);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("id"));
        manager.updateSinner(sinner);

        sinner = manager.findSinnerById(sinnerId);
        sinner.setFirstName(null);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("first"), containsString("null")));
        manager.updateSinner(sinner);

        sinner = manager.findSinnerById(sinnerId);
        sinner.setFirstName("");
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("first"), containsString("empty")));
        manager.updateSinner(sinner);

        sinner = manager.findSinnerById(sinnerId);
        sinner.setLastName(null);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("last"), containsString("null")));
        manager.updateSinner(sinner);

        sinner = manager.findSinnerById(sinnerId);
        sinner.setLastName("");
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("last"), containsString("empty")));
        manager.updateSinner(sinner);

        sinner = manager.findSinnerById(sinnerId);
        sinner.setReleaseDate(LocalDate.of(2035, 4, 1));
        sinner.setSignedContractWithDevil(true);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("date"), containsString("signed")));
        manager.updateSinner(sinner);

        sinner = manager.findSinnerById(sinnerId);
        sinner.setReleaseDate(null);
        sinner.setSignedContractWithDevil(false);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("date"), containsString("signed")));
        manager.updateSinner(sinner);
    }

    @Test
    public void testDeleteSinner() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "murder", null, true);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("null"));
        manager.deleteSinner(null);

        sinner.setId(null);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("id"), containsString("null")));
        manager.deleteSinner(sinner);

        sinner.setId(2L);
        thrown.expect(IllegalArgumentException.class);
        manager.deleteSinner(sinner);
    }

    @Test
    public void testFindSinnerById() throws Exception {
        Sinner sinner = newSinner("John", "Doe", "murder", null, true);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("null"));
        manager.createSinner(null);

        sinner.setId(null);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsString("id"), containsString("null")));
        manager.createSinner(sinner);

        sinner.setId(1L);
        thrown.expect(IllegalArgumentException.class);
        manager.createSinner(sinner);
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
