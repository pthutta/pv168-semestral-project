package cz.muni.fi.pv168;

/**
 * @author Peter Hutta
 * @version 1.0  26.2.2016
 */
public class Cauldron {
    private Long id;
    private int capacity;
    private int waterTemperature;
    private int hellFloor;

    public Cauldron() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if(this.id != null){
            throw new IllegalArgumentException("Id once set cannot be changed");
        }
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        // udelat capacity nemenou, potom co uz byla jendou nastavena
        /*
        if (this.capacity != capacity){
            throw new IllegalArgumentException("capacity cannot be changed");
        }
        */
        this.capacity = capacity;
    }

    public int getWaterTemperature() {
        return waterTemperature;
    }

    public void setWaterTemperature(int waterTemperature) {
        this.waterTemperature = waterTemperature;
    }

    public int getHellFloor() {
        return hellFloor;
    }

    public void setHellFloor(int hellFloor) {
        this.hellFloor = hellFloor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cauldron cauldron = (Cauldron) o;

        if (capacity != cauldron.capacity) return false;
        if (waterTemperature != cauldron.waterTemperature) return false;
        if (hellFloor != cauldron.hellFloor) return false;
        return id != null ? id.equals(cauldron.id) : cauldron.id == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + capacity;
        result = 31 * result + waterTemperature;
        result = 31 * result + hellFloor;
        return result;
    }
}
