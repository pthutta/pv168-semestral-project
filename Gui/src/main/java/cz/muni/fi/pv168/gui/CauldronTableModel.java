package cz.muni.fi.pv168.gui;

import cz.muni.fi.pv168.Cauldron;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;

/**
 * Created by cechy on 03.05.2016.
 */
public class CauldronTableModel extends AbstractTableModel {

    private List<Cauldron> cauldrons = new ArrayList<Cauldron>();

    @Override
    public int getRowCount() {
        return cauldrons.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Cauldron cauldron = cauldrons.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return cauldron.getId();
            case 1:
                return cauldron.getCapacity();
            case 2:
                return cauldron.getWaterTemperature();
            case 3:
                return cauldron.getHellFloor();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Id";
            case 1:
                return "Capacity";
            case 2:
                return "Water Temperature";
            case 3:
                return "Hell Floor";
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Cauldron cauldron = cauldrons.get(rowIndex);
        switch (columnIndex) {
            case 0:
                cauldron.setId((Long)aValue);
                break;
            case 1:
                cauldron.setCapacity((int)aValue);
                break;
            case 2:
                cauldron.setWaterTemperature((int)aValue);
                break;
            case 3:
                cauldron.setHellFloor((int)aValue);
                break;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return false;
            case 1:
            case 2:
            case 3:
                return true;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Long.class;
            case 1:
            case 2:
            case 3:
                return Integer.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void addCauldron(Cauldron cauldron) {
        cauldrons.add(cauldron);
        int lastRow = cauldrons.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public void removeCauldron(long id) {
        cauldrons.removeIf((Cauldron c) -> c.getId() == id);
        fireTableRowsDeleted(cauldrons.size(), cauldrons.size());
    }
}
