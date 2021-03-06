package cz.muni.fi.pv168.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Hutta
 * @version 1.0  3.5.2016
 */
public class SinnerCauldronTableModel extends AbstractTableModel {

    final static Logger log = LoggerFactory.getLogger(SinnerCauldronTableModel.class);
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
                return relation.getSinnerName();
            case 2:
                return relation.getCauldronId();
            default:
                log.error("Invalid column index");
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Sinner Id";
            case 1:
                return "Sinner Name";
            case 2:
                return "Cauldron Id";
            default:
                log.error("Invalid column index");
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void addRelation(Relation relation) {
        relations.add(relation);
        int lastRow = relations.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public void releaseSinner(Long sinnerId) {
        for (Relation rel : relations) {
            if (rel.getSinnerId().equals(sinnerId)) {
                rel.setCauldronId(null);
                break;
            }
        }
        fireTableDataChanged();
    }

    public void updateRelation(Relation relation) {
        for (Relation rel : relations) {
                if (rel.getSinnerId() == relation.getSinnerId()) {
                    rel.setSinnerName(relation.getSinnerName());
                    rel.setCauldronId(relation.getCauldronId());
                    break;
                }
        }
        fireTableDataChanged();
    }

    public void clearTable() {
        relations.clear();
        fireTableDataChanged();
    }
}
