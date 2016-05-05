package cz.muni.fi.pv168.gui;

import cz.muni.fi.pv168.Cauldron;
import cz.muni.fi.pv168.Main;
import cz.muni.fi.pv168.Sinner;
import cz.muni.fi.pv168.exceptions.IllegalEntityException;
import cz.muni.fi.pv168.exceptions.ServiceFailureException;
import cz.muni.fi.pv168.impl.CauldronManagerImpl;
import cz.muni.fi.pv168.impl.HellManagerImpl;
import cz.muni.fi.pv168.impl.SinnerManagerImpl;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.DatePickerCellEditor;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Created by cechy on 26.04.2016.
 */
public class MainForm {
    private CauldronManagerImpl cauldronManager;
    private HellManagerImpl hellManager;
    private SinnerManagerImpl sinnerManager;

    private JPanel panel1;
    private JButton sinnersButton;
    private JButton cauldronsButton;
    private JButton exitButton;
    private JTabbedPane tabbedPane1;
    private JTable sinnersTable;
    private JButton addSinnerButton;
    private JButton deleteSinnerButton;
    private JTextField textFieldFirstName;
    private JTextField textFieldSin;
    private JTextField textFieldLastName;
    private JCheckBox signedContractWithDevilCheckBox;
    private JXDatePicker releaseDate;
    private JTable cauldronsTable;
    private JButton deleteCauldronButton;
    private JTable sinnerCauldronTable;
    private JButton releaseSinnerButton;
    private JButton boilSinnerButton;
    private JTextField textFieldCapacity;
    private JTextField textFieldWaterTemperature;
    private JTextField textFieldHellFloor;
    private JButton addCauldronButton;
    private JPanel MainJPanel;
    private JLabel correctionLabelSinner;
    private JLabel correctionLabelCauldron;
    private JLabel infoLabelSinner;
    private JLabel infoLabelCauldron;

