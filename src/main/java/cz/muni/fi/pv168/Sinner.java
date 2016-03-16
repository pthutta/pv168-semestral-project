package cz.muni.fi.pv168;

import java.time.LocalDate;

/**
 * This class represents Sinner. Sinner has first name, last name, his sin. In case
 * he hasn't signed contract with devil he has release date from hell. He could be
 * boiled in a cauldron.
 *
 * @author Peter Hutta
 * @version 1.0  26.2.2016
 */
public class Sinner {
    private Long id;
    private String firstName;
    private String lastName;
    private String sin;
    private LocalDate releaseDate;
    private boolean signedContractWithDevil;

    public Sinner() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getSin() {
        return sin;
    }

    public void setSin(String sin) {
        this.sin = sin;
    }

    public boolean isSignedContractWithDevil() {
        return signedContractWithDevil;
    }

    public void setSignedContractWithDevil(boolean signedContractWithDevil) {
        this.signedContractWithDevil = signedContractWithDevil;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sinner sinner = (Sinner) o;

        if (signedContractWithDevil != sinner.signedContractWithDevil) return false;
        if (id != null ? !id.equals(sinner.id) : sinner.id != null) return false;
        if (!firstName.equals(sinner.firstName)) return false;
        if (!lastName.equals(sinner.lastName)) return false;
        if (!sin.equals(sinner.sin)) return false;
        return releaseDate != null ? releaseDate.equals(sinner.releaseDate) : sinner.releaseDate == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + sin.hashCode();
        result = 31 * result + (releaseDate != null ? releaseDate.hashCode() : 0);
        result = 31 * result + (signedContractWithDevil ? 1 : 0);
        return result;
    }
}
