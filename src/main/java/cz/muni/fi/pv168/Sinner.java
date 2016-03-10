package cz.muni.fi.pv168;

import java.time.LocalDate;

/**
 * @author Peter Hutta
 * @version 1.0  26.2.2016
 */
public class Sinner {
    private Long Id = null;
    private String firstName = null;
    private String lastName = null;
    private String reason = null;
    private LocalDate releaseDate;
    private boolean signedConstractWithDevil;

    public Sinner() {
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isSignedConstractWithDevil() {
        return signedConstractWithDevil;
    }

    public void setSignedConstractWithDevil(boolean signedConstractWithDevil) {
        this.signedConstractWithDevil = signedConstractWithDevil;
    }
}
