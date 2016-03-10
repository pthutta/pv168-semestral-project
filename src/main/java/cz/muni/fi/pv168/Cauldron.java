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
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
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
}
