package cz.muni.fi.pv168.gui;

import cz.muni.fi.pv168.Sinner;
import org.apache.derby.client.am.DateTime;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cechy on 03.05.2016.
 */
public class SinnerTableModel extends AbstractTableModel {

    private List<Sinner> sinners = new ArrayList<Sinner>();

    @Override
    public int getRowCount() {
        return sinners.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Sinner sinner = sinners.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return sinner.getId();
            case 1:
                return sinner.getFirstName();
            case 2:
                return sinner.getLastName();
            case 3:
                return sinner.getSin();
            case 4:
                return sinner.getReleaseDate();
            case 5:
                return sinner.isSignedContractWithDevil();
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
                return "First Name";
            case 2:
                return "Last Name";
            case 3:
                return "Sin";
            case 4:
                return "Release Date";
            case 5:
                return "Signed contract with devil";
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Sinner sinner = sinners.get(rowIndex);
        switch (columnIndex) {
            case 0:
                sinner.setId(Long.parseLong((String)aValue));
                break;
            case 1:
                sinner.setFirstName((String)aValue);
                break;
            case 2:
                sinner.setLastName((String)aValue);
                break;
            case 3:
                sinner.setSin((String)aValue);
                break;
            case 4:
                sinner.setReleaseDate(LocalDate.parse((String)aValue));
                break;
            case 5:
                sinner.setSignedContractWithDevil((boolean)aValue);
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
            case 4:
            case 5:
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
                return String.class;
            case 4:
                return LocalDate.class;
            case 5:
                return Boolean.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void addSinner(Sinner sinner) {
        sinners.add(sinner);
        int lastRow = sinners.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public void removeSinner(long id) {
        sinners.removeIf((Sinner c) -> c.getId() == id);
        fireTableRowsDeleted(sinners.size(), sinners.size());
    }

    public List<Sinner> getAllSinners() {
        return sinners;
    }


}