    public MainForm() {

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });
        signedContractWithDevilCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(signedContractWithDevilCheckBox.isSelected()){
                    releaseDate.setEnabled(false);
                    releaseDate.setDate(null);
                }
                if(!signedContractWithDevilCheckBox.isSelected()) releaseDate.setEnabled(true);
            }
        });
        addSinnerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //data correctness check
                correctionLabelSinner.setText("Correct");
                correctionLabelSinner.setForeground(Color.GREEN);

                if(textFieldFirstName.getText().isEmpty()){
                    correctionLabelSinner.setText("Please input first name");
                    correctionLabelSinner.setForeground(Color.RED);
                }
                if(textFieldLastName.getText().isEmpty()){
                    correctionLabelSinner.setText("Please input last name");
                    correctionLabelSinner.setForeground(Color.RED);
                }
                if(textFieldSin.getText().isEmpty()){
                    correctionLabelSinner.setText("Please input sin");
                    correctionLabelSinner.setForeground(Color.RED);
                }
                if (!signedContractWithDevilCheckBox.isSelected() && (releaseDate.getDate() == null)){
                    correctionLabelSinner.setText("Please input signed contract or release date");
                    correctionLabelSinner.setForeground(Color.RED);
                }

                //add Sinner
                try {
                    Sinner sinner = new Sinner();
                    sinner.setFirstName(textFieldFirstName.getText());
                    sinner.setLastName(textFieldLastName.getText());
                    sinner.setSin(textFieldSin.getText());
                    if(releaseDate.getDate() != null)
                        sinner.setReleaseDate(releaseDate.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    sinner.setSignedContractWithDevil(signedContractWithDevilCheckBox.isSelected());

                    sinnerManager.createSinner(sinner);

                    SinnerTableModel model = (SinnerTableModel) sinnersTable.getModel();
                    model.addSinner(sinner);
                    refreshSinnerCauldronTable((SinnerCauldronTableModel)sinnerCauldronTable.getModel());
                }catch(NumberFormatException ex){
                    correctionLabelCauldron.setText("Cant parse given input: " + ex.getMessage());
                    correctionLabelCauldron.setForeground(Color.RED);
                }
            }
        });
        addCauldronButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //data correctness check
                correctionLabelCauldron.setText("Correct");
                correctionLabelCauldron.setForeground(Color.GREEN);

                if(textFieldCapacity.getText().isEmpty()){
                    correctionLabelCauldron.setText("Please input capacity");
                    correctionLabelCauldron.setForeground(Color.RED);
                    return;
                }
                if(textFieldWaterTemperature.getText().isEmpty()){
                    correctionLabelCauldron.setText("Please input water temperature");
                    correctionLabelCauldron.setForeground(Color.RED);
                    return;
                }
                if(textFieldHellFloor.getText().isEmpty()){
                    correctionLabelCauldron.setText("Please input hell floor");
                    correctionLabelCauldron.setForeground(Color.RED);
                    return;
                }

                //add cauldron
                try {
                    Cauldron cauldron = new Cauldron();
                    cauldron.setCapacity(Integer.parseInt(textFieldCapacity.getText()));
                    cauldron.setHellFloor(Integer.parseInt(textFieldHellFloor.getText()));
                    cauldron.setWaterTemperature(Integer.parseInt(textFieldWaterTemperature.getText()));

                    cauldronManager.createCauldron(cauldron);

                    CauldronTableModel model = (CauldronTableModel) cauldronsTable.getModel();
                    model.addCauldron(cauldron);
                }catch(NumberFormatException ex){
                    correctionLabelCauldron.setText("Cant parse given input: " + ex.getMessage());
                    correctionLabelCauldron.setForeground(Color.RED);
                }
            }
        });
        deleteCauldronButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = cauldronsTable.getSelectedRow();
                if(selectedRow != -1) {
                    try {
                        long id = (long) cauldronsTable.getValueAt(selectedRow, 0);
                        cauldronManager.deleteCauldron(cauldronManager.findCauldronById(id));
                        CauldronTableModel model = (CauldronTableModel) cauldronsTable.getModel();
                        model.removeCauldron(id);
                    } catch (ServiceFailureException ex) {
                        correctionLabelCauldron.setText("Cannot delete cauldron");
                        correctionLabelCauldron.setForeground(Color.RED);
                    }
                }
            }
        });
        releaseSinnerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = sinnerCauldronTable.getSelectedRow();
                if(selectedRow != -1) {
                    try {
                        long sinnerId = (long) sinnerCauldronTable.getValueAt(selectedRow, 0);
                        hellManager.releaseSinnerFromCauldron(sinnerManager.findSinnerById(sinnerId));
                        SinnerCauldronTableModel model = (SinnerCauldronTableModel) sinnerCauldronTable.getModel();
                        model.releaseSinner(sinnerId);
                    } catch (ServiceFailureException ex) {
                        correctionLabelCauldron.setText("Cannot delete relation");
                        correctionLabelCauldron.setForeground(Color.RED);
                    } catch (IllegalArgumentException ex) {
                        correctionLabelCauldron.setText(ex.getMessage());
                        correctionLabelCauldron.setForeground(Color.RED);
                    }
                }
            }
        });
        deleteSinnerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = sinnersTable.getSelectedRow();
                if(selectedRow != -1) {
                    try {
                        long id = (long) sinnersTable.getValueAt(selectedRow, 0);
                        sinnerManager.deleteSinner(sinnerManager.findSinnerById(id));
                        SinnerTableModel model = (SinnerTableModel) sinnersTable.getModel();
                        model.removeSinner(id);
                        refreshSinnerCauldronTable((SinnerCauldronTableModel)sinnerCauldronTable.getModel());
                    } catch (ServiceFailureException ex) {
                        correctionLabelCauldron.setText("Cannot delete sinner");
                        correctionLabelCauldron.setForeground(Color.RED);
                    }
                }
            }
        });

        sinnersTable.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                SinnerTableModel model = (SinnerTableModel) sinnersTable.getModel();
                try {
                    for (Sinner sinner : model.getAllSinners()) {
                        sinnerManager.updateSinner(sinner);
                    }
                } catch (IllegalArgumentException ex) {
                    correctionLabelSinner.setText(ex.getMessage());
                    correctionLabelSinner.setForeground(Color.RED);
                }
                refreshSinnerCauldronTable((SinnerCauldronTableModel)sinnerCauldronTable.getModel());
            }
        });
        boilSinnerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedCauldronRow = cauldronsTable.getSelectedRow();
                if (selectedCauldronRow == -1) {
                    correctionLabelCauldron.setText("Please, select cauldron.");
                    correctionLabelCauldron.setForeground(Color.RED);
                }

                int selectedSinnerRow = sinnerCauldronTable.getSelectedRow();
                if (selectedSinnerRow == -1) {
                    correctionLabelCauldron.setText("Please, select sinner");
                    correctionLabelCauldron.setForeground(Color.RED);
                }

                try {
                    long sinnerId = (long) sinnerCauldronTable.getValueAt(selectedSinnerRow, 0);
                    long cauldronId = (long) cauldronsTable.getValueAt(selectedCauldronRow, 0);

                    Sinner sinner = sinnerManager.findSinnerById(sinnerId);
                    Cauldron cauldron = cauldronManager.findCauldronById(cauldronId);

                    hellManager.boilSinnerInCauldron(sinner, cauldron);
                    SinnerCauldronTableModel model = (SinnerCauldronTableModel)sinnerCauldronTable.getModel();
                    Relation relation = new Relation(sinner.getId(), sinner.getFirstName() + " " + sinner.getLastName(), cauldronId);
                    model.updateRelation(relation);

                } catch (ServiceFailureException | IllegalEntityException ex ) {
                    correctionLabelCauldron.setText("Cannot boil sinner in cauldron: " + ex.getMessage());
                    correctionLabelCauldron.setForeground(Color.RED);
                }
            }
        });
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Hell Manager");
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setContentPane(new MainForm().MainJPanel);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    private void createUIComponents() {
        releaseDate = new JXDatePicker();

        DataSource dataSource = Main.createMemoryDatabase();
        cauldronManager = new CauldronManagerImpl(dataSource);
        hellManager = new HellManagerImpl(dataSource);
        sinnerManager = new SinnerManagerImpl(dataSource, Clock.systemDefaultZone());

        CauldronTableModel cauldronTableModel = new CauldronTableModel();
        cauldronManager.findAllCauldrons().forEach(cauldronTableModel::addCauldron);
        cauldronsTable = new JTable(cauldronTableModel);

        SinnerTableModel sinnerTableModel = new SinnerTableModel();
        sinnerManager.findAllSinners().forEach(sinnerTableModel::addSinner);
        sinnersTable = new JTable(sinnerTableModel);
        sinnersTable.setDefaultRenderer(LocalDate.class, FormatRenderer.getDateTimeRenderer());
        sinnersTable.getColumnModel().getColumn(4).setCellEditor(new DatePickerCellEditor());

        SinnerCauldronTableModel sinCaulTableModel = new SinnerCauldronTableModel();
        refreshSinnerCauldronTable(sinCaulTableModel);
        sinnerCauldronTable = new JXTable(sinCaulTableModel);
    }

    private void refreshSinnerCauldronTable(SinnerCauldronTableModel sinCaulTableModel) {
        sinCaulTableModel.clearTable();
        for (Sinner sinner : sinnerManager.findAllSinners()) {
            Cauldron cauldron = hellManager.findCauldronWithSinner(sinner);
            Long cauldronId = null;
            if (cauldron != null) {
                cauldronId = cauldron.getId();
            }
            Relation relation = new Relation(sinner.getId(), sinner.getFirstName() + " " + sinner.getLastName(), cauldronId);
            sinCaulTableModel.addRelation(relation);
        }
    }
}
