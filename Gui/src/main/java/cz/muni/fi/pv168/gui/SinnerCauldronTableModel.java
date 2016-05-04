package cz.muni.fi.pv168.gui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Hutta
 * @version 1.0  3.5.2016
 */
public class SinnerCauldronTableModel extends AbstractTableModel {

    private List<Relation> relations = new ArrayList<Relation>();

    @Override
    public int getRowCount() {
        return relations.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Relation relation = relations.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return relation.getSinnerId();
            case 1:
                return relation.getFullName();
            case 2:
                return relation.getCauldronId();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Sinner Id";
            case 1:
                return "Full Name";
            case 2:
                return "Cauldron Id";
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void addRelation(Relation cauldron) {
        relations.add(cauldron);
        int lastRow = relations.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public void removeRelation(long sinnerId) {
        relations.removeIf((Relation c) -> c.getSinnerId() == sinnerId);
        fireTableRowsDeleted(relations.size(),relations.size());
    }
}
